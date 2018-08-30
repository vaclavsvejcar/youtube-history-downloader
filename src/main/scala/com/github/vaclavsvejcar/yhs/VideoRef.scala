package com.github.vaclavsvejcar.yhs

import kantan.csv.HeaderCodec

final case class VideoRef(id: String, title: String, description: Option[String],
                          user: String, userLink: String)

object VideoRef {
  implicit val headerCodec: HeaderCodec[VideoRef] = HeaderCodec.caseCodec(
    "id", "title", "description", "user", "userLink"
  )(VideoRef.apply)(VideoRef.unapply)

}

