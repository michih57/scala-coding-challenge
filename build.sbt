import Dependencies._

ThisBuild / scalaVersion := "2.13.8"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.github.michih57"
ThisBuild / organizationName := "michih57"

lazy val root = (project in file("."))
  .settings(
    name := "hivemind",
    libraryDependencies ++= Seq(
      http4sServer,
      catsEffect,
      scalaTest % Test
    ) ++ circe
  )
