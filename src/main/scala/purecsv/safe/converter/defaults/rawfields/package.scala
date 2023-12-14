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

import purecsv.safe.converter.StringConverter
import shapeless.{::, Generic, HList, HNil}

import scala.util.{Failure, Success, Try}

package object rawfields {

  import purecsv.safe.converter.RawFieldsConverter

  def illegalConversion(what: String, typ: String) = {
    Failure(new IllegalArgumentException(s"$what cannot be converter to a value of type $typ"))
  }

  implicit val deriveHNil: RawFieldsConverter[HNil] = new RawFieldsConverter[HNil] {
    override def tryFrom(s: Seq[String]): Try[HNil] = s match {
      case Nil => Success(HNil)
      case _   => illegalConversion(s.mkString("[", ", ", "]"), "HNil")
    }
    override def to(a: HNil): Seq[String] = Seq.empty
  }

  implicit def deriveHCons[V, T <: HList](implicit
      sc: StringConverter[V],
      fto: RawFieldsConverter[T]
  ): RawFieldsConverter[V :: T] = new RawFieldsConverter[V :: T] {
    override def tryFrom(s: Seq[String]): Try[V :: T] = s match {
      case Nil => illegalConversion("", classOf[V :: T].toString)
      case _ =>
        for {
          head <- sc.tryFrom(s.head)
          tail <- fto.tryFrom(s.tail)
        } yield head :: tail
    }

    override def to(a: ::[V, T]): Seq[String] = sc.to(a.head) +: fto.to(a.tail)
  }

  implicit def deriveClass[A, R](implicit
      gen: Generic.Aux[A, R],
      conv: RawFieldsConverter[R]
  ): RawFieldsConverter[A] = new RawFieldsConverter[A] {
    override def tryFrom(s: Seq[String]): Try[A] = conv.tryFrom(s).map(gen.from)
    override def to(a: A): Seq[String]           = conv.to(gen.to(a))
  }
}
