import sbt.Keys._

lazy val buildSettings = Seq(
  organization := "io.kontainers",
  scalaVersion := "2.13.8",
  crossScalaVersions := Seq("2.11.12", "2.12.15", scalaVersion.value)
)

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  Test/ publishArtifact := false,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  publishTo := Some(
    if (isSnapshot.value)
      Opts.resolver.sonatypeSnapshots
    else
      Opts.resolver.sonatypeStaging),
    pomIncludeRepository := { x => false },
    pomExtra := (
    <url>https://github.com/kontainers/PureCSV</url>
      <licenses>
        <license>
          <name>Apache2</name>
          <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:kontainers/PureCSV.git</url>
        <connection>scm:git:git@github.com:kontainers/PureCSV.git</connection>
      </scm>
      <developers>
        <developer>
          <id>sentenza</id>
          <name>Alfredo Torre</name>
        </developer>
        <developer>
          <id>melrief</id>
          <name>Mario Pastorelli</name>
        </developer>
      </developers>
  )
)

lazy val pureCSV = project.in(file(".")).
  settings(buildSettings).
  settings(publishSettings).
  settings(
    name := "purecsv",
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    scalacOptions ++= Seq("-feature", "-deprecation"),
    libraryDependencies ++= Seq(
      "com.chuusai" %% "shapeless" % "2.3.7",
      "com.github.tototoshi" %% "scala-csv" % "1.3.10",
      "org.scalatest" %% "scalatest" % "3.2.10" % Test
    )
  )
