name := "ytb-history-downloader"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12.10"

mainClass in(Compile, run) := Some("com.github.vaclavsvejcar.yhd.Launcher")

assemblyJarName in assembly := "yhd.jar"

libraryDependencies ++= Seq(
  "net.ruippeixotog" %% "scala-scraper" % "2.2.0",
  "com.typesafe.play" %% "play-json" % "2.7.4",
  "org.wvlet.airframe" %% "airframe-log" % "19.10.1",
  "com.github.scopt" %% "scopt" % "3.7.1",
  "com.github.mpilquist" %% "simulacrum" % "0.19.0",
  "com.nrinaudo" %% "kantan.csv-generic" % "0.6.0",
  "com.beachape" %% "enumeratum" % "1.5.13",
  "org.typelevel" %% "cats-core" % "2.0.0",

  // TEST dependencies
  "com.lihaoyi" %% "utest" % "0.7.1" % "test"
)

testFrameworks += new TestFramework("utest.runner.Framework")

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.10")

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
