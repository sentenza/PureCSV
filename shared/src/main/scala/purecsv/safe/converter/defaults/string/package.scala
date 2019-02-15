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

import java.util.UUID

import scala.util.{Success, Try}


package object string {
  import purecsv.safe.converter.StringConverter
  import purecsv.safe.converter.StringConverterUtils.mkStringConverter
  import purecsv.unsafe.converter.defaults.string.{strToBool, strToChar}

  sealed trait Trimming {
    def trim(s: String): String
  }

  object Trimming {
    object NoAction extends Trimming {
      override def trim(s: String): String = s
    }

    object TrimEmpty extends Trimming {
      override def trim(s: String): String = if (s.matches("\\s+")) s.trim else s
    }

    object TrimAll extends Trimming {
      override def trim(s: String): String = s.trim
    }
  }

  implicit val boolc:   StringConverter[Boolean] = mkStringConverter((s, t) => Try(strToBool(t.trim(s))), _.toString)
  implicit val bytec:   StringConverter[Byte]    = mkStringConverter((s, t) => Try(t.trim(s).toByte), _.toString)
  implicit val charc:   StringConverter[Char]    = mkStringConverter((s, t) => Try(strToChar(t.trim(s))), _.toString)
  implicit val doublec: StringConverter[Double]  = mkStringConverter((s, t) => Try(t.trim(s).toDouble), _.toString)
  implicit val floatc:  StringConverter[Float]   = mkStringConverter((s, t) => Try(t.trim(s).toFloat), _.toString)
  implicit val intc:    StringConverter[Int]     = mkStringConverter((s, t) => Try(t.trim(s).toInt), _.toString)
  implicit val longc:   StringConverter[Long]    = mkStringConverter((s, t) => Try(t.trim(s).toLong), _.toString)
  implicit val shortc:  StringConverter[Short]   = mkStringConverter((s, t) => Try(t.trim(s).toShort), _.toString)
  implicit val uuidc:   StringConverter[UUID]    = mkStringConverter((s, t) => Try(UUID.fromString(t.trim(s))), _.toString)
  implicit val stringc: StringConverter[String]  = new StringConverter[String] {
    override def tryFrom(s: String, trimming: Trimming): Try[String] = Success(trimming.trim(s))
    override def to(s: String): String = "\"" + s.replaceAllLiterally("\"", "\"\"") + "\""
  }

  implicit def optionc[A](implicit ac: StringConverter[A]): StringConverter[Option[A]] = new StringConverter[Option[A]] {
    override def tryFrom(s: String, trimming: Trimming): Try[Option[A]] = trimming.trim(s) match {
      case "" => Success(None)
      case s  => ac.tryFrom(s, trimming).map(Some(_))
    }

    override def to(v: Option[A]): String = v.map(ac.to).getOrElse("")
  }
}
