package com.github.vaclavsvejcar.yhd.domain

import kantan.csv.HeaderCodec

final case class VideoRef(
  id: String,
  title: String,
  description: Option[String],
  duration: Option[String],
  author: String,
  authorLink: String
)

object VideoRef {
  implicit val csvCodec: HeaderCodec[VideoRef] = HeaderCodec.caseCodec(
    "id",
    "title",
    "description",
    "duration",
    "author",
    "authorLink"
  )(VideoRef.apply)(VideoRef.unapply)

}
