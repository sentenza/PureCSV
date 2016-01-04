import sbt.Keys._

lazy val pureCSV = crossProject.crossType(CrossType.Full).in(new File(".")).
  settings(
    name := "purecsv",
    version := "0.0.4",
    organization := "com.github.melrief",
    publishMavenStyle := true,
    publishArtifact in Test := false,
    scalaVersion := "2.11.7",
    scalacOptions ++= Seq("-feature", "-deprecation"),
    libraryDependencies ++= Seq(
      "com.chuusai" %% "shapeless" % "2.2.5",
      compilerPlugin("org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full),
      "org.scalatest" %% "scalatest" % "3.0.0-M7" % "test"
      ),
    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases"),
      Resolver.sonatypeRepo("snapshots")
    ),
    publishTo <<= version { v: String =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
        else
          Some("releases" at nexus + "service/local/staging/deploy/maven2")
      },
      pomIncludeRepository := { x => false },
      pomExtra := (
      <url>https://github.com/melrief/PureCSV</url>
        <licenses>
          <license>
            <name>Apache2</name>
            <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <scm>
          <url>git@github.com:melrief/PureCSV.git</url>
          <connection>scm:git:git@github.com:melrief/PureCSV.git</connection>
        </scm>
        <developers>
          <developer>
            <id>melrief</id>
            <name>Mario Pastorelli</name>
          </developer>
        </developers>
    )
  ).
  jvmSettings(
    libraryDependencies += "net.sf.opencsv" % "opencsv" % "2.3"
  ).
  jsSettings(
  )

lazy val pureCSVJVM = pureCSV.jvm
lazy val pureCSVJS  = pureCSV.js
