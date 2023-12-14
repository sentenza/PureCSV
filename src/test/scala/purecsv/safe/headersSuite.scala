package purecsv.safe

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers
import purecsv.config.Headers

import scala.util.Success

class headersSuite extends AnyFunSuite with Matchers {
  private final case class TestUser(id: Int, name: String, email: Option[String])

  test("should parse csv without header") {
    val csv = "42,joey,joey@tribiani.com"

    readCsv(csv, Headers.None) must contain only Success(TestUser(42, "joey", Some("joey@tribiani.com")))
  }

  test("should skip header and parse csv entry") {
    val csv = """id,name,email
        |42,rachel,rachel@green.com""".stripMargin

    readCsv(csv, Headers.ReadAndIgnore) must contain only Success(TestUser(42, "rachel", Some("rachel@green.com")))
  }

  test("should parse csv entry with headers") {
    val csv = """id,name,email
        |42,chandler,chandler@bing.com""".stripMargin

    readCsv(csv, Headers.ParseHeaders) must contain only Success(
      TestUser(42, "chandler", Some("chandler@bing.com"))
    )
  }

  test("should parse csv entry with headers even if fields order doesn't match class's fields order") {
    val csv = """email,id,name
        |ross@geller.com,42,ross""".stripMargin

    readCsv(csv, Headers.ParseHeaders) must contain only Success(TestUser(42, "ross", Some("ross@geller.com")))
  }

  test("should parse csv entry with headers even if fields csv contains additional fields") {
    val csv = """email,age,id,name
        |ross@geller.com,34,42,ross""".stripMargin

    readCsv(csv, Headers.ParseHeaders) must contain only Success(TestUser(42, "ross", Some("ross@geller.com")))
  }

  test("should parse csv entry with headers when mapping is provided for headers") {
    val csv = """email address,age,id,name
        |ross@geller.com,34,42,ross""".stripMargin

    readCsv(csv, Headers.ParseHeaders, Map("email address" -> "email")) must contain only Success(
      TestUser(42, "ross", Some("ross@geller.com"))
    )
  }

  private def readCsv(csv: String, headers: Headers, headerMapping: Map[String, String] = Map.empty) =
    CSVReader[TestUser].readCSVFromString(csv, headers = headers, headerMapping = headerMapping)
}
