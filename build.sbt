name := "yhd"
organization in ThisBuild := "com.github.vaclavsvejcar.yhd"
version in ThisBuild := "0.1.0-SNAPSHOT"
scalaVersion in ThisBuild := "2.12.6"

lazy val root = (project in file("."))
  .settings(settings)
  .aggregate(core, launcher)

lazy val core = (project in file("core"))
  .settings(
    name := "yhd-core",
    settings,
    libraryDependencies ++= commonDependencies ++ Seq(
      dependencies.scalaScraper,
      dependencies.playJson,
      dependencies.airframeLog,
      dependencies.simulacrum,
      dependencies.kantanCsvGeneric,
      dependencies.enumeratum,
      dependencies.catsCore
    ),
    testFrameworks += new TestFramework("utest.runner.Framework")
  )
  .enablePlugins(SbtTwirl)

lazy val launcher = (project in file("launcher"))
  .settings(
    name := "yhd-launcher",
    settings,
    assemblySettings,
    libraryDependencies ++= commonDependencies ++ Seq(
      dependencies.scopt
    ),
    testFrameworks += new TestFramework("utest.runner.Framework"),
    mainClass in(Compile, run) := Some("com.github.vaclavsvejcar.yhd.launcher.Launcher")
  )
  .dependsOn(core)

lazy val settings = commonSettings

lazy val commonSettings = Seq(
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
  ),
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full),
  addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.7")
)

lazy val assemblySettings = Seq(
  assemblyJarName in assembly := name.value + ".jar",
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", _*) => MergeStrategy.discard
    case _ => MergeStrategy.first
  }
)

lazy val dependencies = new {

  object Version {
    val airframe = "0.59"
    val cats = "1.2.0"
    val enumeratum = "1.5.13"
    val kantanCsv = "0.4.0"
    val playJson = "2.6.10"
    val scalaScraper = "2.1.0"
    val simulacrum = "0.13.0"
    val scopt = "3.7.0"
    val utest = "0.6.4"
  }

  val airframeLog = "org.wvlet.airframe" %% "airframe-log" % Version.airframe
  val catsCore = "org.typelevel" %% "cats-core" % Version.cats
  val enumeratum = "com.beachape" %% "enumeratum" % Version.enumeratum
  val kantanCsvGeneric = "com.nrinaudo" %% "kantan.csv-generic" % Version.kantanCsv
  val playJson = "com.typesafe.play" %% "play-json" % Version.playJson
  val scalaScraper = "net.ruippeixotog" %% "scala-scraper" % Version.scalaScraper
  val scopt = "com.github.scopt" %% "scopt" % Version.scopt
  val simulacrum = "com.github.mpilquist" %% "simulacrum" % Version.simulacrum
  val utest = "com.lihaoyi" %% "utest" % Version.utest
}

lazy val commonDependencies = Seq(
  dependencies.utest % "test"
)

/*assemblyJarName in assembly := "yhd.jar"

libraryDependencies ++= Seq(
  "net.ruippeixotog" %% "scala-scraper" % "2.1.0",
  "com.typesafe.play" %% "play-json" % "2.6.10",
  "org.wvlet.airframe" %% "airframe-log" % "0.59",
  "com.github.scopt" %% "scopt" % "3.7.0",
  "com.github.mpilquist" %% "simulacrum" % "0.13.0",
  "com.nrinaudo" %% "kantan.csv-generic" % "0.4.0",
  "com.beachape" %% "enumeratum" % "1.5.13",
  "org.typelevel" %% "cats-core" % "1.2.0",

  // TEST dependencies
  "com.lihaoyi" %% "utest" % "0.6.4" % "test"
)

testFrameworks += new TestFramework("utest.runner.Framework")

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.7")

enablePlugins(SbtTwirl)*/

