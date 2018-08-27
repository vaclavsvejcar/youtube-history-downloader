package com.github.vaclavsvejcar.yhs

import com.github.vaclavsvejcar.yhs.tools.CookiesParser

import scala.io.Source
import scala.util.{Failure, Success, Try}

object Launcher extends App {

  val CookiesFile = "cookies.txt"

  println("-- Welcome to the Youtube History Scrapper --")

  println(s"Parsing Youtube cookies from '$CookiesFile'...")
  Try(Source.fromFile(CookiesFile).getLines().mkString("\n")).map(CookiesParser.parse) match {
    case Success(cookies) => new HistoryScraper(cookies).fetchHistory()
    case Failure(ex) =>
      println(s"Cannot read file '$CookiesFile' with Youtube cookies (reason: $ex)")
  }
}
