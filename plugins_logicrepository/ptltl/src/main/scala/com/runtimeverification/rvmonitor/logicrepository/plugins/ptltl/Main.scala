// PTLTL plugin

//import language.implicitConversions

import scala.util.parsing.combinator._
import scala.math._
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptltl._
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptltl.AssignmentHelpers._
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptltl.FSMHelpers._
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptltl.PTLTLHelpers._  // Helpers (found in this file)

package com.runtimeverification.rvmonitor.logicrepository.plugins.ptltl {

  object PTLTL {
    // Go all the way from a PTLTL String to a FSM String
    def mkFSM(s: String) : String = {
      val pbv = formulaToAssnBV(Parse.parseFormula(s)).toParallelBitVector
      val bv = new BitVector(pbv)
      val fns = fillTransitions(fromBitVectors(getReachables(reachables(bv))))
      new FSM(fns).toString
    }
  }

  sealed abstract class TemporalOp extends BooleanFormula {
    var depth: Int = Int.MaxValue
    var temporality: Int = Int.MaxValue

    def setDepth(i: Int) : TemporalOp = {
      this.depth = i
      this
    }

    def setTemporality(i: Int) : TemporalOp = {
      this.temporality = i
      this
    }

    override def simplify : Formula =
      Formula.simplifyAll(ptltlSimplify, this)

    def toAssignment = {
      var pos: OutPos = Neither
      var e: Formula = this match {
        case Since(left, right) =>
          pos = Before; Or(List(And(List(left, this)), right))
        case Previously(arg) => pos = After; arg
        case _ =>
          throw new RuntimeException("toAssignment called before simplifying")

      }
      Assignment(this.temporality, toRefs(e), pos)
    }

  }

  case class Previously(arg: Formula) extends TemporalOp
  case class Since(left: Formula, right: Formula) extends TemporalOp

  // The following are simplified to instances of the above
  case class Always(arg: Formula) extends TemporalOp
  case class Eventually(arg: Formula) extends TemporalOp


/* Helpers */
  object PTLTLHelpers {
    def ptltlSimplify : PartialFunction[Formula, Formula] =
      new PartialFunction[Formula, Formula] {
        def apply(form: Formula) : Formula =
          form match {
            case Since(left, True) => True
            case Since(left, right) => Since(left.simplify, right.simplify)

            case Previously(arg) => Previously(arg.simplify)

            case Eventually(arg) => Since(True, arg)
            case Always(arg) => Not(Eventually(Not(arg)))

          }
        def isDefinedAt(form: Formula) : Boolean =
          form.isInstanceOf[TemporalOp]
      }

    def extractEvents(form: Formula) : List[Event] =
      (form match {
        case And(args) => args.flatMap( x => extractEvents(x) )
        case Or(args) =>  args.flatMap( x => extractEvents(x) )

        case Not(arg) => extractEvents(arg)

        case Xor(left, right) => extractEvents(left) ::: extractEvents(right)

        case Previously(arg) => extractEvents(arg)
        case Since(left, right) => extractEvents(left) ::: extractEvents(right)

        case Event(n) => List(Event(n))

        case True => List()
        case False => List()

        case Reference(i) => List()

        case _ => throw new RuntimeException("extractEvents called before simplify")
      }).distinct


    def tagDepths(form: Formula, d: Int) : Formula = {
      val next = d+1
      var ret: Formula = form match {
        case Since(l, r) => Since(tagDepths(l, next), tagDepths(r, next)).setDepth(d)
        case Previously(a) => Previously(tagDepths(a, next)).setDepth(d)
        case Not(a)        => Not(tagDepths(a , next))
        case And(args)     => And(args.map( x => tagDepths(x, next)))
        case Or(args)      => Or(args.map( x => tagDepths(x, next)))
        case Xor(l, r)     => Xor(tagDepths(l, next), tagDepths(r, next))
        case _             => form
      }
      ret
    }

    // The below preserves references, i.e. the pointers for the members of the
    // resultant list point to the same spot in memory as in the Formula
    def extractTempOps(form: Formula) : List[TemporalOp] =
      form match {
        case tmpop: TemporalOp => tmpop match {
          case Previously(a) => tmpop :: extractTempOps(a)
          case Since(l, r)    => (tmpop :: extractTempOps(l)) ::: extractTempOps(r)
          case _ => List()
        }

        case Not(a)    => extractTempOps(a)
        case And(args) => args.flatMap(extractTempOps)
        case Or(args)  =>  args.flatMap(extractTempOps)
        case Xor(l, r) => extractTempOps(l) ::: extractTempOps(r)
        case _ => List()
      }

    // Sort the temporals, and tag them with their temporality
    // Mutates each element of the list
    def tagTemporalities(temps: List[TemporalOp]) : List[TemporalOp] =
      for ( (t, i) <- temps.sortWith( (a, b) => a.depth > b.depth ).zipWithIndex )
      yield t.setTemporality(i)

    def toRefs(form: Formula) : Formula = {
      val ret = form match {
        case x: TemporalOp => x match {
          case Since(_, _) => Reference(x.temporality)
          case Previously(_) => Reference(x.temporality)
          case _ => throw new RuntimeException("toRefs called before simplify")
        }

        case Not(a) => Not(toRefs(a))
        case And(args) => And(args.map(x => toRefs(x)))
        case Or(args) => Or(args.map(x => toRefs(x)))
        case Xor(l, r) => Xor(toRefs(l), toRefs(r))
        case _ => form
      }
      ret.simplify
    }

    def formulaToAssnBV(form: Formula) : AssignmentBitVector = {
      val events = extractEvents(form)
      val ret = new AssignmentBitVector(events)
      val formWithDepths = tagDepths(form, 0)  // needed, tagDepths doesn't mutate

      val ts = extractTempOps(formWithDepths)
      tagTemporalities(ts).map( x =>
        ret.add(x.toAssignment))
      ret.exp = Output(toRefs(formWithDepths))
      ret
    }

    // Compute the new formula after seeing an event
    def eventOccurs(e: Event, form: Formula, bs: List[Boolean]) : Formula =
      form match {
        case And(args) => And(args.map( x => eventOccurs(e, x, bs) ))
        case Or(args) =>  Or(args.map( x => eventOccurs(e, x, bs) ))

        case Not(arg) => Not(eventOccurs(e, arg, bs))

        case Xor(left, right) =>
          Xor(eventOccurs(e, left, bs), eventOccurs(e, right, bs))

        case Previously(arg) => Previously(eventOccurs(e, arg, bs))
        case Since(left, right) =>
          Since(eventOccurs(e, left, bs), eventOccurs(e, right, bs))

        case Event(n) => if (Event(n) == e) True else False

        case True => True
        case False => False

        case Reference(i) => if (bs(i)) True else False

        case _ => throw new RuntimeException("eventOccurs called before simplify")
      }

    // Generate the next bitvector for when event occurs
    def readEvent(e: Event, bv: BitVector) : BitVector = {
      var ret = new BitVector(bv.pv)
      ret.out = eventOccurs(e, bv.pv.output.expression, bv.state).evaluate
      for ( a <- bv.pv.contents )
        ret.state = ret.state.set( a.index
                                 , eventOccurs(e, a.expression, bv.state).evaluate)
      ret
    }

    def reachables(bv: BitVector) : List[BitVector] =
      bvlRemoveDups(bv.pv.events.map( e => readEvent(e, bv) ))

    def reachablesWithE(bv: BitVector) : List[(BitVector,Event)] =
      bv.pv.events.map( e => (readEvent(e, bv), e) )

    // def fix[A]: ((A => A), A) => A = (f,x) =>
    //   if (f(x) == x) x
    //   else fix(f,f(x))

    def findReachables(left: List[BitVector], right: List[BitVector]) : List[BitVector] =
      right match {
        case r :: rs => if (isPresent(left, r)) findReachables(left, rs)
                        else findReachables(left ::: List(r), rs ::: reachables(r))
        case List() => bvlRemoveDups(left)
      }

    //
    def getReachables(right: List[BitVector]) : List[BitVector] =
      findReachables(List(), right)


    def fromBitVector(b: BitVector, i: Int) : FSMNode =
      FSMNode(i).setUid(b)

    def fromBitVectors(bs: List[BitVector]) : List[FSMNode] =
      for ( (b,i) <- bs.zipWithIndex )
      yield fromBitVector(b,i)

    def findFSMNode(fns: List[FSMNode], b: BitVector) : FSMNode =
      fns.find( x => x.uid == b ).get

    def fillTransitions(fns: List[FSMNode]) : List[FSMNode] = {
      var ret = fns
      for ( n <- ret
          ; (b,e) <- reachablesWithE(n.uid) )
        n.addTransition(Transition(e, findFSMNode(ret, b)))
      ret
    }


    // Find the start state, put at the head of the list
    // def startOnTop: List[BitVector] => List[BitVector] = bvs =>
    //   bvs match {
    //     case b :: bs => if (b.state == b.pv.contents.map( _ => false ))
    //                       b :: bs
    //                     else
    //                       startOnTop(bs ::: List(b))
    //   }


    implicit def wraplist[T](x: List[T]) : ListWrapper[T] = new ListWrapper(x)
    implicit def fromwraplist[T](x: ListWrapper[T]) : List[T] = x.in

  }




  // Parsing
  class PTLTLParser extends JavaTokenParsers {

    // For now, disregard event names
    def syntax : Parser[Formula] =
      rep("event" ~ event_name) ~> "ptltl" ~ ":" ~ formula ^^
        {case "ptltl"~":"~f => f}

    def formula : Parser[Formula] = (
      chainl1(formula2, "<->" ^^^ {(left, right) => IFF(left, right)})
    )

    def formula2 : Parser[Formula] = (
      chainl1(formula3, implies ^^^ {(left, right) => Implies(left, right)})
    )

    def formula3 : Parser[Formula] = (
      chainl1(formula4, or ^^^ {(left:Formula, right:Formula) => Or(List(left, right))})
    )

    def formula4 : Parser[Formula] = (
      chainl1(formula5, xor ^^^ {(left, right) => Xor(left, right)})
    )

    def formula5 : Parser[Formula] = (
      chainl1(formula6, and ^^^ {(left:Formula, right:Formula) => And(List(left, right))})
    )
    
    def formula6 : Parser[Formula] = (
      chainl1(formula7, "S" ^^^ {(left, right) => Since(left, right)})
    )

    def formula7 : Parser[Formula] = (
      "[*]" ~> formula7            ^^ (Always(_))         // always in the past
    | "<*>" ~> formula7            ^^ (Eventually(_))     // eventually in the past
    | "(*)" ~> formula7            ^^ (Previously(_))     // previously
    | not   ~> formula7            ^^ (Not(_))
    | term
    )
    
    def term = ( "true"     ^^ (_ => True)
               | "false"    ^^ (_ => False)
               | event_name ^^ (Event(_))
               | "(" ~> formula <~ ")"
               )

    def not  = "not" | "!"
    def and  = "/\\" | "and" | "&&"
    def or   = "\\/" | "or"  | "||"
    def xor  = "++" | "xor" | "^"
    def implies = "->" | "implies" | "=>"

    def event_name = """[A-Za-z]+""".r

  }

  object Parse extends PTLTLParser {
    def parseFormula(s: String) : Formula =
      parseAll(syntax, "event create event next ptltl: " + s).get.simplify
  }

}


