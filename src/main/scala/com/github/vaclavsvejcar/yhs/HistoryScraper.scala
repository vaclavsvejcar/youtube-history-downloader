package com.github.vaclavsvejcar.yhs

import java.io.PrintWriter

import net.ruippeixotog.scalascraper.browser.JsoupBrowser.JsoupDocument
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import org.jsoup.Jsoup
import wvlet.log.LogSupport

import scala.annotation.tailrec
import scala.util.Try

class HistoryScraper(cookies: Map[String, String]) extends LogSupport {

  import com.github.vaclavsvejcar.yhs.tools.Parsers._

  private object Url {
    val root = "https://youtube.com"
    val history = s"$root/feed/history"

    def continuation(cToken: String) = s"$root/browse_ajax?ctoken=$cToken&continuation=$cToken"
  }

  private val browser = initBrowser()

  def fetchHistory(filename: String = "ytb_history.csv"): Unit = {
    val document = browser.get(Url.history)

    if (!isLoggedIn(document)) {
      error("User is not logged in, exiting...")
      System.exit(1) // FIXME handle this better
    }

    val sessionToken = parseSessionToken(document.toHtml)
    val writer = new PrintWriter(filename)

    @tailrec def next(nextToken: Option[String], iteration: Int, total: Int): Unit = {
      nextToken match {
        case Some(token) =>
          val (newToken, videos) = fetchNext(token, sessionToken)
          val newIteration = iteration + 1
          val newTotal = total + videos.size

          info(s"Iteration $newIteration - writing down next ${videos.size} videos (total $newTotal videos until now)")
          videos.foreach(video => writer.write(csvRow(video)))

          next(newToken, iteration + 1, total + videos.size)
        case None =>
          info(s"No more videos to fetch, total $total videos fetched in $iteration iterations")
      }
    }


    val firstPageVideos = parseVideoRefs(document)
    info(s"Iteration 1 - writing down initial list of videos (total ${firstPageVideos.size})")
    firstPageVideos.foreach(video => writer.write(csvRow(video)))

    next(nextPageCToken(document), 1, firstPageVideos.size)
    writer.close()
  }

  private def fetchNext(cToken: String, sessionToken: String): (Option[String], Seq[VideoRef]) = {
    import play.api.libs.json._
    val url = Url.continuation(cToken)
    val response = browser.post(url, Map("session_token" -> sessionToken))
    val json = Json.parse(response.body.text)

    val loadMoreHtml = (json \ "load_more_widget_html").asOpt[String].map(Jsoup.parse)
    val contentHtml = (json \ "content_html").asOpt[String].map(Jsoup.parse)
    val nextCToken = loadMoreHtml.map(JsoupDocument).flatMap(nextPageCToken)
    val videos = contentHtml.map(JsoupDocument).map(parseVideoRefs).getOrElse(Seq.empty)

    (nextCToken, videos)
  }

  private def nextPageCToken(doc: JsoupDocument): Option[String] =
    Try(
      parseCToken(doc >> element(".load-more-button") >> attr("data-uix-load-more-href"))
    ).toOption

  // FIXME really really naive stupid implementation, but works for testing purposes
  private def isLoggedIn(document: JsoupDocument): Boolean =
    document.toHtml.contains("yt-masthead-picker-name")

  private def csvRow(videoRef: VideoRef): String = {
    videoRef.id + "," + videoRef.title + "\n"
  }

  private def initBrowser(): CustomBrowser = {
    val browser = new CustomBrowser()
    browser.setCookies("/", cookies)
    browser
  }
}


