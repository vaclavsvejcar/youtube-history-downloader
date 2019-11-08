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
