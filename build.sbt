name := "ytb-history-downloader"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12.6"

mainClass in(Compile, run) := Some("com.github.vaclavsvejcar.yhd.Launcher")

libraryDependencies ++= Seq(
  "net.ruippeixotog" %% "scala-scraper" % "2.1.0",
  "com.typesafe.play" %% "play-json" % "2.6.7",
  "org.wvlet.airframe" %% "airframe-log" % "0.56",
  "com.github.scopt" %% "scopt" % "3.7.0",
  "com.github.mpilquist" %% "simulacrum" % "0.13.0",
  "com.nrinaudo" %% "kantan.csv-generic" % "0.4.0",
  "com.beachape" %% "enumeratum" % "1.5.13",
  "org.typelevel" %% "cats-core" % "1.2.0",

  // TEST dependencies
  "com.lihaoyi" %% "utest" % "0.6.3" % "test"
)

testFrameworks += new TestFramework("utest.runner.Framework")

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.7")

enablePlugins(SbtTwirl)

scalacOptions ++= Seq(
  "-feature",
  "-language:existentials",
  "-language:experimental.macros",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-Ypartial-unification",
  "-Ywarn-dead-code",
  "-Ywarn-unused:implicits",
  "-Ywarn-unused:imports",
  "-Ywarn-unused:locals",
  "-Ywarn-unused:patvars",
  "-Ywarn-unused:privates",
)
