package com.github.vaclavsvejcar.yhd.parsers

import com.github.vaclavsvejcar.yhd.domain.VideoRef
import net.ruippeixotog.scalascraper.browser.JsoupBrowser.JsoupDocument
import org.jsoup.Jsoup
import org.scalatest.{FlatSpec, Matchers}

import scala.io.Source

class VideoRefParserSpec extends FlatSpec with Matchers {

  "VideoRef parser" should "parse VideoRefs from JsoupDocument" in {
    val source   = Source.fromResource("removed-video-snippet.html").getLines().mkString("\n")
    val document = JsoupDocument(Jsoup.parse(source))
    val expected = Seq(Right(VideoRef("aabbccdd", "The Video Title", None, "4:48", "John Smith", "/user/johnSmith")))

    VideoRefParser.parseVideoRefs(document) shouldEqual expected
  }

}
