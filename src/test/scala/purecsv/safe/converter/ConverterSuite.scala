/* Copyright 2015 Mario Pastorelli (pastorelli.mario@gmail.com)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package purecsv.safe.converter

import java.util.UUID

import org.scalatest.{FunSuite, Matchers, TryValues}
import purecsv.safe.converter.defaults.string.Trimming.{TrimAll, TrimEmpty}
import purecsv.safe.converter.defaults.string._
import purecsv.util.serializeAndDeserialize
import shapeless.{::, Generic, HNil}

import scala.util.{Failure, Success}

case class Event(ts: Long, msg: String, user: Option[Int])

class ConverterSuite extends FunSuite with Matchers with TryValues {

  test("conversion String -> Try[Boolean] works") {
    StringConverter[Boolean].tryFrom("false") should be (Success(false))
    StringConverter[Boolean].tryFrom("1") should be (Success(true))
    StringConverter[Boolean].tryFrom("TRUE") should be (Success(true))
  }

  test("conversion String <-> Try[UUID] works") {
    val uuid = UUID.randomUUID()
    StringConverter[UUID].tryFrom(uuid.toString) should be (Success(uuid))
    StringConverter[UUID].tryFrom(uuid.toString.toLowerCase) should be (Success(uuid))
    StringConverter[UUID].tryFrom(uuid.toString.toUpperCase) should be (Success(uuid))
  }

  test("conversion string -> Try[Option[Int]] works") {
    StringConverter[Option[Int]].tryFrom("") should be (Success(None))
    StringConverter[Option[Int]].tryFrom("1") should be (Success(Some(1)))
  }

  test("conversion String -> HNil works") {
    RawFieldsConverter[HNil].tryFrom(Seq.empty) should be (Success(HNil))
  }

  test("conversion String -> HList works") {
    val conv = RawFieldsConverter[String :: Int :: HNil]
    conv.tryFrom(Seq("foo","2")) should be (Success("foo" :: 2 :: HNil))
  }

  test("conversion String -> case class works") {
    val conv = RawFieldsConverter[Event]
    conv.tryFrom(Seq("2","barfoo","")) should be (Success(Event(2,"barfoo",None)))
    conv.tryFrom(Seq("2","barfoo","1")) should be (Success(Event(2,"barfoo",Some(1))))
  }

  class Event2(val ts: Long, var msg: String) {
    override def equals(o: Any): Boolean = o match {
      case other:Event2 => (this.ts == other.ts && this.msg == other.msg)
      case _ => false
    }
    override def toString: String = s"Event($ts, $msg)"
  }

  implicit val fooGeneric = new Generic[Event2] {
    override type Repr = Long :: String :: HNil
    override def from(r: Repr): Event2 = {
      val ts :: msg :: HNil = r
      new Event2(ts, msg)
    }
    override def to(t: Event2): Repr = t.ts :: t.msg :: HNil
  }

  test("conversion String -> class with custom Generic works") {
    val conv = RawFieldsConverter[Event2]
    conv.tryFrom(Seq("2","bar")) should be (Success(new Event2(2,"bar")))

    // Strings are quoted
    val event = new Event2(1,"foo")
    val expectedEvent = new Event2(1, "\"foo\"")
    conv.tryFrom(conv.to(event)) should be (Success(expectedEvent))
  }

  test("serializing a RawFieldsConverter should work") {
    val conv = RawFieldsConverter[Event]
    val convDeserialized = serializeAndDeserialize(conv)

    convDeserialized.tryFrom(Seq("2","barfoo","")) should be (Success(Event(2,"barfoo",None)))
    convDeserialized.tryFrom(Seq("2","barfoo","1")) should be (Success(Event(2,"barfoo",Some(1))))
  }
}