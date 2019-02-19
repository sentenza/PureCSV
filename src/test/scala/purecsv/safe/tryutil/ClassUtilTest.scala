package purecsv.safe.tryutil

import org.scalatest.{FunSuite, MustMatchers}
import purecsv.safe.tryutil.ClassUtil.caseClassParams

class ClassUtilTest extends FunSuite with MustMatchers {
  private final case class TestClass(id: Int, name: String, email: Option[String])
  private final case class EmptyTestClass()

  test("it should return list containing names of case class's fields") {
    caseClassParams[TestClass] must contain only ("id", "name", "email")
  }

  test("it should return empty list when case class has no fields") {
    caseClassParams[EmptyTestClass] mustBe empty
  }
}
