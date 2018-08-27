package com.github.vaclavsvejcar.yhs.tools

import com.github.vaclavsvejcar.yhs.HistoryScraper
import utest._

object HistoryScraperSpec extends TestSuite {

  override def tests = Tests {

    'testParseCToken - {
      val expected = "theToken"
      val raw = s"/browse_ajax?action_continuation=1&direct_render=1&target_id=item-section-102652&continuation=$expected&foo=bar"

      HistoryScraper.parseCToken(raw) ==> expected
    }

    'testParseSessionToken -  {
      val expected = "aaa"
      val raw =
        s"""
          |yt.setConfig({
          |      'XSRF_TOKEN': "$expected",
          |      'XSRF_FIELD_NAME': "session_token",
          |
          |      'XSRF_REDIRECT_TOKEN': "bbb"  });
          |yt.setConfig('ID_TOKEN', "ccc");window.ytcfg.set('SERVICE_WORKER_KILLSWITCH', false);  yt.setConfig('THUMB_DELAY_LOAD_BUFFER', 0);
          |if (window.ytcsi) {window.ytcsi.tick("jl", null, '');}
        """.stripMargin

      HistoryScraper.parseSessionToken(raw) ==> expected
    }

    'testParseVideoId - {
      val expected = "theId"
      val raw = "/watch?v=theId"

      HistoryScraper.parseVideoId(raw) ==> expected
    }
  }
}
