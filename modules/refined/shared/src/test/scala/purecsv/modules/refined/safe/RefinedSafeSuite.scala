package purecsv.modules.refined.safe

import scala.util.Success

import eu.timepit.refined.auto._
import eu.timepit.refined.types.numeric.PosInt
import org.scalatest.{FunSuite, Matchers}
import purecsv.safe._
import purecsv.safe.converter.StringConverter
import purecsv.module.refined.safe._

class RefinedSafeSuite extends FunSuite with Matchers {

  test("Reading a refined type works") {
    val posOne: PosInt = 1
    StringConverter[PosInt].tryFrom("1")  should be (Success(posOne))
    StringConverter[PosInt].tryFrom("-4") should be a 'failure
  }

  test("Writing a refined type works") {
    val posOne: PosInt = 1
    StringConverter[PosInt].to(posOne) should be ("1")
  }
}
