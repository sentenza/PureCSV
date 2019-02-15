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

import purecsv.safe.converter.defaults.string.Trimming
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

  implicit def boolc:   StringConverter[Boolean] = mkStringConverter((s, t) => strToBool(t.trim(s)),  _.toString)
  implicit def bytec:   StringConverter[Byte]    = mkStringConverter((s, t) => t.trim(s).toByte, _.toString)
  implicit def charc:   StringConverter[Char]    = mkStringConverter((s, t) => strToChar(t.trim(s)), _.toString)
  implicit def doublec: StringConverter[Double]  = mkStringConverter((s, t) => t.trim(s).toDouble, _.toString)
  implicit def floatc:  StringConverter[Float]   = mkStringConverter((s, t) => t.trim(s).toFloat, _.toString)
  implicit def intc:    StringConverter[Int]     = mkStringConverter((s, t) => t.trim(s).toInt, _.toString)
  implicit def longc:   StringConverter[Long]    = mkStringConverter((s, t) => t.trim(s).toLong, _.toString)
  implicit def shortc:  StringConverter[Short]   = mkStringConverter((s, t) => t.trim(s).toShort, _.toString)
  implicit def uuidc:   StringConverter[UUID]    = mkStringConverter((s, t) => UUID.fromString(t.trim(s)), _.toString)
  implicit def stringc: StringConverter[String]  = new StringConverter[String] {
    override def from(s: String, trimming: Trimming): String = s
    override def to(s: String): String = "\"" + s.replaceAllLiterally("\"", "\"\"") + "\""
  }

  implicit def optionc[A](implicit ac: StringConverter[A]): StringConverter[Option[A]] = new StringConverter[Option[A]] {
    override def from(s: String, trimming: Trimming): Option[A] = s match {
      case "" => None
      case x  => Some(ac.from(x, trimming))
    }
    override def to(v: Option[A]): String = v.map(ac.to).getOrElse("")
  }

}
