package com.github.vaclavsvejcar.yhd

import com.github.vaclavsvejcar.yhd.downloader.HistoryDownloader
import com.github.vaclavsvejcar.yhd.report.ReportGenerator
import com.github.vaclavsvejcar.yhd.tools.{Parsers, Using}
import wvlet.log.LogFormatter.BareFormatter
import wvlet.log.{LogSupport, Logger}

import scala.io.Source
import scala.util.{Failure, Success, Try}

object Launcher extends App with LogSupport {

  Logger.setDefaultFormatter(BareFormatter)

  info("-- Welcome to the Youtube History Downloader (YHD) --")
  Config.parse(args.toIndexedSeq: _*).foreach { config =>
    config.mode match {
      case Mode.Fetch =>
        info(s"Parsing Youtube cookies from '${config.cookies.getName}'...")
        val rawCookies = Try(Using(Source.fromFile(config.cookies))(_.getLines().mkString("\n")))

        rawCookies.map(Parsers.parseCookies) match {
          case Success(cookies) => new HistoryDownloader(cookies, config).fetchAndSave()
          case Failure(ex) =>
            println(s"Cannot read file '${config.cookies.getName}' with Youtube cookies (reason: $ex)")
        }
      case Mode.Report =>
        new ReportGenerator(config).generateAndWrite()
      case Mode.Other =>
        error("No mode specified (run with --help to see possible options)")
    }
  }
}
