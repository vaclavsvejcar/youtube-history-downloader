name := "youtube-history-downloader"
version := "0.1.0-SNAPSHOT"
scalaVersion := "2.13.4"

mainClass in(Compile, run) := Some("com.github.vaclavsvejcar.yhd.Launcher")
assemblyJarName in assembly := "yhd.jar"

libraryDependencies ++= Seq(
  "net.ruippeixotog" %% "scala-scraper" % "2.2.0",
  "com.typesafe.play" %% "play-json" % "2.9.2",
  "org.wvlet.airframe" %% "airframe-log" % "21.1.0",
  "com.github.scopt" %% "scopt" % "4.0.0",
  "com.github.mpilquist" %% "simulacrum" % "0.19.0",
  "com.nrinaudo" %% "kantan.csv-generic" % "0.6.1",
  "com.beachape" %% "enumeratum" % "1.6.1",
  "org.typelevel" %% "cats-core" % "2.3.1",

  // TEST dependencies
  "org.scalactic" %% "scalactic" % "3.2.3",
  "org.scalatest" %% "scalatest" % "3.2.3" % "test"
)

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.2" cross CrossVersion.full)

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
