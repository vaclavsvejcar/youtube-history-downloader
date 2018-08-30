package com.github.vaclavsvejcar.yhd

import kantan.csv.HeaderCodec

final case class VideoRef(id: String, title: String, description: Option[String], duration: String,
                          user: String, userLink: String)

object VideoRef {
  implicit val headerCodec: HeaderCodec[VideoRef] = HeaderCodec.caseCodec(
    "id", "title", "description", "duration", "user", "userLink"
  )(VideoRef.apply)(VideoRef.unapply)

}

