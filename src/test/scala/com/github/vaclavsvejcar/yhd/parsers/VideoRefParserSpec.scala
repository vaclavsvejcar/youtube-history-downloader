/*
 * YouTube History Downloader :: Download your full YouTube watch history
 * Copyright (c) 2019 Vaclav Svejcar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.vaclavsvejcar.yhd.parsers

import com.github.vaclavsvejcar.yhd.domain.VideoRef
import net.ruippeixotog.scalascraper.browser.JsoupBrowser.JsoupDocument
import org.jsoup.Jsoup
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.io.Source

class VideoRefParserSpec extends AnyFlatSpec with Matchers {

  "VideoRef parser" should "parse VideoRefs from JsoupDocument" in {
    val source   = Source.fromResource("removed-video-snippet.html").getLines().mkString("\n")
    val document = JsoupDocument(Jsoup.parse(source))
    val expected =
      Seq(Right(VideoRef("aabbccdd", "The Video Title", None, Some("4:48"), "John Smith", "/user/johnSmith")))

    VideoRefParser.parseVideoRefs(document) shouldEqual expected
  }

  it should "parse VideoRef of livestream video" in {
    val source   = Source.fromResource("livestream-video-snippet.html").getLines().mkString("\n")
    val document = JsoupDocument(Jsoup.parse(source))
    val expected = Seq(
      Right(
        VideoRef(
          "21X5lGlDOfg",
          "NASA Live: Official Stream of NASA TV",
          Some(
            "Direct from America's space program to YouTube, watch NASA TV live streaming here to get the latest from our exploration of the universe and learn how we discover our home planet. NASA TV airs a v..."
          ),
          None,
          "NASA",
          "/user/NASAtelevision"
        )
      )
    )

    VideoRefParser.parseVideoRefs(document) shouldEqual expected
  }

}
