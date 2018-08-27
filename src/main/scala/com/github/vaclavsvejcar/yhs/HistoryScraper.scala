package com.github.vaclavsvejcar.yhs

import net.ruippeixotog.scalascraper.browser.JsoupBrowser.JsoupDocument
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import org.jsoup.Jsoup

import scala.util.matching.Regex

class HistoryScraper(cookies: Map[String, String]) {

  import HistoryScraper._

  private object Url {
    val root = "https://youtube.com"
    val history = s"$root/feed/history"

    def continuation(cToken: String) = s"$root/browse_ajax?ctoken=$cToken&continuation=$cToken"
  }

  private val browser = initBrowser()

  def fetchHistory(): Unit = {
    val document = browser.get(Url.history)
    val sessionToken = parseSessionToken(document.toHtml)

    val result = parseVideos(document)
    println("\n\n\n\n" + result)
  }

  private def fetchNext(cToken: String, sessionToken: String): (String, Seq[VideoRef]) = {
    import play.api.libs.json._
    val url = Url.continuation(cToken)
    val response = browser.post(url, Map("session_token" -> sessionToken))
    val json = Json.parse(response.body.text)
    val loadMoreHtml = Jsoup.parse((json \ "load_more_widget_html").as[String])
    val contentHtml = Jsoup.parse((json \ "content_html").as[String])

    val nextCToken = nextPageCToken(JsoupDocument(loadMoreHtml))
    val videos = parseVideos(JsoupDocument(contentHtml))

    (nextCToken, videos)
  }

  private def nextPageCToken(doc: JsoupDocument): String =
    parseCToken(doc >> element(".load-more-button") >> attr("data-uix-load-more-href"))

  private def parseVideos(doc: JsoupDocument): Seq[VideoRef] = {
    val videos = doc >> elementList("a.yt-uix-tile-link")
    videos.map { video =>
      val id = video >> attr("href")
      val title = video >> attr("title")
      VideoRef(parseVideoId(id), title)
    }
  }

  private def initBrowser(): CustomBrowser = {
    val browser = new CustomBrowser()
    browser.setCookies("/", cookies)
    browser
  }
}

object HistoryScraper {

  object Parsers {
    val sessionToken: Regex = """'XSRF_TOKEN': "(.*?)"""".r
  }

  def parseCToken(source: String): String = {
    source.split("&continuation=")(1).split("&")(0)
  }

  def parseSessionToken(source: String): String =
    Parsers.sessionToken.findFirstMatchIn(source).map(_.group(1)).get // FIXME handle error case

  def parseVideoId(source: String): String = source.split("=")(1)
}

case class VideoRef(id: String, title: String)



