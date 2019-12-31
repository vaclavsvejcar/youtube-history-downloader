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
package com.github.vaclavsvejcar.yhd.report

import java.io.{FileOutputStream, OutputStreamWriter}
import java.nio.charset.StandardCharsets

import com.github.vaclavsvejcar.yhd.domain.{ReportData, VideoRef}
import com.github.vaclavsvejcar.yhd.tools.Using
import com.github.vaclavsvejcar.yhd.{Config, abort}
import kantan.csv._
import kantan.csv.ops._
import wvlet.log.LogSupport

import scala.io.Source

class ReportGenerator(config: Config) extends LogSupport {

  private val LargeReportThreshold = 5000

  def generateAndWrite(): Unit = {
    info(s"Generating HTML report into '${config.report.getName}'...")
    val videoRefs = parseHistory()
    val template  = render(ReportData.from(videoRefs))

    info(s"Processing ${videoRefs.size} records...")
    if (videoRefs.size > LargeReportThreshold) {
      warn("Your watching history is quite large. It may take a while to open the report in web browser.")
    }
    Using(new OutputStreamWriter(new FileOutputStream(config.report), StandardCharsets.UTF_8))(_.write(template))
    info(s"Report successfully generated, now open following file in web browser: ${config.report.getAbsolutePath}")
  }

  private def parseHistory(): Seq[VideoRef] = {
    import cats.implicits._

    val csv = Using(Source.fromFile(config.history))(_.mkString.readCsv[List, VideoRef](rfc.withHeader))
    csv.sequence[Either[ReadError, *], VideoRef] match {
      case Right(videoRefs) => videoRefs
      case Left(error) =>
        abort(
          s"""
             |Error during parsing the ${config.history.getName} CSV file (maybe corrupted file,
             |please re-run the 'fetch' command).""".stripMargin.replaceAll("\n", " "),
          error
        )
    }
  }

  private def render(data: ReportData): String = com.github.vaclavsvejcar.yhd.templates.html.report(data).toString()

}
