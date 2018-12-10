import sbt.Keys._

lazy val buildSettings = Seq(
  organization := "com.github.melrief",
  scalaVersion := "2.12.8",
  crossScalaVersions := Seq("2.10.5", "2.11.8", scalaVersion.value)
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
  aggregate(pureCSVJVM, pureCSVJS).
  settings(buildSettings).
  settings(publishSettings).
  settings(noPublishSettings)

lazy val pureCSV = crossProject.crossType(CrossType.Full).in(new File(".")).
  settings(buildSettings).
  settings(publishSettings).
  settings(
    name := "purecsv",
    version := "0.1.2-SNAPSHOT",
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    scalacOptions ++= Seq("-feature", "-deprecation"),
    libraryDependencies ++= Seq(
      "com.chuusai" %% "shapeless" % "2.3.2",
      compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
      "org.scalatest" %% "scalatest" % "3.0.5" % Test,
      "com.github.marklister" %%% "product-collections" % "1.4.5"
    ),
    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases"),
      Resolver.sonatypeRepo("snapshots")
    )
  ).
  jvmSettings(
    libraryDependencies += "com.opencsv" % "opencsv" % "4.2"
  ).
  jsSettings(
  )

lazy val pureCSVJVM = pureCSV.jvm
lazy val pureCSVJS  = pureCSV.js
