import sbt.Keys._

lazy val buildSettings = Seq(
  organization := "io.kontainers",
  scalaVersion := "2.12.8",
  crossScalaVersions := Seq("2.11.12", scalaVersion.value)
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
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    scalacOptions ++= Seq("-feature", "-deprecation"),
    libraryDependencies ++= Seq(
      "com.chuusai" %% "shapeless" % "2.3.3",
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
  ).
  jsSettings(
  )

lazy val pureCSVJVM = pureCSV.jvm
lazy val pureCSVJS  = pureCSV.js
