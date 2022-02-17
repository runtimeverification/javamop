// Classes for Boolean Formulas. You can extend these for whatever logic you use
//   (see ptltl for an example)

// ToDo, create a specialization of PartialFunction, or change overall structure

import com.runtimeverification.rvmonitor.logicrepository.plugins.ptltl._
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptltl.BooleanFormulaHelpers._

package com.runtimeverification.rvmonitor.logicrepository.plugins.ptltl {

  abstract class Formula { // Perhaps later make a mix-in

    // Make sure to override me --- perhaps later find a way to enforce this
    // ToDo, clean up
    def simplify: Formula =
      Formula.simplifyAll(Formula.simplify, this)

    def evaluate: Boolean =
      this.simplify match {
        case True => true
        case False => false
        case _ =>
          throw new RuntimeException("unable to evaluate" + this.simplify.toString)
      }
  }

  object Formula {
    def simplifyAll(f: PartialFunction[Formula, Formula], form: Formula) : Formula =
      if (form == pfSimplify(f,form))
        form
      else simplifyAll(f, pfSimplify(f,form))

    private def pfSimplify(f: PartialFunction[Formula, Formula], form: Formula) : Formula =
      if (f.isDefinedAt(form))
        f(form)
      else form match {
        case _ => form
      }
    private def simplify: PartialFunction[Formula, Formula] = {
      new PartialFunction[Formula,Formula] {
        def isDefinedAt(form: Formula) : Boolean = true
        def apply(form: Formula) : Formula = form
      }
    }
  }


  
  case class Event(e: String) extends Formula {
    override def toString = e
  }

  case object True extends Formula
  case object False extends Formula


  abstract class BooleanFormula extends Formula {
    override def simplify: Formula =
      Formula.simplifyAll(BooleanFormula.simplify, this)
  }

  object BooleanFormula {
    def simplify: PartialFunction[Formula, Formula] =
      new PartialFunction[Formula, Formula] {
        def apply(form: Formula) : Formula =
          form match {
            case And(args) if args.isIn(False) => False
            case And(args) if args.areAll(True) => True
            case And(args) =>
              canonicalize(And(args.map(_.simplify).distinct))

            case Or(args) if args.isIn(True) => True
            case Or(args) if args.areAll(False) => False
            case Or(args) => canonicalize(Or(args.map(_.simplify).distinct))
            
            case Not(True) => False
            case Not(False) => True
            case Not(Not(arg)) => arg
            case Not(arg) => Not(arg.simplify)

            case Xor(False,right) => right
            case Xor(left,False) => left
            case Xor(True,right) => Not(right)
            case Xor(left,True) => Not(left)
            case Xor(left,right) => Xor(left.simplify, right.simplify)

            case Implies(left,right) => Or(List(Not(left), right))
            case IFF(left, right) =>
              And(List(Implies(left,right), Implies(right,left)))
          }
        def isDefinedAt(form: Formula) : Boolean =
          form.isInstanceOf[BooleanFormula]
      }
  }


  case class Not(arg: Formula) extends BooleanFormula

  case class And(args: List[Formula]) extends BooleanFormula {
    def this(left: Formula, right: Formula) = this(List(left,right))
    override def toString = "And(" + args.mkString(" , ") + ")"
  }

  case class Or(args: List[Formula]) extends BooleanFormula {
    override def toString = "Or(" + args.mkString(" , ") + ")"
  }

  case class Xor(left: Formula, right: Formula) extends BooleanFormula


  case class Implies(left: Formula, right: Formula) extends BooleanFormula
  case class IFF(left: Formula, right: Formula) extends BooleanFormula




  /* Helpers for Simplification */
  object BooleanFormulaHelpers {

    def flatten(form: Formula) : Formula =
      form match {
        case And( And(inner) :: outer ) => flatten(And( inner ::: outer ))
        case And( x :: xs ) if xs.exists(isAnd) => flatten(And( xs ::: List(x) ))
        case Or( Or(inner) :: outer ) => flatten(Or( inner ::: outer ))
        case Or( x :: xs ) if xs.exists(isOr) => flatten(Or( xs ::: List(x) ))
        case _ => form
      }

    def trim(form: Formula) : Formula =
      form match {
        case And(xs) => And(xs.filterNot(True.==))
        case Or(xs)  => Or(xs.filterNot(False.==))
        case _ => form
      }

    def canonicalize(x: Formula) : Formula =
      trim(flatten(x))

    def isAnd(form: Formula) : Boolean =
      form match {
        case And(_) => true
        case _ => false
      }

    def isOr(form: Formula) : Boolean =
      form match {
        case Or(_) => true
        case _ => false
      }

    implicit def wraplist[T](x: List[T]) : ListWrapper[T] = new ListWrapper(x)
    implicit def fromwraplist[T](x: ListWrapper[T]) : List[T] = x.in

  }

// A future goal is to provide some combinators for parsing
}
