package purecsv.safe

import org.scalatest.TryValues
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers
import purecsv.config.{Headers, Trimming}
import purecsv.config.Trimming.NoAction

import scala.util.Success

class absentColumnsSuite extends AnyFunSuite with Matchers with TryValues {
  final case class TestAccount(id: Int, name: String, surname: Option[String])

  test("should set none for the missing column") {
    val csv = """id,name
                |42,joey
                |55,rachel""".stripMargin

    readCsv(csv, NoAction) must contain only (Success(TestAccount(42, "joey", None)),
    Success(TestAccount(55, "rachel", None)))
  }

  test("should set some value when column is present") {
    val csv = """id,name,surname
                |42,joey,tribiani
                |55,rachel,green
                |66,chandler,bing""".stripMargin

    readCsv(csv, NoAction) must contain only (Success(TestAccount(42, "joey", Some("tribiani"))),
    Success(TestAccount(55, "rachel", Some("green"))),
    Success(TestAccount(66, "chandler", Some("bing"))))
  }

  test("should set empty string for the missing column") {
    val csv = """id,surname
                |42,tribiani
                |55,green""".stripMargin

    readCsv(csv, NoAction) must contain only (Success(TestAccount(42, "", Some("tribiani"))),
    Success(TestAccount(55, "", Some("green"))))
  }

  test("should fail when column is missing and skipping header is enabled") {
    val csv = """id,surname
                |42,ross""".stripMargin

    readCsv(csv, NoAction, headers = Headers.None).foreach(_.failure.exception mustBe an[IllegalArgumentException])
  }

  private def readCsv(csv: String, trimming: Trimming, headers: Headers = Headers.ParseHeaders) =
    CSVReader[TestAccount].readCSVFromString(csv, ',', trimming = trimming, headers)
}
