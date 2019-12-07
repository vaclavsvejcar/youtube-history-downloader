/*
 * YouTube History Downloader :: Download your full YouTube watch history
 * Copyright (c) 2019 Vaclav Svejcar
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
package com.github.vaclavsvejcar.yhd

import java.io.File

import enumeratum._
import scopt.OptionParser

import scala.collection.immutable

sealed trait Mode extends EnumEntry

object Mode extends Enum[Mode] {
  override def values: immutable.IndexedSeq[Mode] = findValues

  case object Fetch  extends Mode
  case object Report extends Mode
  case object Other  extends Mode

}

case class Config(
  mode: Mode = Mode.Other,
  debug: Boolean = false,
  cookies: File = new File("cookies.txt"),
  history: File = new File("history.csv"),
  report: File = new File("report.html")
)

object Config {
  val Default: Config = Config()

  def parse(args: String*): Option[Config] = parser.parse(args, Default)

  private def parser: OptionParser[Config] = new scopt.OptionParser[Config]("yhd") {
    help("help").text("Prints this help text")

    cmd("fetch")
      .action((_, c) => c.copy(mode = Mode.Fetch))
      .text("Fetches the youtube history into the output CSV file")
      .children(
        opt[File]('c', "cookies")
          .valueName("<file>")
          .action((x, c) => c.copy(cookies = x))
          .text(s"file containing the copied Youtube cookies (default: ${Default.cookies.getName})"),
        opt[File]('o', "output")
          .valueName("<file>")
          .action((x, c) => c.copy(history = x))
          .text(s"Output CSV file with Youtube history (default: ${Default.history.getName})"),
        opt[Unit]('d', "debug")
          .action((_, c) => c.copy(debug = true))
          .text("Produces more verbose output for debugging purposes")
      )

    cmd("report")
      .action((_, c) => c.copy(mode = Mode.Report))
      .text("Generates pretty HTML report from the previously fetched CSV file")
      .children(
        opt[File]('o', "output")
          .valueName("<file>")
          .action((x, c) => c.copy(report = x))
          .text(s"Output HTML file with the generated report (default: ${Default.report.getName})")
      )
  }
}
