package com.github.vaclavsvejcar.yhd.domain

case class ReportData(videos: Seq[VideoRef], uniqueNo: Int)

object ReportData {
  def from(videos: Seq[VideoRef]): ReportData = ReportData(videos, videos.distinct.size)
}
