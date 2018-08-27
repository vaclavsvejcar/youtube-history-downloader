package com.github.vaclavsvejcar.yhs.tools

import scala.util.matching.Regex

object Parsers {

  private object Regexes {
    val sessionToken: Regex = """'XSRF_TOKEN': "(.*?)"""".r

  }

  /**
    * Parses the input string, that should contain cookie string. The implementation tries to be
    * as bulletproof as possible, since the input comes from the end-user, copied from the web
    * browser.
    *
    * @param raw raw string, that should contain cookie string
    * @return parsed map of cookies
    */
  def parseCookies(raw: String): Map[String, String] = {
    val rawCookies = raw
      .replaceAll("Cookie: ", "")
      .replaceAll("\n", "")
      .replaceAll("; ", ";")
      .split(";")
    rawCookies.flatMap { cookie =>
      val chunks = cookie.split("=")
      if (chunks.size < 2) None else Some(chunks(0).trim -> chunks(1).trim)
    }.toMap
  }

  def parseCToken(source: String): String = {
    source.split("&continuation=")(1).split("&")(0)
  }

  def parseSessionToken(source: String): String =
    Regexes.sessionToken.findFirstMatchIn(source).map(_.group(1)).get // FIXME handle error case

  def parseVideoId(source: String): String = source.split("=")(1)
}