// val form1: Formula =
//   Parse.parseFormula("(B and C) and (((*)((*) A)) and (D and (B and true))) ")
// val form2: Formula =
//   Parse.parseFormula("(B or C) or (((*)((*) A)) or (D and (B and (D and true)))) ")
// val form3: Formula =
//   Parse.parseFormula("(B and C) and (((*)((*) A)) and (D and B)) ")
// val form4: Formula =
//   Parse.parseFormula("((*) B) and ((*)((*) C))")
// val form5: Formula =
//   Parse.parseFormula("((*) B) S ((*)((*) C))")
// val form6: Formula =
//   Parse.parseFormula("(*) A")

// print(form1.toString + "\n")

// print(form2.toString + "\n")

// print("\n\nNow going for bit vector\n")
// print(formulaToAssnBV(form1))
// print("\n\nNow going for parallel bit vector\n")
// print(formulaToAssnBV(form1).toParallelBitVector)

// print("\n\nNow going for bit vector\n")
// print(formulaToAssnBV(form4))
// print("\n\nNow going for parallel bit vector\n")
// print(formulaToAssnBV(form4).toParallelBitVector)

// print(form5.toString + "\n")

// print("\n\nNow going for bit vector\n")
// print(formulaToAssnBV(form5))
// print("\n\nNow going for parallel bit vector\n")
// print(formulaToAssnBV(form5).toParallelBitVector)

