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
package com.github.vaclavsvejcar.yhd.downloader

import java.io.{File, InputStream}

import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.browser.JsoupBrowser.JsoupDocument
import net.ruippeixotog.scalascraper.util.using
import org.jsoup.Connection.Method.{GET, POST}
import org.jsoup.Connection.Response
import org.jsoup.{Connection, Jsoup}

import scala.collection.mutable
import scala.jdk.CollectionConverters._

class CustomBrowser(val userAgent: String = "jsoup/1.8", val proxy: java.net.Proxy = null) extends Browser {
  type DocumentType = JsoupDocument

  private[this] val cookieMap = mutable.Map.empty[String, String]

  def get(url: String): JsoupDocument =
    executePipeline(Jsoup.connect(url).method(GET).proxy(proxy))

  def post(url: String, form: Map[String, String]): JsoupDocument =
    executePipeline(Jsoup.connect(url).method(POST).proxy(proxy).data(form.asJava).ignoreContentType(true))

  def parseFile(file: File, charset: String): JsoupDocument =
    JsoupDocument(Jsoup.parse(file, charset))

  def parseString(html: String): JsoupDocument =
    JsoupDocument(Jsoup.parse(html))

  def parseInputStream(inputStream: InputStream, charset: String): JsoupDocument =
    using(inputStream) { _ =>
      JsoupDocument(Jsoup.parse(inputStream, charset, ""))
    }

  def cookies(url: String): Map[String, String] = cookieMap.toMap

  def setCookie(url: String, key: String, value: String): mutable.Map[String, String] = {
    cookieMap += key -> value
  }

  def setCookies(url: String, m: Map[String, String]): mutable.Map[String, String] = {
    cookieMap ++= m
  }

  def clearCookies(): Unit = cookieMap.clear()

  def requestSettings(conn: Connection): Connection = conn

  protected[this] def defaultRequestSettings(conn: Connection): Connection =
    conn
      .cookies(cookieMap.asJava)
      .userAgent(userAgent)
      .header("Accept", "text/html,application/xhtml+xml,application/xml")
      .header("Accept-Charset", "utf-8")
      .timeout(15000)
      .maxBodySize(0)

  protected[this] def executeRequest(conn: Connection): Response =
    conn.execute()

  protected[this] def processResponse(res: Connection.Response): JsoupDocument = {
    lazy val doc = res.parse
    cookieMap ++= res.cookies.asScala
    if (res.hasHeader("Location")) get(res.header("Location")) else JsoupDocument(doc)
  }

  private[this] val executePipeline: Connection => JsoupDocument =
    (defaultRequestSettings _)
      .andThen(requestSettings)
      .andThen(executeRequest)
      .andThen(processResponse)
}
