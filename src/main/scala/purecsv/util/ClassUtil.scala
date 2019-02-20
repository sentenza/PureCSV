package purecsv.util

import scala.reflect.runtime.universe.TypeTag
import scala.reflect.runtime.universe.typeOf
import scala.reflect.runtime.universe.termNames

object ClassUtil {
  def caseClassParams[T: TypeTag]: List[String] = {
    val tpe = typeOf[T]
    val constructorSymbol = tpe.decl(termNames.CONSTRUCTOR)
    val defaultConstructor =
      if (constructorSymbol.isMethod) {
        constructorSymbol.asMethod
      } else {
        val ctors = constructorSymbol.asTerm.alternatives
        ctors.map(_.asMethod).find(_.isPrimaryConstructor).get
      }

    List[String]() ++ defaultConstructor.paramLists.reduceLeft(_ ++ _).map {
      sym => sym.name.toString
    }
  }
}
