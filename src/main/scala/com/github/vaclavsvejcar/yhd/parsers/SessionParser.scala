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
package com.github.vaclavsvejcar.yhd.parsers

import scala.util.matching.Regex

object SessionParser {

  val SessionToken: Regex = """'XSRF_TOKEN': "(.*?)"""".r

  def parseCookies(raw: String): Map[String, String] = {
    val rawCookies = raw
      .replaceAll("Cookie: ", "")
      .replaceAll("\n", "")
      .replaceAll("; ", ";")
      .split(";")
    rawCookies.toSeq.flatMap { cookie =>
      val chunks = cookie.split("=", 2)
      if (chunks.size != 2) None else Some(chunks(0).trim -> chunks(1).trim)
    }.toMap
  }

  def parseCToken(source: String): String =
    source.split("&continuation=")(1).split("&")(0)

  def parseSessionToken(source: String): String =
    SessionToken.findFirstMatchIn(source).map(_.group(1)).get // FIXME handle error case

}
