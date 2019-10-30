package com.github.vaclavsvejcar.yhd.tools

import scala.io.Source

trait Releasable[T] {
  def release(resource: T): Unit
}

object Releasable {
  def apply[T: Releasable]: Releasable[T] = implicitly[Releasable[T]]

  implicit def javaAutoCloseable[T <: java.lang.AutoCloseable]: Releasable[T] = _.close()
  implicit def scalaSource[T <: Source]: Releasable[T]                        = _.close()
}

object Using {

  /**
    * Function that takes the releasable resource, performs the function `f` over it and automatically closes is after.
    *
    * @param resource instance of releasable resource
    * @param f        function to perform
    * @tparam T type of the resource
    * @tparam U resulting type
    * @return result of the given function
    */
  def apply[T: Releasable, U](resource: T)(f: T => U): U = {
    try {
      f(resource)
    } finally {
      implicitly[Releasable[T]].release(resource)
    }
  }
}