// print("\n\n")
// print(extractEvents(form1).mkString(", "))
// print("\n" + List(0,1,2,3,4,5,6,7).set(2,Int.MaxValue).toString)

// var pbv1 = formulaToAssnBV(form5).toParallelBitVector
// pbv1.events = Event("default") :: pbv1.as.flatMap( x => extractEvents(x.expression) )
// var pbv1 = formulaToAssnBV(form4).toParallelBitVector
// pbv1.events = Event("default") :: pbv1.as.flatMap( x => extractEvents(x.expression) )
// var pbv1 = formulaToAssnBV(form6).toParallelBitVector
// pbv1.events = Event("default") :: pbv1.as.flatMap( x => extractEvents(x.expression) )

// print("\npbv: \n")
// print(pbv1.toString)
// print("\n")

// val bv1 = new BitVector(pbv1)


// print(bv1.toString)

// // print(bv1.pv.events.mkString("\nbv1's events:", ", ","\n"))
// // print(reachables(bv1).mkString("\nbv1's reachables:\n\t", "\n\t","\n"))

// val rs = findReachables(List(), reachables(bv1))
// print(rs.mkString("\nbv1's all reachables:\n\t", "\n\t","\n"))


// val fns = fromBitVectors(rs)
// //print(fns.mkString("\nFSMNodes: ", "\n", "\n"))


