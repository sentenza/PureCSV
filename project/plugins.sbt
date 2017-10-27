/**
  * ScalaJS sbt plugin
  *
  * @see http://www.scala-js.org/
  */
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.20")

/**
  * coursier
  *
  * better ivy alternative for dependency resolution
  * @see https://github.com/alexarchambault/coursier
  */
addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.0-RC12")

/**
  * sbt-updates
  *
  * for easier dependency updates monitoring
  * @see https://github.com/rtimush/sbt-updates
  */
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.3")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.0")
