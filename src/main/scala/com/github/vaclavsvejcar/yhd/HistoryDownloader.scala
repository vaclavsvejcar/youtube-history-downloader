package com.github.vaclavsvejcar.yhd

import com.github.vaclavsvejcar.yhd.tools.Using
import net.ruippeixotog.scalascraper.browser.JsoupBrowser.JsoupDocument
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import org.jsoup.Jsoup
import wvlet.log.LogSupport

import scala.annotation.tailrec
import scala.util.Try

class HistoryDownloader(cookies: Map[String, String], config: Config) extends LogSupport {

  import com.github.vaclavsvejcar.yhd.tools.Parsers._

  private object Url {
    val root    = "https://youtube.com"
    val history = s"$root/feed/history"

    def continuation(cToken: String) = s"$root/browse_ajax?ctoken=$cToken&continuation=$cToken"
  }

  private val browser = initBrowser()

  def fetchAndSave(): Unit = {
    import kantan.csv._
    import kantan.csv.ops._

    val document = browser.get(Url.history)

    // no sense to continue if not logged in, abort
    exitIfNotLoggedIn(document)

    // parse the session token
    val sessionToken = parseSessionToken(document.toHtml)

    val csvWriter = config.history.asCsvWriter[VideoRef](rfc.withHeader)

    Using(csvWriter) { writer =>
      @tailrec def next(nextToken: Option[String], iteration: Int, total: Int): Unit = {
        nextToken match {
          case Some(token) =>
            val (newToken, videos) = fetchNext(token, sessionToken)
            val newIteration       = iteration + 1
            val newTotal           = total + videos.size

            info(s"Iteration $newIteration - writing down next ${videos.size} videos (total $newTotal videos)")
            videos.foreach(writer.write)

            next(newToken, iteration + 1, total + videos.size)
          case None =>
            info(s"No more videos to fetch, total $total videos fetched in $iteration iterations")
            info(s"Successfully saved to: ${config.history.getAbsolutePath}")
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
    val url      = Url.continuation(cToken)
    val response = browser.post(url, Map("session_token" -> sessionToken))
    val json     = Json.parse(response.body.text)

    val loadMoreHtml = (json \ "load_more_widget_html").asOpt[String].map(Jsoup.parse)
    val contentHtml  = (json \ "content_html").asOpt[String].map(Jsoup.parse)
    val nextCToken   = loadMoreHtml.map(JsoupDocument).flatMap(nextPageCToken)
    val videos       = contentHtml.map(JsoupDocument).map(parseVideoRefs).getOrElse(Seq.empty)

    (nextCToken, videos)
  }

  private def nextPageCToken(doc: JsoupDocument): Option[String] =
    Try(parseCToken(doc >> element(".load-more-button") >> attr("data-uix-load-more-href"))).toOption

  private def exitIfNotLoggedIn(document: JsoupDocument): Unit = {
    if (!document.toHtml.contains("yt-masthead-picker-name")) {
      error("Cannot login user using given cookies, exiting...")
      System.exit(1)
    }
  }

  private def initBrowser(): CustomBrowser = {
    val browser = new CustomBrowser()
    browser.setCookies("/", cookies)
    browser
  }
}
