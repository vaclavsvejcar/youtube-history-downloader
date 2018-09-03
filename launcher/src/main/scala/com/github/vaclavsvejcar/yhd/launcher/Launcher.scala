package com.github.vaclavsvejcar.yhd.launcher

import com.github.vaclavsvejcar.yhd.core.report.ReportGenerator
import com.github.vaclavsvejcar.yhd.core.tools.Parsers
import com.github.vaclavsvejcar.yhd.core.HistoryDownloader
import wvlet.log.LogFormatter.BareFormatter
import wvlet.log.{LogSupport, Logger}

import scala.io.Source
import scala.util.{Failure, Success, Try}

object Launcher extends App with LogSupport {

  Logger.setDefaultFormatter(BareFormatter)

  info("-- Welcome to the Youtube History Downloader (YHD) --")
  Config.parse(args: _*) foreach { config =>
    config.mode match {
      case Mode.Fetch =>
        info(s"Parsing Youtube cookies from '${config.cookies.getName}'...")
        val rawCookies = Try(Source.fromFile(config.cookies).getLines().mkString("\n"))
        rawCookies.map(Parsers.parseCookies) match {
          case Success(cookies) => new HistoryDownloader(cookies).fetchAndSave(config.history)
          case Failure(ex) => println(
            s"Cannot read file '${config.cookies.getName}' with Youtube cookies (reason: $ex)")
        }
      case Mode.Report =>
        new ReportGenerator().generateAndWrite(config.history, config.report)
      case Mode.Other =>
        error("No mode specified (run with --help to see possible options)")
    }
  }
}
