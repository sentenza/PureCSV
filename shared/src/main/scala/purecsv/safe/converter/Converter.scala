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

import purecsv.safe.converter.defaults.string.Trimming
import purecsv.safe.converter.defaults.string.Trimming.NoAction

import scala.util.{Failure, Success, Try}


/**
 * A version of [[purecsv.unsafe.converter.Converter]] with a special from method
 * for safe conversions. The method [[Converter!from()]] is defined starting from
 * [[Converter!tryFrom()]] but it throw [[IllegalArgumentException]] when
 * the result is [[Failure]]
 */
trait Converter[A,B] extends purecsv.unsafe.converter.Converter[A,B] {
  /**
   * @param b The starting value from which we try the conversion to [[A]]
   * @return A value of type [[A]] wrapped in [[Success]] if the conversion is successful else [[Failure]] with the
   *         error
   */
  def tryFrom(b: B, trimming: Trimming = NoAction): Try[A]
  final override def from(b: B, trimming: Trimming): A = tryFrom(b, trimming) match {
    case Success(a) => a
    case Failure(f) => throw new IllegalArgumentException(s"'$b' cannot be converted because: $f")
  }
}

/** Converter from/to String */
trait StringConverter[A] extends Converter[A,String]

object StringConverter {
  def apply[A](implicit conv: StringConverter[A]): StringConverter[A] = conv
}

object StringConverterUtils {
  def mkStringConverter[A](fromF: (String, Trimming) => Try[A], toF: A => String) = new StringConverter[A] {
    def tryFrom(s: String, trimming: Trimming = NoAction): Try[A] = fromF(s, trimming)
    def to(a: A): String = toF(a)
  }
}

/** Converter from/to raw fields, represented as sequence of strings */
trait RawFieldsConverter[A] extends Converter[A,Seq[String]]

object RawFieldsConverter {
  def apply[A](implicit conv: RawFieldsConverter[A]): RawFieldsConverter[A] = conv
}