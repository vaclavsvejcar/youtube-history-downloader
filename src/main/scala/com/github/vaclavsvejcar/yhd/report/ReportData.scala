package com.github.vaclavsvejcar.yhd.report

import com.github.vaclavsvejcar.yhd.VideoRef

case class ReportData(videos: Seq[VideoRef], uniqueNo: Int)

object ReportData {
  def from(videos: Seq[VideoRef]): ReportData = ReportData(videos, videos.distinct.size)
}
