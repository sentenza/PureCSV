package purecsv.util

import org.scalatest.{FunSuite, MustMatchers}
import purecsv.util.ClassUtil.caseClassParams

class ClassUtilSuite extends FunSuite with MustMatchers {
  private final case class TestClass(id: Int, name: String, email: Option[String])
  private final case class EmptyTestClass()

  test("it should return list containing names of case class's fields") {
    caseClassParams[TestClass] must contain only ("id", "name", "email")
  }

  test("it should return empty list when case class has no fields") {
    caseClassParams[EmptyTestClass] mustBe empty
  }
}
