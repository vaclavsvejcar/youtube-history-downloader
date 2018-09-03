package com.github.vaclavsvejcar.yhd.core.report

import java.io.{File, FileOutputStream, OutputStreamWriter, Writer}
import java.nio.charset.StandardCharsets

import com.github.vaclavsvejcar.yhd.core.tools.{Resource, withResource}
import com.github.vaclavsvejcar.yhd.core.{VideoRef, abort}
import kantan.csv._
import kantan.csv.ops._
import wvlet.log.LogSupport

import scala.io.Source

// TODO #1: produce output as stream, instead of file write
class ReportGenerator extends LogSupport {

  private val LargeReportThreshold = 5000

  def generateAndWrite(historyFile: File, reportFile: File): Unit = {
    info(s"Generating HTML report into '${reportFile.getName}'...")
    val videoRefs = parseHistory(historyFile)
    val template = render(createReport(videoRefs))

    info(s"Processing ${videoRefs.size} records...")
    if (videoRefs.size > LargeReportThreshold) {
      warn("Your watching history is quite large. It may take a while to open the report in web browser.")
    }
    withResource(
      new OutputStreamWriter(new FileOutputStream(reportFile), StandardCharsets.UTF_8)
    )(_.write(template))
    info(s"Report successfully generated, now open following file in web browser: ${reportFile.getAbsolutePath}")
  }

  // TODO #1: indicate failure to caller, instead of hard exit
  private def parseHistory(historyFile: File): Seq[VideoRef] = {
    import cats.implicits._

    val csv = Source.fromFile(historyFile).mkString.readCsv[List, VideoRef](rfc.withHeader)
    csv.sequence[Either[ReadError, ?], VideoRef] match {
      case Right(videoRefs) => videoRefs
      case Left(error) => abort(
        s"Error during parsing the ${historyFile.getName} CSV file (maybe corrupted file?).", error)
    }
  }

  private def createReport(videos: Seq[VideoRef]): ReportData = {
    val uniqueNo = videos.distinct.size

    ReportData(videos, uniqueNo)
  }

  private def render(data: ReportData): String =
    com.github.vaclavsvejcar.yhd.core.templates.html.report(data).toString()

  private implicit def writerResource[T <: Writer]: Resource[T] =
    (resource: Writer) => resource.close()

}
