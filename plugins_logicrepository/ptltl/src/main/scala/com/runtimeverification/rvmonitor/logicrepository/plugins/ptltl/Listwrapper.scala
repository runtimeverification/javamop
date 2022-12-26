// Provides extra functionality for lists
// To use: define an implicit conversion in your file, e.g.:
// implicit def wraplist[T](x: List[T]) : ListWrapper[T] = new ListWrapper(x)
// implicit def fromwraplist[T](x: ListWrapper[T]) : List[T] = x.in

package com.runtimeverification.rvmonitor.logicrepository.plugins.ptltl {

  case class ListWrapper[T](in: List[T]) {
    type T2

    def isIn(x: T) : Boolean =
      in.exists(x.==)

    def areAll(x: T) : Boolean =
      in == in.filter(x.==)

    // def mapWithIndex: ((T, Int) => T2) => List[T2] = f =>
    //   in.zipWithIndex.map( x => x match { case (a,b) => f(a,b) } )

    def set(i: Int, e: T) : List[T] = 
      this.take(i) ::: (e :: this.drop(i + 1))


    implicit def wraplist[T](x: List[T]) : ListWrapper[T] = new ListWrapper(x)
    implicit def fromwraplist[T](x: ListWrapper[T]) : List[T] = x.in

  }

}
