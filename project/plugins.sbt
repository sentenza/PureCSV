/**
  * ScalaJS sbt plugin
  *
  * @see http://www.scala-js.org/
  */
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.14")

/**
  * coursier
  *
  * better ivy alternative for dependency resolution
  * @see https://github.com/alexarchambault/coursier
  */
addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.0-M15-1")

/**
  * sbt-updates
  *
  * for easier dependency updates monitoring
  * @see https://github.com/rtimush/sbt-updates
  */
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.0")