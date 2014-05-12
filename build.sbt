name := "play-polls"

version := "1.0-SNAPSHOT"

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "com.h2database" % "h2" % "1.3.174",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "org.dupontmanual" %% "dm-forms" % "0.2-SNAPSHOT"
)     

play.Project.playScalaSettings
