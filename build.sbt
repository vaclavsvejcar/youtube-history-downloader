name := "youtube-history-downloader"
version := "0.1.0-SNAPSHOT"
scalaVersion := "2.13.1"

mainClass in(Compile, run) := Some("com.github.vaclavsvejcar.yhd.Launcher")
assemblyJarName in assembly := "yhd.jar"

libraryDependencies ++= Seq(
  "net.ruippeixotog" %% "scala-scraper" % "2.2.0",
  "com.typesafe.play" %% "play-json" % "2.8.0",
  "org.wvlet.airframe" %% "airframe-log" % "19.11.2",
  "com.github.scopt" %% "scopt" % "3.7.1",
  "com.github.mpilquist" %% "simulacrum" % "0.19.0",
  "com.nrinaudo" %% "kantan.csv-generic" % "0.6.0",
  "com.beachape" %% "enumeratum" % "1.5.13",
  "org.typelevel" %% "cats-core" % "2.0.0",

  // TEST dependencies
  "org.scalactic" %% "scalactic" % "3.0.8",
  "org.scalatest" %% "scalatest" % "3.0.8" % "test"
)

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full)

enablePlugins(SbtTwirl)

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-language:existentials",
  "-language:experimental.macros",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-Ywarn-dead-code",
  "-Ywarn-unused:implicits",
  "-Ywarn-unused:imports",
  "-Ywarn-unused:locals",
  "-Ywarn-unused:patvars",
  "-Ywarn-unused:privates",
)
