package purecsv.modules.refined.unsafe

import eu.timepit.refined.auto._
import eu.timepit.refined.types.numeric.PosInt
import org.scalatest.{FunSuite, Matchers}
import purecsv.unsafe._
import purecsv.unsafe.converter.StringConverter
import purecsv.module.refined.unsafe._

class RefinedUnsafeSuite extends FunSuite with Matchers {

  test("Reading a refined type works") {
    val posOne: PosInt = 1
    StringConverter[PosInt].from("1") should be (posOne)

    an [IllegalArgumentException] should be thrownBy
      StringConverter[PosInt].from("-4")
  }

  test("Writing a refined type works") {
    val posOne: PosInt = 1
    StringConverter[PosInt].to(posOne) should be ("1")
  }

}
