/*
 * YouTube History Downloader :: Download your full YouTube watch history
 * Copyright (c) 2019-2020 Vaclav Svejcar
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
import net.ruippeixotog.scalascraper.model.Element

import scala.util.Try

object VideoRefParser {
  import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
  import net.ruippeixotog.scalascraper.dsl.DSL._

  case class ParseError(message: String, elem: Element, cause: Option[Throwable]) {
    def withoutCause: ParseError = this.copy(cause = None)
  }

  def parseVideoRefs(document: JsoupDocument): Seq[Either[ParseError, VideoRef]] =
    (document >> elementList(".yt-lockup-video")).map(parseVideoRef)

  def parseVideoRef(elem: Element): Either[ParseError, VideoRef] =
    for {
      id    <- extract(elem, "video ID")(_ >> attr("data-context-item-id"))
      title <- extract(elem, "video title")(_ >> element("a.yt-uix-tile-link") >> attr("title"))
      desc  <- extract(elem, "video description")(_ >?> element(".yt-lockup-description") >> text)
      duration <- extract(elem, "video duration") { video =>
        val videoTime = video >?> element(".video-time")
        videoTime.flatMap(_ >?> element("span") >> text).orElse(videoTime.flatMap(_ >?> element("span") >> text))
      }
      user     <- extract(elem, "user info")(_ >> element(".yt-lockup-byline > a"))
      userName <- extract(user, "user name")(_ >> text)
      userLink <- extract(user, "user profile link")(_ >> attr("href"))
    } yield VideoRef(id, title, desc, duration, userName, userLink)

  private def extract[T](elem: Element, field: String)(fn: Element => T): Either[ParseError, T] =
    Try(fn(elem)).toEither.left.map(cause => ParseError(s"Cannot parse field '$field'", elem, Some(cause)))

}
