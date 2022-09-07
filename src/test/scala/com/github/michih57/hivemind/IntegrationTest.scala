package com.github.michih57.hivemind

import cats.effect._
import cats.effect.unsafe.implicits.global
import io.circe.generic.auto._
import io.circe.parser.decode
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class IntegrationTest extends AnyFunSuite with Matchers {

  test("missing request body") {
    val response =
      runRequest(Request(method = Method.POST, uri = uri"/amazon/best-rated"))
    response.status should be(BadRequest)
  }

  test("simple successful request") {
    val body =
      """
        |{
        |    "start": "01.01.2010",
        |    "end": "31.12.2020",
        |    "limit": 2,
        |    "min_number_reviews": 2
        |}""".stripMargin
    val response = runRequest(
      Request[IO](
        method = Method.POST,
        uri = uri"/amazon/best-rated"
      ).withEntity(body)
    )
    response.status should be(Ok)
    val responseBody =
      response.bodyText.compile.toList.unsafeRunSync().mkString("")
    val topProducts: List[TopProduct] =
      decode[List[TopProduct]](responseBody).getOrElse(List.empty)
    topProducts should be(
      List(
        TopProduct("B000JQ0JNS", 4.5),
        TopProduct("B000NI7RW8", 3.6666666666666665)
      )
    )
  }

  private def runRequest(request: Request[IO]): Response[IO] = {
    val service = ReviewService.mkService(
      fs2.io.file.Path("src/test/resources/sorted.json")
    )
    val response: IO[Response[IO]] =
      service.run(request)
    response.unsafeRunSync()
  }

}
