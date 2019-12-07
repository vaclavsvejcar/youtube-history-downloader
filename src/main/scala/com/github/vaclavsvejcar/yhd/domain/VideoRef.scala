/*
 * YouTube History Downloader :: Download your full YouTube watch history
 * Copyright (c) 2019 Vaclav Svejcar
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
