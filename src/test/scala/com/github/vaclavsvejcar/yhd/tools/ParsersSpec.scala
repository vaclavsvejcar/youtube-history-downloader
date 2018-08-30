package com.github.vaclavsvejcar.yhd.tools

import com.github.vaclavsvejcar.yhd.VideoRef
import net.ruippeixotog.scalascraper.browser.JsoupBrowser.JsoupDocument
import org.jsoup.Jsoup
import utest._

import scala.io.Source

object ParsersSpec extends TestSuite {

  override def tests = Tests {

    import Parsers._

    'testParseCookies - {
      val testData1 =
        """
          |Cookie: NAME1=value1; NAME2=value2;
          |NAME3=value3
        """.stripMargin
      val testData2 =
        """
          |NAME1   = value1;NAME2=value2;
          |
          |NAME3=value3
        """.stripMargin
      val testData3 = "completelyInvalidInput"

      val expected = Map("NAME1" -> "value1", "NAME2" -> "value2", "NAME3" -> "value3")

      parseCookies(testData1) ==> expected
      parseCookies(testData2) ==> expected
      parseCookies(testData3) ==> Map.empty
    }

    'testParseCToken - {
      val expected = "theToken"
      val raw = s"/browse_ajax?action_continuation=1&direct_render=1&target_id=item-section-102652&continuation=$expected&foo=bar"

      parseCToken(raw) ==> expected
    }

    'testParseSessionToken - {
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

      parseSessionToken(raw) ==> expected
    }

    'testParseVideoRefs - {
      'testRemovedVideo - {
        val source = Source.fromResource("removed-video-snippet.html").getLines().mkString("\n")
        val document = JsoupDocument(Jsoup.parse(source))
        val expected = VideoRef("aabbccdd", "The Video Title", None,
          "4:48", "John Smith", "/user/johnSmith")

        val results = parseVideoRefs(document)
        results.size ==> 1
        results.head ==> expected
      }

      'testExistingVideo - {
        val source = Source.fromResource("existing-video-snippet.html").getLines().mkString("\n")
        val document = JsoupDocument(Jsoup.parse(source))
        val expected = VideoRef("aabbccdd", "The Video Title", Some("The Video Description"),
          "8:09", "The Channel Name", "/channel/theChannel")

        val results = parseVideoRefs(document)
        results.size ==> 1
        results.head ==> expected
      }

    }

  }
}
