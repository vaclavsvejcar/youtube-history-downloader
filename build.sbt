name := "ytb-history-scraper"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12.6"

mainClass in(Compile, run) := Some("com.github.vaclavsvejcar.yhs.Launcher")

libraryDependencies ++= Seq(
  "net.ruippeixotog" %% "scala-scraper" % "2.1.0",
  "com.typesafe.play" %% "play-json" % "2.6.7",
  "org.wvlet.airframe" %% "airframe-log" % "0.56",
  "com.github.scopt" %% "scopt" % "3.7.0",

  // TEST dependencies
  "com.lihaoyi" %% "utest" % "0.6.3" % "test"
)

testFrameworks += new TestFramework("utest.runner.Framework")