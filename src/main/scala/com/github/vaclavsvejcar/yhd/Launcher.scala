/*
 * YouTube History Downloader :: Download your full YouTube watch history
 * Copyright (c) 2019-2020 Vaclav Svejcar
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
package com.github.vaclavsvejcar.yhd

import java.util.Properties

import com.github.vaclavsvejcar.yhd.downloader.HistoryDownloader
import com.github.vaclavsvejcar.yhd.parsers.SessionParser
import com.github.vaclavsvejcar.yhd.report.ReportGenerator
import com.github.vaclavsvejcar.yhd.tools.Using
import wvlet.log.LogFormatter.BareFormatter
import wvlet.log.{LogSupport, Logger}

import scala.io.Source
import scala.util.{Failure, Success, Try}

object Launcher extends App with LogSupport {

  Logger.setDefaultFormatter(BareFormatter)
  Logger.scanLogLevels

  info("-- Welcome to the Youtube History Downloader (YHD) --")
  Config.parse(args.toIndexedSeq: _*).foreach { config =>
    config.mode match {
      case Mode.Fetch =>
        if (config.debug) {
          val logLevels = new Properties()
          logLevels.setProperty(getClass.getPackage.getName, "debug")
          Logger.setLogLevels(logLevels)
        }
        info(s"Parsing Youtube cookies from '${config.cookies.getName}'...")
        val rawCookies = Try(Using(Source.fromFile(config.cookies))(_.getLines().mkString("\n")))

        rawCookies.map(SessionParser.parseCookies) match {
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