// val fnsT = fillTransitions(fns)
// //print(fnsT.mkString("\nFSMNodes: ", "\n", "\n"))

// val fsm = new FSM
// for (n <- fnsT)
//   fsm.addNode(n)
// fsm.mkValids

// print("\nFSM:\n")
// print(fsm.toString)

// print(rs.mkString("\nReachables:\n","\n","\n"))

// print("\n\nAll Done\n")




// print(readEvent(Event("something"),readEvent(Event("B"),readEvent(Event("C"),bv1))).toString)

// print("\n\n\t")
// print((readEvent(Event("C"),bv1) == readEvent(Event("B"),bv1)).toString)



// print("\n\nRemoving Dups:")
// print("\ntesting == ... ")
// print((readEvent(Event("C"),bv1) == readEvent(Event("C"),bv1)).toString)
// print("\ntesting distinct ... ")
// print((List( readEvent(Event("C"),bv1)
//            , readEvent(Event("C"),bv1)).distinct.length == 1).toString)
// print("\ntesting my distinct ... ")
// print((bvlRemoveDups(
//   List( readEvent(Event("C"),bv1), readEvent(Event("C"),bv1))).length == 1).toString)
// print("\ntesting isPresent ... ")
// print(isPresent(List( readEvent(Event("C"),bv1)), readEvent(Event("C"),bv1)).toString
    // )


// print("\n\nList equality: ")
// print((List(1,2,3) == List(1,2,3)).toString)


// Trailer stuff:


