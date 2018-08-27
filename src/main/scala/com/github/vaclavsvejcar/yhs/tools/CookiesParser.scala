package com.github.vaclavsvejcar.yhs.tools

object CookiesParser {

  /**
    * Parses the input string, that should contain cookie string. The implementation tries to be
    * as bulletproof as possible, since the input comes from the end-user, copied from the web
    * browser.
    *
    * @param raw raw string, that should contain cookie string
    * @return parsed map of cookies
    */
  def parse(raw: String): Map[String, String] = {
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
}
