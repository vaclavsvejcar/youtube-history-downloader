package com.github.vaclavsvejcar.yhs.report

import com.github.vaclavsvejcar.yhs.VideoRef

case class ReportData(videos: Seq[VideoRef], uniqueNo: Int)
