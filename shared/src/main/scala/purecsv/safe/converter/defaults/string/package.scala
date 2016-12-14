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
package purecsv.safe.converter.defaults

import scala.util.{Success, Try}


package object string {
  import purecsv.safe.converter.StringConverter
  import purecsv.safe.converter.StringConverterUtils.mkStringConverter
  import purecsv.unsafe.converter.defaults.string.{strToBool, strToChar}

  implicit val boolc:   StringConverter[Boolean] = mkStringConverter(s => Try(strToBool(s)),_.toString)
  implicit val bytec:   StringConverter[Byte]    = mkStringConverter(s => Try(s.toByte),_.toString)
  implicit val charc:   StringConverter[Char]    = mkStringConverter(s => Try(strToChar(s)),_.toString)
  implicit val doublec: StringConverter[Double]  = mkStringConverter(s => Try(s.toDouble),_.toString)
  implicit val floatc:  StringConverter[Float]   = mkStringConverter(s => Try(s.toFloat),_.toString)
  implicit val intc:    StringConverter[Int]     = mkStringConverter(s => Try(s.toInt),_.toString)
  implicit val longc:   StringConverter[Long]    = mkStringConverter(s => Try(s.toLong),_.toString)
  implicit val shortc:  StringConverter[Short]   = mkStringConverter(s => Try(s.toShort),_.toString)
  implicit val stringc: StringConverter[String]  = new StringConverter[String] {
    override def tryFrom(s: String): Try[String] = Success(s)
    override def to(s: String): String = "\"" + s.replaceAllLiterally("\"", "\"\"") + "\""
  }

  implicit def optionc[A](implicit ac: StringConverter[A]): StringConverter[Option[A]] = new StringConverter[Option[A]] {
    override def tryFrom(s: String): Try[Option[A]] = s match {
      case "" => Success(None)
      case s  => ac.tryFrom(s).map(Some(_))
    }
    override def to(v: Option[A]): String = v.map(ac.to).getOrElse("")
  }
}
