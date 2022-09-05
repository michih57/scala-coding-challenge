package com.github.michih57.hivemind
import cats.effect._
import com.comcast.ip4s._
import com.github.michih57.hivemind.ReviewSearchSorted.Filter
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe.jsonEncoder
import org.http4s.dsl.io._
import org.http4s.ember.server._
import org.http4s.implicits._
import org.http4s.{EntityDecoder, HttpApp, HttpRoutes, circe}
import org.slf4j.LoggerFactory

import java.io.File
import java.time.{LocalTime, ZoneOffset}
import scala.util.{Failure, Success, Try}

object ReviewService {

  private val log = LoggerFactory.getLogger("ReviewService")

  case class BestRatedRequest(
      start: String,
      end: String,
      limit: Int,
      min_number_reviews: Int
  )

  def run(sortedReviewsFile: File) = {
    buildServer {
      val reviewsFilePath = fs2.io.file.Path(sortedReviewsFile.getAbsolutePath)
      mkService(reviewsFilePath)
    }
  }

  private def buildServer: HttpApp[IO] => IO[ExitCode] = app => {
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(app)
      .withErrorHandler { error =>
        log.error("error in service", error)
        InternalServerError(error.getMessage)
      }
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }

  private def mkService(reviewsFilepath: fs2.io.file.Path) = {
    implicit val requestDecoder: EntityDecoder[IO, BestRatedRequest] =
      circe.jsonOf[IO, BestRatedRequest]
    implicit val responseDecoder = circe.jsonOf[IO, TopProduct]

    val reviewService = HttpRoutes
      .of[IO] { case req @ POST -> Root / "amazon" / "best-rated" =>
        for {
          params <- req.as[BestRatedRequest]
          // TODO: maybe add logging how long the computation takes?
          maybeProducts <- findTopProducts(reviewsFilepath, params)
          response <- maybeProducts match {
            case Success(products) => Ok(products.asJson)
            case Failure(exc) =>
              log.error("failed to find top products", exc)
              InternalServerError(exc.getMessage)
          }
        } yield response
      }
      .orNotFound

    reviewService
  }

  private def findTopProducts(
      reviewFilePath: fs2.io.file.Path,
      request: BestRatedRequest
  ): IO[Try[Seq[TopProduct]]] = {
    val filter = for {
      startDate <- Utils.parseDate(request.start)
      startTime = startDate.toEpochSecond(
        LocalTime.MIN,
        ZoneOffset.UTC
      ) // start of day for start date
      endDate <- Utils.parseDate(request.end)
      endTime = endDate.toEpochSecond(
        LocalTime.MAX,
        ZoneOffset.UTC
      ) // end of day for end date
    } yield Filter(
      startTime,
      endTime,
      request.min_number_reviews,
      request.limit
    )

    filter match {
      case Success(f) =>
        val reviewStream = fs2.io.file
          .Files[IO]
          .readUtf8Lines(
            reviewFilePath
          )
        ReviewSearchSorted.search(f, reviewStream).map(Success.apply)
      case Failure(exc) => IO.pure(Failure(exc))
    }
  }

}
