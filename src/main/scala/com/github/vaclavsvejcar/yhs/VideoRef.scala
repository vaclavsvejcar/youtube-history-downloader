package com.github.vaclavsvejcar.yhs

import kantan.csv.HeaderEncoder

final case class VideoRef(id: String, title: String, description: Option[String])

object VideoRef {
  implicit val headerEncoder: HeaderEncoder[VideoRef] =
    HeaderEncoder.caseEncoder("id", "title", "description")(VideoRef.unapply)

}

