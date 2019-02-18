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
package purecsv.unsafe.converter.defaults

import java.util.UUID

import purecsv.unsafe.converter.StringConverterUtils

package object string {
  import StringConverterUtils.mkStringConverter
  import purecsv.unsafe.converter.StringConverter

  def strToBool(s: String): Boolean = s match {
    case "1" | "true"  | "True"  | "TRUE" => true
    case "0" | "false" | "False" | "FALSE" => false
    case _ => throw new IllegalArgumentException(s"'$s' cannot be converter to boolean")
  }

  def strToChar(s: String): Char = {
    if (s.length == 1) return s.head
    else throw new IllegalArgumentException(s"'$s' cannot be converted to char")
  }

  implicit val boolc:   StringConverter[Boolean] = mkStringConverter(strToBool,_.toString)
  implicit val bytec:   StringConverter[Byte]    = mkStringConverter(_.toByte,_.toString)
  implicit val charc:   StringConverter[Char]    = mkStringConverter(strToChar,_.toString)
  implicit val doublec: StringConverter[Double]  = mkStringConverter(_.toDouble,_.toString)
  implicit val floatc:  StringConverter[Float]   = mkStringConverter(_.toFloat,_.toString)
  implicit val intc:    StringConverter[Int]     = mkStringConverter(_.toInt,_.toString)
  implicit val longc:   StringConverter[Long]    = mkStringConverter(_.toLong,_.toString)
  implicit val shortc:  StringConverter[Short]   = mkStringConverter(_.toShort,_.toString)
  implicit val uuidc:   StringConverter[UUID]    = mkStringConverter(UUID.fromString,_.toString)
  implicit val stringc: StringConverter[String]  = new StringConverter[String] {
    override def from(s: String): String = s
    override def to(s: String): String = "\"" + s.replaceAllLiterally("\"", "\"\"") + "\""
  }

  implicit def optionc[A](implicit ac: StringConverter[A]): StringConverter[Option[A]] = new StringConverter[Option[A]] {
    override def from(s: String): Option[A] = s match {
      case "" => None
      case x  => Some(ac.from(x))
    }
    override def to(v: Option[A]): String = v.map(ac.to).getOrElse("")
  }

}
