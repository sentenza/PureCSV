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

import java.io.StringWriter

import com.github.tototoshi.csv.CSVWriter

/** Typeclass for Converters of A from/to B */
trait Converter[A,B] extends Serializable {
  /**
   * @param b The initial value
   * @return b converted to the type [[A]]
   * @throws IllegalArgumentException if b cannot be converted to [[A]]
   */
  def from(b: B): A

  /**
   * @param a The initial value
   * @return a converted to the type [[B]]
   * @throws IllegalArgumentException if a cannot be converted to [[B]]
   */
  def to(a: A): B
}

object Converter {
  def apply[A,B](implicit conv: Converter[A,B]): Converter[A,B] = conv
}

/** Converter from/to String */
trait StringConverter[A] extends Converter[A,String]

object StringConverter {
  def apply[A](implicit conv: StringConverter[A]): StringConverter[A] = conv
}

object StringConverterUtils {
  def mkStringConverter[A](fromF: String => A, toF: A => String) = new StringConverter[A] {
    def from(s: String): A = fromF(s)
    def to(a: A): String = toF(a)
  }
  def quoteTextIfNecessary(s: String): String = {
    val sw = new StringWriter(64)
    try {
      val writer = new CSVWriter(sw)
      writer.writeRow(Seq(s))
      removeTrailingNewLines(sw.toString)
    } finally {
      sw.close()
    }
  }
  private def removeTrailingNewLines(str: String): String = {
    val pass1 = removeLastIfMatches(str, "\n")
    removeLastIfMatches(pass1, "\r")
  }
  private def removeLastIfMatches(str: String, last: String): String = {
    if (str.endsWith(last)) str.substring(0, str.length - last.length) else str
  }
}

/** Converter from/to raw fields, represented as sequence of strings */
trait RawFieldsConverter[A] extends Converter[A,Seq[String]]

object RawFieldsConverter {
  def apply[A](implicit conv: RawFieldsConverter[A]): RawFieldsConverter[A] = conv
}