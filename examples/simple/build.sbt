name := "spark"

organization := "com.github.melrief"

version := "1.0"
//
scalaVersion := "2.11.7"

crossScalaVersions := Seq("2.10.5", "2.11.7")

scalacOptions ++= Seq("-feature", "-deprecation")


resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

libraryDependencies ++= Seq(
    "com.github.melrief" %% "purecsv"    % "0.0.4"
  , compilerPlugin("org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full)
  , "org.scalatest"      %% "scalatest"  % "2.2.1" % "test"
)

