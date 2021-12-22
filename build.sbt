import sbt.Keys._

lazy val buildSettings = Seq(
  organization := "com.github.melrief",
  scalaVersion := "2.12.1",
  crossScalaVersions := Seq("2.10.5", "2.11.8", "2.12.1"),
  javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
  scalacOptions ++= Seq("-feature", "-deprecation")
)

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishArtifact in Test := false,
  publishTo := Some(
    if (isSnapshot.value)
      Opts.resolver.sonatypeSnapshots
    else
      Opts.resolver.sonatypeStaging),
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
)

lazy val noPublishSettings = Seq(
  publish := { },
  publishLocal := { },
  publishArtifact := false
)

lazy val root = project.in(file(".")).
  aggregate(pureCSVJVM, pureCSVJS, refinedJVM, refinedJS).
  settings(buildSettings).
  settings(publishSettings)

lazy val pureCSV = crossProject.crossType(CrossType.Full).in(file("purecsv")).
  settings(buildSettings).
  settings(publishSettings).
  settings(
    name := "purecsv",
    libraryDependencies ++= Seq(
      "com.chuusai" %% "shapeless" % "2.3.2",
      compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
      "org.scalatest" %% "scalatest" % "3.0.1" % Test,
      "com.github.marklister" %%% "product-collections" % "1.4.5"
    ),
    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases"),
      Resolver.sonatypeRepo("snapshots")
    )
  ).
  jvmSettings(
    libraryDependencies += "net.sf.opencsv" % "opencsv" % "2.3"
  ).
  jsSettings(
  )

lazy val pureCSVJVM = pureCSV.jvm
lazy val pureCSVJS  = pureCSV.js

lazy val refined = (crossProject.crossType(CrossType.Full) in file("modules/refined")).
  dependsOn(pureCSV).
  dependsOn(pureCSV % "test->test").
  settings(buildSettings).
  settings(publishSettings).
  settings(
    name := "purecsv-refined",
    libraryDependencies ++= Seq(
      "eu.timepit" %% "refined" % "0.8.4")
  ).
  jvmSettings().
  jsSettings()

lazy val refinedJVM = refined.jvm
lazy val refinedJS  = refined.js
