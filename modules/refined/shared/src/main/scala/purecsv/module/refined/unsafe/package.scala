package purecsv.module.refined

import scala.language.higherKinds

import eu.timepit.refined.api.{RefType, Validate}
import purecsv.unsafe.converter.StringConverter

package object unsafe {

  implicit def refinedConverter[R[_, _], T, P](implicit
                                               baseConverter: StringConverter[T],
                                               reftype: RefType[R],
                                               validate: Validate[T, P]) = new StringConverter[R[T, P]] {

    override def from(str: String): R[T, P] = {
      val baseValue = baseConverter.from(str)
      reftype.refine[P](baseValue) match {
        case Left(errors) => throw new IllegalArgumentException(errors)
        case Right(refinedValue) => refinedValue
      }
    }

    override def to(value: R[T, P]): String = {
      baseConverter.to(reftype.unwrap(value))
    }

  }

}
