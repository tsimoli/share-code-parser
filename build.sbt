name := """share-code-parser"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

resolvers ++= Seq(
  "Typesafe repository snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
  "Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype repo"                    at "https://oss.sonatype.org/content/groups/scala-tools/",
  "Sonatype releases"                at "https://oss.sonatype.org/content/repositories/releases",
  "Sonatype snapshots"               at "https://oss.sonatype.org/content/repositories/snapshots",
  "Sonatype staging"                 at "http://oss.sonatype.org/content/repositories/staging",
  "Java.net Maven2 Repository"       at "http://download.java.net/maven/2/",
  "Twitter Repository"               at "http://maven.twttr.com"
)

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.0.3",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  "com.github.tototoshi" %% "slick-joda-mapper" % "2.0.0",
  "com.typesafe.play" %% "play-slick" % "1.1.1",
  "com.github.tminglei" %% "slick-pg" % "0.10.0",
  "com.enragedginger" %% "akka-quartz-scheduler" % "1.4.0-akka-2.3.x",
  ws
)


// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

// Docker settings
maintainer in Docker := "tsimoli"

version in Docker := "0.19"

packageName in Docker := "share-code-parser"
