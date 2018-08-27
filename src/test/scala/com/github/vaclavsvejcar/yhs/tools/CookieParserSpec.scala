package com.github.vaclavsvejcar.yhs.tools

import utest._

object CookieParserSpec extends TestSuite {

  override def tests = Tests(

    'testParse - {
      import CookiesParser.parse

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

      parse(testData1) ==> expected
      parse(testData2) ==> expected
      parse(testData3) ==> Map.empty
    }
  )
}
