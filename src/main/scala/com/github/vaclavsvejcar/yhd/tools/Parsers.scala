package com.github.vaclavsvejcar.yhd.tools

import com.github.vaclavsvejcar.yhd.VideoRef
import net.ruippeixotog.scalascraper.browser.JsoupBrowser.JsoupDocument

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
      val chunks = cookie.split("=", 2)
      if (chunks.size < 2) None else Some(chunks(0).trim -> chunks(1).trim)
    }.toMap
  }

  def parseCToken(source: String): String = {
    source.split("&continuation=")(1).split("&")(0)
  }

  def parseSessionToken(source: String): String =
    Regexes.sessionToken.findFirstMatchIn(source).map(_.group(1)).get // FIXME handle error case

  def parseVideoRefs(document: JsoupDocument): Seq[VideoRef] = {
    import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
    import net.ruippeixotog.scalascraper.dsl.DSL._

    (document >> elementList(".yt-lockup-video")).map { video =>
      val id    = video >> attr("data-context-item-id")
      val title = video >> element("a.yt-uix-tile-link") >> attr("title")
      val desc  = video >?> element(".yt-lockup-description") >> text
      val duration = (video >> element(".video-time") >?> element("span") >> text)
        .getOrElse(video >> element(".video-time") >> text)
      val userElem = video >> element(".yt-lockup-byline > a")
      val user     = userElem >> text
      val userLink = userElem >> attr("href")

      VideoRef(id, title, desc, duration, user, userLink)
    }
  }
}
