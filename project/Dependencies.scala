import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.11"
  lazy val http4sServer = "org.http4s" %% "http4s-server" % "0.23.15"

  lazy val catsEffect = "org.typelevel" %% "cats-effect" % "3.3.14"

  val circeVersion = "0.14.2"
  lazy val circeCore = "io.circe" %% "circe-core" % circeVersion
  lazy val circeGeneric = "io.circe" %% "circe-generic" % circeVersion
  lazy val circeParser = "io.circe" %% "circe-parser" % circeVersion

  lazy val circe = Seq(circeCore, circeGeneric, circeParser)
}
