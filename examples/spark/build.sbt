name := "spark"

organization := "com.github.melrief"

version := "1.0"

scalaVersion := "2.11.7"

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

libraryDependencies ++= Seq(
    "org.apache.spark"   %% "spark-core" % "1.3.1"
  , "joda-time"          %  "joda-time"  % "2.7"
  , "com.github.melrief" %% "purecsv"    % "0.0.4"
  , "org.scalatest"      %% "scalatest"  % "2.2.1" % "test"

)

