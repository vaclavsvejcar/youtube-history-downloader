/*
 * YouTube History Downloader :: Download your full YouTube watch history
 * Copyright (c) 2019-2020 Vaclav Svejcar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
