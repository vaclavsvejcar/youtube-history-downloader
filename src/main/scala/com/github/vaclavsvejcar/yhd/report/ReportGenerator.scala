package com.github.vaclavsvejcar.yhd.report

import java.io.{FileOutputStream, OutputStreamWriter, Writer}
import java.nio.charset.StandardCharsets

import com.github.vaclavsvejcar.yhd.tools.{Resource, withResource}
import com.github.vaclavsvejcar.yhd.{Config, VideoRef, abort}
import kantan.csv._
import kantan.csv.ops._
import wvlet.log.LogSupport

import scala.io.Source

class ReportGenerator(config: Config) extends LogSupport {

  private val LargeReportThreshold = 5000

  def generateAndWrite(): Unit = {
    info(s"Generating HTML report into '${config.report.getName}'...")
    val videoRefs = parseHistory()
    val template  = render(createReport(videoRefs))

    info(s"Processing ${videoRefs.size} records...")
    if (videoRefs.size > LargeReportThreshold) {
      warn("Your watching history is quite large. It may take a while to open the report in web browser.")
    }
    withResource(new OutputStreamWriter(new FileOutputStream(config.report), StandardCharsets.UTF_8))(_.write(template))
    info(s"Report successfully generated, now open following file in web browser: ${config.report.getAbsolutePath}")
  }

  private def parseHistory(): Seq[VideoRef] = {
    import cats.implicits._

    val csv = Source.fromFile(config.history).mkString.readCsv[List, VideoRef](rfc.withHeader)
    csv.sequence[Either[ReadError, *], VideoRef] match {
      case Right(videoRefs) => videoRefs
      case Left(error) =>
        abort(s"Error during parsing the ${config.history.getName} CSV file (maybe corrupted file?).", error)
    }
  }

  private def createReport(videos: Seq[VideoRef]): ReportData = {
    val uniqueNo = videos.distinct.size

    ReportData(videos, uniqueNo)
  }

  private def render(data: ReportData): String =
    com.github.vaclavsvejcar.yhd.templates.html.report(data).toString()

  private implicit def writerResource[T <: Writer]: Resource[T] =
    (resource: Writer) => resource.close()

}
