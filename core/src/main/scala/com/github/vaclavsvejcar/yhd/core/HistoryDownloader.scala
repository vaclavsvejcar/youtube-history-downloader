package com.github.vaclavsvejcar.yhd.core

import java.io.File

import com.github.vaclavsvejcar.yhd.core.tools.Resource
import kantan.csv.CsvWriter
import net.ruippeixotog.scalascraper.browser.JsoupBrowser.JsoupDocument
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import org.jsoup.Jsoup
import wvlet.log.LogSupport

import scala.annotation.tailrec
import scala.util.Try

// TODO #1: produce downloaded output as stream, instead of file write
class HistoryDownloader(cookies: Map[String, String]) extends LogSupport {

  import com.github.vaclavsvejcar.yhd.core.tools.Parsers._

  private object Url {
    val root = "https://youtube.com"
    val history = s"$root/feed/history"

    def continuation(cToken: String) = s"$root/browse_ajax?ctoken=$cToken&continuation=$cToken"
  }

  private val browser = initBrowser()

  def fetchAndSave(out: File): Unit = {
    import kantan.csv._
    import kantan.csv.ops._

    val document = browser.get(Url.history)

    // no sense to continue if not logged in, abort
    exitIfNotLoggedIn(document)

    // parse the session token
    val sessionToken = parseSessionToken(document.toHtml)

    val csvWriter = out.asCsvWriter[VideoRef](rfc.withHeader)

    tools.withResource(csvWriter) { writer =>
      @tailrec def next(nextToken: Option[String], iteration: Int, total: Int): Unit = {
        nextToken match {
          case Some(token) =>
            val (newToken, videos) = fetchNext(token, sessionToken)
            val newIteration = iteration + 1
            val newTotal = total + videos.size

            info(s"Iteration $newIteration - writing down next ${videos.size} videos (total $newTotal videos until now)")
            videos.foreach(writer.write)

            next(newToken, iteration + 1, total + videos.size)
          case None =>
            info(s"No more videos to fetch, total $total videos fetched in $iteration iterations")
            info(s"Successfully saved to: ${out.getAbsolutePath}")
        }
      }

      val firstPageVideos = parseVideoRefs(document)
      info(s"Iteration 1 - writing down initial list of videos (total ${firstPageVideos.size})")
      firstPageVideos.foreach(writer.write)

      next(nextPageCToken(document), 1, firstPageVideos.size)
    }
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

  // TODO #1: indicate failure to caller, instead of hard exit
  private def exitIfNotLoggedIn(document: JsoupDocument): Unit = {
    if (!document.toHtml.contains("yt-masthead-picker-name")) {
      error("User is not logged in, exiting...")
      System.exit(1)
    }
  }

  private def initBrowser(): CustomBrowser = {
    val browser = new CustomBrowser()
    browser.setCookies("/", cookies)
    browser
  }

  private implicit def csvWriterResource[T]: Resource[CsvWriter[T]] =
    (resource: CsvWriter[T]) => resource.close
}


