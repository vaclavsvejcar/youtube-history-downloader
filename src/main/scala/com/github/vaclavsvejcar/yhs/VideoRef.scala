package com.github.vaclavsvejcar.yhs

case class VideoRef(id: String, title: String)

object VideoRef {
  implicit val csvRow: CsvRow[VideoRef] = new CsvRow[VideoRef] {
    override val header: Seq[String] = Seq("id", "title")

    override def row(data: VideoRef): Seq[String] = Seq(data.id, data.title)
  }
}
