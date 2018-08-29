package com.github.vaclavsvejcar.yhs

import java.io.File

import enumeratum._
import scopt.OptionParser

import scala.collection.immutable


sealed trait Mode extends EnumEntry

object Mode extends Enum[Mode] {
  override def values: immutable.IndexedSeq[Mode] = findValues

  case object Fetch extends Mode
  case object Other extends Mode

}

case class Config(mode: Mode = Mode.Other,
                  cookies: File = new File("cookies.txt"),
                  outputCsv: File = new File("history.csv"))

object Config {
  val Default: Config = Config()

  def parse(args: String*): Option[Config] = parser.parse(args, Default)

  private def parser: OptionParser[Config] = new scopt.OptionParser[Config]("yhs") {
    help("help").text("Prints this help text")

    cmd("fetch").action((_, c) => c.copy(mode = Mode.Fetch))
      .text("Fetches the youtube history into the output CSV file")
      .children(
        opt[File]('c', "cookies").valueName("<file>").action((x, c) => c.copy(cookies = x))
          .text(s"file containing the copied Youtube cookies (default: ${Default.cookies.getName})"),
        opt[File]('o', "output").valueName("<file>").action((x, c) => c.copy(outputCsv = x))
          .text(s"Output CSV file with Youtube history (default: ${Default.outputCsv.getName})")
      )
  }
}