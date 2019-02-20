package purecsv.config

import org.scalatest.{FunSuite, MustMatchers}

class TrimmingTest extends FunSuite with MustMatchers {
  test("no action should return same string") {
    Trimming.NoAction.trim("   some value     ") mustBe "   some value     "
  }

  test("trim empty should return empty string when given string contains whitespace only") {
    Trimming.TrimEmpty.trim("       ") mustBe ""
  }

  test("trim empty should not trim given string if it contains something more than whitespace") {
    Trimming.TrimEmpty.trim("          a  ") mustBe "          a  "
  }

  test("trim all should trim given string") {
    Trimming.TrimAll.trim("      s     ") mustBe "s"
  }

  test("trim all should return empty string when given string contains whitespace only") {
    Trimming.TrimAll.trim("         ") mustBe ""
  }
}
