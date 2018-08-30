package com.github.vaclavsvejcar.yhd.report

import com.github.vaclavsvejcar.yhd.VideoRef

case class ReportData(videos: Seq[VideoRef], uniqueNo: Int)
