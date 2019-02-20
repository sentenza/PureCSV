package purecsv.safe

import org.scalatest.{FlatSpec, FunSuite, MustMatchers}
import purecsv.safe._
import purecsv.config.Trimming
import purecsv.config.Trimming.{NoAction, TrimAll, TrimEmpty}

import scala.util.Success

class trimmingSuite extends FunSuite with MustMatchers {
  private final case class Account(id: Int, name: String, surname: Option[String])

  test("should convert string containing whitespace only to none when trimming empty") {
    val csv = """id,name,surname
                |1,some name,   """.stripMargin

    readCsv(csv, TrimEmpty) must contain only Success(Account(1, "some name", None))
  }

  test("should convert given string to some trimmed string when trimming all") {
    val csv = """id,name,surname
                |1,some name,    some surname    """.stripMargin

    readCsv(csv, TrimAll) must contain only Success(Account(1, "some name", Some("some surname")))
  }

  test(" should return some string as it is when trimming is set to no action") {
    val csv = """id,name,surname
                |1,some name,   some surname    """.stripMargin

    readCsv(csv, NoAction) must contain only Success(Account(1, "some name", Some("   some surname    ")))
  }

  test("should return trimmed string when trimming all") {
    val csv = """id,name,surname
                |1,    some name    ,some surname""".stripMargin

    readCsv(csv, TrimAll) must contain only Success(Account(1, "some name", Some("some surname")))
  }

  test("should return empty string when string that is being converted contain whitespace only") {
    val csv = """id,name,surname
                |1,      ,some surname""".stripMargin

    readCsv(csv, TrimEmpty) must contain only Success(Account(1, "", Some("some surname")))
  }

  test("should return given string as it is when trimming is set to no action") {
    val csv = """id,name,surname
                |1,    some name    ,some surname""".stripMargin

    readCsv(csv, NoAction) must contain only Success(Account(1, "    some name    ", Some("some surname")))
  }

  private def readCsv(csv: String, trimming: Trimming) =
    CSVReader[Account].readCSVFromString(csv, ',', trimming = trimming)
}
