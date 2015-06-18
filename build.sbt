name := "purecsv"

version := "0.0.2"

organization := "com.github.melrief"

publishMavenStyle := true

publishArtifact in Test := false

scalaVersion := "2.11.6"

scalacOptions ++= Seq("-feature", "-deprecation")

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

publishTo <<= version { v: String =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % "2.1.0",
  "net.sf.opencsv" % "opencsv" % "2.3",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test"
)

pomIncludeRepository := { x => false }

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
