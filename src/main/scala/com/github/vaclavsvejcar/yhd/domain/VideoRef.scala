package com.github.vaclavsvejcar.yhd.domain

import kantan.csv.HeaderCodec

final case class VideoRef(
  id: String,
  title: String,
  description: Option[String],
  duration: Option[String],
  user: String,
  userLink: String
)

object VideoRef {
  implicit val csvCodec: HeaderCodec[VideoRef] = HeaderCodec.caseCodec(
    "id",
    "title",
    "description",
    "duration",
    "user",
    "userLink"
  )(VideoRef.apply)(VideoRef.unapply)

}
