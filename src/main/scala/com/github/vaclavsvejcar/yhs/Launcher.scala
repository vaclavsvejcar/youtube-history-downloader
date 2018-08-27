package com.github.vaclavsvejcar.yhs

import com.github.vaclavsvejcar.yhs.tools.Parsers
import wvlet.log.LogFormatter.BareFormatter
import wvlet.log.{LogSupport, Logger}

import scala.io.Source
import scala.util.{Failure, Success, Try}

object Launcher extends App with LogSupport {

  Logger.setDefaultFormatter(BareFormatter)

  val CookiesFile = "cookies.txt"

  info("-- Welcome to the Youtube History Scrapper --")

  info(s"Parsing Youtube cookies from '$CookiesFile'...")
  Try(Source.fromFile(CookiesFile).getLines().mkString("\n")).map(Parsers.parseCookies) match {
    case Success(cookies) => new HistoryScraper(cookies).fetchHistory()
    case Failure(ex) =>
      println(s"Cannot read file '$CookiesFile' with Youtube cookies (reason: $ex)")
  }
}
