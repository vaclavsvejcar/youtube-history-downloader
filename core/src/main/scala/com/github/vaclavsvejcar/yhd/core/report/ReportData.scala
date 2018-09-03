package com.github.vaclavsvejcar.yhd.core.report

import com.github.vaclavsvejcar.yhd.core.VideoRef

case class ReportData(videos: Seq[VideoRef], uniqueNo: Int)
