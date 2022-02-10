package purecsv.util

import scala.reflect.{ClassTag, NameTransformer}

object ClassUtil {
  def caseClassParams[T: ClassTag]: Seq[String] = {
    val clz = implicitly[ClassTag[T]].runtimeClass
    clz.getDeclaredFields
      .map(_.getName)
      .filterNot(_.startsWith("$"))
      .map(NameTransformer.decode)
      .toSeq
  }
}
