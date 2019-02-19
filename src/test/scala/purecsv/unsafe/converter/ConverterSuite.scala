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
package purecsv.unsafe.converter

import java.util.UUID

import purecsv.unsafe.converter.defaults.rawfields._
import purecsv.unsafe.converter.defaults.string._
import purecsv.util.serializeAndDeserialize
import org.scalatest.{FunSuite, Matchers}
import shapeless.{::, Generic, HNil}

case class Event(ts: Long, msg: String)

class ConverterSuite extends FunSuite with Matchers {

  test("conversion String <-> Boolean works") {
    StringConverter[Boolean].to(true) should be ("true")
    StringConverter[Boolean].from("false") should be (false)
    StringConverter[Boolean].from("1") should be (true)
    StringConverter[Boolean].from("TRUE") should be (true)
  }

  test("conversion String <-> UUID works") {
    val uuid = UUID.randomUUID()
    StringConverter[UUID].to(uuid) should be (uuid.toString)
    StringConverter[UUID].from(uuid.toString) should be (uuid)
    StringConverter[UUID].from(uuid.toString.toLowerCase) should be (uuid)
    StringConverter[UUID].from(uuid.toString.toUpperCase) should be (uuid)
  }

  test("conversion HNil <-> String works") {
    RawFieldsConverter[HNil].to(HNil) should contain theSameElementsInOrderAs (Seq.empty)
    RawFieldsConverter[HNil].from(Seq.empty) should be (HNil)
  }

  test("conversion HList <-> String works") {
    val conv = RawFieldsConverter[String :: Int :: HNil]
    conv.to("test" :: 1 :: HNil) should contain theSameElementsInOrderAs (Seq("test","1"))
    conv.from(Seq("foo","2")) should be ("foo" :: 2 :: HNil)
  }

  test("conversion case class <-> String works") {
    val conv = RawFieldsConverter[Event]
    conv.to(Event(1,"foobar")) should contain theSameElementsInOrderAs(Seq("1","foobar"))
    conv.from(Seq("2","barfoo")) should be (Event(2,"barfoo"))
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

  test("conversion class with custom Generic <-> String works") {
    val conv = RawFieldsConverter[Event2]
    conv.to(new Event2(1,"foo")) should contain theSameElementsInOrderAs(Seq("1","foo"))
    conv.from(Seq("2","bar")) should be (new Event2(2,"bar"))

    // Strings are quoted
    val event = new Event2(1,"foo")
    conv.from(conv.to(event)) should be (event)
  }

  test("conversion String -> class with custom Generic works - with quotes") {
    val conv = RawFieldsConverter[Event2]
    // Strings are quoted
    val event = new Event2(1,"""with "quotes"""")
    conv.to(event) should be (Seq("1", "\"with \"\"quotes\"\"\""))
  }

  test("serializing a RawFieldsConverter should work") {
    val conv = RawFieldsConverter[Event]
    val convDeserialized = serializeAndDeserialize(conv)

    convDeserialized.to(Event(1,"foobar")) should contain theSameElementsInOrderAs(Seq("1","foobar"))
    convDeserialized.from(Seq("2","barfoo")) should be (Event(2,"barfoo"))
  }

  test("quoteTextIfNecessary should work") {
    StringConverterUtils.quoteTextIfNecessary("abc") shouldBe "abc"
    StringConverterUtils.quoteTextIfNecessary("text with \"quotes\"") shouldBe "\"text with \"\"quotes\"\"\""
    StringConverterUtils.quoteTextIfNecessary("a,b,c") shouldBe "\"a,b,c\""
    StringConverterUtils.quoteTextIfNecessary("a\nb") shouldBe "\"a\nb\""
  }

}
