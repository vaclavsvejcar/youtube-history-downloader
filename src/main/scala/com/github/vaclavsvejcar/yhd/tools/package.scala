package com.github.vaclavsvejcar.yhd

import scala.util.{Failure, Success, Try}

package object tools {

  def withResource[A: Resource, B](resource: A)(logic: A => B): B = Try(logic(resource)) match {
    case Success(result) =>
      Resource[A].close(resource)
      result
    case Failure(e) =>
      Resource[A].close(resource)
      throw e
  }
}
