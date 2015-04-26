name := "purecsv"

version := "0.0.1"

scalaVersion := "2.11.6"

scalacOptions ++= Seq("-feature", "-deprecation")

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % "2.1.0",
  "net.sf.opencsv" % "opencsv" % "2.3",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test"
)