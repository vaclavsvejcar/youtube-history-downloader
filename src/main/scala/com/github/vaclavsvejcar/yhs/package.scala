package com.github.vaclavsvejcar

import scala.util.{Failure, Success, Try}

package object yhs {

  def withCloseable[A: AutoCloseable, B](resource: A)(block: A => B): B = {
    Try(block(resource)) match {
      case Success(result) =>
        AutoCloseable[A].close(resource)
        result
      case Failure(e) =>
        AutoCloseable[A].close(resource)
        throw e
    }
  }

}
