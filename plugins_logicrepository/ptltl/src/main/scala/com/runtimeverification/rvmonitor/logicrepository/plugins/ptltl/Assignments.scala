import scala.math._
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptltl._
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptltl.AssignmentHelpers._

package com.runtimeverification.rvmonitor.logicrepository.plugins.ptltl {

  // References --- Use these to refer to entries of the bitvector
  case class Reference(index: Int) extends Formula {
    override def toString = "b[" + index + "]"
  }

  sealed abstract class OutPos
  case object Before extends OutPos
  case object After extends OutPos
  case object Neither extends OutPos  

  case class Assignment(index: Int, expression: Formula, pos: OutPos) {
    override def toString =
      if (this.pos == Output)
        "Output: " + expression.toString
      else
        mkref + " := " + expression.toString

    private def mkref : String =
      "b[" + index.toString + "]"      
  }
  
  class Output(override val expression: Formula)
       extends Assignment(Int.MaxValue, expression, Neither) 

  object Output {
	   def unapply(o: Output) : Option[Formula] = {	
		return Some(o.expression);
	   }
	   
	   def apply(expression: Formula) : Output = {
		 return new Output(expression);
	   }
	}


  // Must only contain types provided by booleanformula and the above Reference type
  // I may later try to enforce these at the type level
  class AssignmentBitVector(es: List[Event]) {
    var before: List[Assignment] = List()
    var after : List[Assignment] = List()
    var exp   : Assignment = null
    var events: List[Event] = Event("default") :: es

    def this(es: List[Event], as: List[Assignment]) = {
      this(es)
      as.map( x => this.add(x))
    }

  
    // Mutates self         
    def add(assn: Assignment) : Unit =
      assn match {
        case Assignment(_,_,Before) => before = before ::: List(assn)
        case Assignment(_,_,After)  => after = assn :: after
        case Output(_)  =>
          throw new RuntimeException("Trying to add another Output")
      }

    def toParallelBitVector: ParallelAssignmentBitVector = 
      new ParallelAssignmentBitVector(parallelize(before ::: (exp :: after)), events)
  
    override def toString =
      ( "Before: " + before.toString() + "\n"
      + "Output: " + exp.toString()  + "\n"
      + "After: " + after.toString() + "\n"
      )

  }


  class ParallelAssignmentBitVector(in: List[Assignment], es: List[Event]) {
    var contents: List[Assignment] = in.sortWith((a,b) => a.index < b.index).dropRight(1)
    var output: Assignment = in.sortWith((a,b) => a.index < b.index).last

    val as: List[Assignment] = in
  
    var events: List[Event] = es

    override def toString =
      ( events.mkString("Events: ", ", ", "\n")
      + contents.mkString("Parallel Assignments:\n\t", "\n\t", "")
      + "\n" + output.toString )
  }

  // Class representing the current state of the parallelAssgnBV
  class BitVector(in: ParallelAssignmentBitVector) {
    var state: List[Boolean] = in.contents.map( _ => false )
    var out: Boolean = false
    var pv: ParallelAssignmentBitVector = in

    def ==(bv: BitVector) : Boolean = 
      (bv.state == this.state) && (bv.out == this.out)

    def !=(bv: BitVector) : Boolean = 
      (bv.state != this.state) && (bv.out != this.out)
  
    override def toString =
      state.mkString("\nState: ",", ","") + "\nOut: " + out.toString
  }

  object AssignmentHelpers {

    def parallelize(as: List[Assignment]) : List[Assignment] = {
      var ret:List[Assignment] = as
      if (!(ret.length == 0))
        for (i <- ret.indices) {
          var a = ret(i)
          var relevants = ret.take(i)
          ret = ret.set(i, Assignment( a.index
                                     , backSubstitute(a.expression, relevants)
                                     , a.pos ))
        }
      ret
    }

    def backSubstitute(form: Formula, as: List[Assignment]) : Formula =
      if (as.length == 0) form else
        form match {

          case Reference(i) => if (as.exists( x => x.index == i ))
                                   as.find( x => x.index == i ).get.expression
                               else form

          case Not(a) => Not(backSubstitute(a, as))
          case And(args) => And(args.map( x => backSubstitute(x, as) ))
          case Or(args)  => Or(args.map( x => backSubstitute(x, as) ))
          case Xor(l, r) => Xor(backSubstitute(l, as), backSubstitute(r, as))

          case Event(_) => form

          case _ =>
            throw new RuntimeException("backSubstitute called on non-matching pattern")
      }

    // Because the scala provided one doesn't behave according to their documentation
    // ToDo: come up with a simple case, and submit a bug
    def bvlRemoveDups(ls: List[BitVector]) : List[BitVector] =
      ls match {
        case (bv :: bvs) => bv :: bvlRemoveDups(bvs.filterNot( x => bv == x ))
        case List() => List()
      }

    // Same as above
    def isPresent(bs: List[BitVector], b: BitVector): Boolean =  {
      for ( v <- bs )
        if (v == b) return true
      return false
    }
  
    implicit def wraplist[T]: List[T] => ListWrapper[T] = x => new ListWrapper(x)
    implicit def fromwraplist[T]: ListWrapper[T] => List[T] = x => x.in
  }


}
