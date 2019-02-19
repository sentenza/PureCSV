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

import purecsv.unsafe.converter.StringConverter
import shapeless.{::, Generic, HList, HNil}


package object rawfields {

  import purecsv.unsafe.converter.RawFieldsConverter

  implicit val deriveHNil = new RawFieldsConverter[HNil] {
    override def from(s: Seq[String]): HNil = s match {
      case Nil => HNil
      case _       => throw new IllegalArgumentException(s"'$s' cannot be converted to HNil")
    }
    override def to(a: HNil): Seq[String] = Seq.empty
  }

  implicit def deriveHCons[V, T <: HList]
  (implicit sc:  StringConverter[V],
   fto: RawFieldsConverter[T])
  : RawFieldsConverter[V :: T] = new RawFieldsConverter[V :: T] {
    override def from(s: Seq[String]): ::[V, T] = s match {
      case Nil => throw new IllegalArgumentException(s"The empty String cannot be converted to HList")
      case _   => sc.from(s.head) :: fto.from(s.tail)
    }

    override def to(a: ::[V, T]): Seq[String] = sc.to(a.head) +: fto.to(a.tail)
  }

  implicit def deriveClass[A, R](implicit gen: Generic.Aux[A, R],
                                 conv: RawFieldsConverter[R])
  : RawFieldsConverter[A] = new RawFieldsConverter[A] {
    override def from(s: Seq[String]): A = gen.from(conv.from(s))
    override def to(a: A): Seq[String] = conv.to(gen.to(a))
  }
}