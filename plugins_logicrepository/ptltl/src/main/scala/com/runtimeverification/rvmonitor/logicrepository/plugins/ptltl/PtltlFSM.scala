//import language.implicitConversions

import com.runtimeverification.rvmonitor.logicrepository.plugins.ptltl._
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptltl.FSMHelpers._

package com.runtimeverification.rvmonitor.logicrepository.plugins.ptltl {

  case class Transition(on: Event, to: FSMNode) {
    override def toString: String = {
      //it would probably be cleaner to use separate classes for default transitions -- Pat
      if (on == Event("default"))
        "default " + to.mkRef
      else
        on + " -> " + to.mkRef
    }
  }


  case class FSMNode(id: Int) {
    var default: Transition = null
    var transitions: List[Transition] = List()

    var uid: BitVector = null

    var validates: Boolean = false

    // Mutates self
    def addTransition(t: Transition) : FSMNode = {
      if (t.on == Event("default"))
        this.default = t
      else
        this.transitions = t :: transitions
      this
    }

    // Mutates self
    def setDefaultTransition(to: FSMNode) : FSMNode = {
      this.default = Transition(Event("default"),to)
      this
    }

    // Mutates self
    def setUid(b: BitVector) : FSMNode = {
      this.uid = b
      this.validates = b.out
      this
    }

    def mkRef : String =
      "n" + this.id.toString 

    override def toString =
      mkRef + "[\n  " + transitionsToString(default :: transitions) + "\n]"

  }

  class FSM {
    var nodes: List[FSMNode] = List()
    var counter: Int = 0

    var validation: List[FSMNode] = List()
    var violation:  List[FSMNode] = List()

    // This is the prefered constructor, otherwise you have to call mkValids yourself
    def this(fns: List[FSMNode]) = {
      this()
      for (n <- fns)
        this.addNode(n)
      this.mkValids
    }

    // Mutates self
    def addNode(n: FSMNode) : FSM = {
      this.nodes = n :: nodes
      this
    }

    // Mutates self
    def mkValids : FSM = {
      for (n <- nodes)
        if (n.validates)
          validation = n :: validation
        else
          violation = n :: violation
      this
    }

    override def toString =
      ( nodes.reverse.mkString("\n")
      + (if (violation.length == 0) "" else violation.map(_.mkRef).mkString("\nalias violation = ", " ", ""))
      + (if (validation.length == 0) "" else validation.map(_.mkRef).mkString("\nalias validation = ", " ", ""))
      )
  }

  object FSMHelpers {
    def transitionsToString(ts: List[Transition]) : String =
      ts.reverse.mkString(",\n  ")

    implicit def wraplist[T](x: List[T]) : ListWrapper[T] = new ListWrapper(x)
    implicit def fromwraplist[T](x: ListWrapper[T]) : List[T] = x.in

  }

}
