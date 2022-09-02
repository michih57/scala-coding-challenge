import Dependencies._

ThisBuild / scalaVersion := "2.13.8"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.github.michih57"
ThisBuild / organizationName := "michih57"

lazy val root = (project in file("."))
  .settings(
    name := "hivemind",
    libraryDependencies ++= Seq(
      catsEffect,
      decline,
      bigSorter,
      slf4j,
      logbackClassic,
      scalaTest % Test
    ) ++ http4s ++ circe
  )
