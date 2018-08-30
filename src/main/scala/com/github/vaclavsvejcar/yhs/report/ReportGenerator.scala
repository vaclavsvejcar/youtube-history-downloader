package com.github.vaclavsvejcar.yhs.report

import java.io.{FileOutputStream, OutputStreamWriter, Writer}
import java.nio.charset.StandardCharsets

import com.github.vaclavsvejcar.yhs.tools.{Resource, withResource}
import com.github.vaclavsvejcar.yhs.{Config, VideoRef, abort}
import kantan.csv._
import kantan.csv.ops._
import wvlet.log.LogSupport

import scala.io.Source

class ReportGenerator(config: Config) extends LogSupport {

  def generateAndWrite(): Unit = {
    info(s"Generating HTML report into '${config.report.getName}'...")
    val videoRefs = parseHistory()
    val template = render(videoRefs)

    info(s"Processing ${videoRefs.size} records...")
    withResource(
      new OutputStreamWriter(new FileOutputStream(config.report), StandardCharsets.UTF_8)
    )(_.write(template))
  }

  private def parseHistory(): Seq[VideoRef] = {
    import cats.implicits._

    val csv = Source.fromFile(config.history).mkString.readCsv[List, VideoRef](rfc.withHeader)
    csv.sequence[Either[ReadError, ?], VideoRef] match {
      case Right(videoRefs) => videoRefs
      case Left(error) => abort(
        s"Error during parsing the ${config.history.getName} CSV file (maybe corrupted file?).", error)
    }
  }

  private def render(videos: Seq[VideoRef]): String =
    com.github.vaclavsvejcar.yhs.templates.html.report(videos).toString()

  private implicit def writerResource[T <: Writer]: Resource[T] =
    (resource: Writer) => resource.close()

}
