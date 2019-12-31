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
package com.github.vaclavsvejcar.yhd.parsers

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SessionParserSpec extends AnyFlatSpec with Matchers {

  "Session parser" should "parse cookies from raw input" in {
    val testData1 =
      """
        |Cookie: NAME1=value1; NAME2=value2;
        |NAME3=value3=withEqualSign
        """.stripMargin
    val testData2 =
      """
        |NAME1   = value1;NAME2=value2;
        |
        |NAME3=value3=withEqualSign
        """.stripMargin
    val testData3 = "completelyInvalidInput"

    val expected = Map("NAME1" -> "value1", "NAME2" -> "value2", "NAME3" -> "value3=withEqualSign")

    SessionParser.parseCookies(testData1) shouldEqual expected
    SessionParser.parseCookies(testData2) shouldEqual expected
    SessionParser.parseCookies(testData3) shouldEqual Map.empty
  }

  it should "parse CToken from raw input" in {
    val expected = "theToken"
    val raw =
      s"/browse_ajax?action_continuation=1&direct_render=1&target_id=item-section-102652&continuation=$expected&foo=bar"

    SessionParser.parseCToken(raw) shouldEqual expected
  }

  it should "parse session token from raw input" in {
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

    SessionParser.parseSessionToken(raw) shouldEqual expected
  }

}
