import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.11"
  lazy val http4sServer = "org.http4s" %% "http4s-server" % Versions.http4s
  lazy val http4sDsl = "org.http4s" %% "http4s-dsl" % Versions.http4s
  lazy val http4sCirce = "org.http4s" %% "http4s-circe" % Versions.http4s
  lazy val http4sEmberServer =
    "org.http4s" %% "http4s-ember-server" % Versions.http4s
  lazy val http4s = Seq(http4sServer, http4sEmberServer, http4sDsl, http4sCirce)

  lazy val catsEffect = "org.typelevel" %% "cats-effect" % "3.3.14"

  lazy val circeCore = "io.circe" %% "circe-core" % Versions.circe
  lazy val circeGeneric = "io.circe" %% "circe-generic" % Versions.circe
  lazy val circeParser = "io.circe" %% "circe-parser" % Versions.circe

  lazy val circe = Seq(circeCore, circeGeneric, circeParser)

  lazy val decline = "com.monovore" %% "decline" % "2.3.0"

  lazy val bigSorter = "com.github.davidmoten" % "big-sorter" % "0.1.21"

  lazy val slf4j = "org.slf4j" % "slf4j-api" % "2.0.0"
  lazy val logbackClassic = "ch.qos.logback" % "logback-classic" % "1.3.0"

  object Versions {
    val http4s = "0.23.15"
    val circe = "0.14.2"
  }
}
