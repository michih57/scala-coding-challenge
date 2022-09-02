package com.github.michih57.hivemind
import cats.effect._
import com.comcast.ip4s._
import com.github.michih57.hivemind.ReviewSearch.TopProduct
import com.monovore.decline.Opts
import org.http4s.{HttpRoutes, circe}
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.ember.server._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe.jsonEncoder
import org.slf4j.LoggerFactory
import com.monovore.decline._

import java.io.File
import java.time.{LocalDate, LocalDateTime, LocalTime, ZoneOffset}
import java.time.format.DateTimeFormatter

object ReviewService extends IOApp {

  private val log = LoggerFactory.getLogger("ReviewService")

  case class BestRatedRequest(
      start: String,
      end: String,
      limit: Int,
      min_number_reviews: Int
  )

  def run(args: List[String]): IO[ExitCode] = {
    println(args.zipWithIndex.mkString("\n"))

    val reviewFileOpt = Opts.option[String](
      "reviewsFile",
      help = "Path to the file containing reviews.",
      short = "r"
    )

    val options = reviewFileOpt.withDefault(
      "src/test/resources/video_game_reviews_example.json"
    )

    val cmd = Command(
      name = "best-rated",
      header = "Find top rated products from a reviews file."
    )(options)
    cmd.parse(args) match {
      case Left(help) =>
        System.err.println(help)
        sys.exit(1)
      case Right(reviewFilePath) =>
        runServer(reviewFilePath)
    }

  }

  private def runServer(reviewsFile: String) = {
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(mkService(fs2.io.file.Path(reviewsFile)))
      .withErrorHandler { error =>
        log.error("error in service", error)
        InternalServerError(error.getMessage)
      }
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)

  }

  private def mkService(reviewsFilepath: fs2.io.file.Path) = {
    implicit val requestDecoder = circe.jsonOf[IO, BestRatedRequest]
    implicit val responseDecoder = circe.jsonOf[IO, TopProduct]

    val reviewCollector = new ReviewCollector(reviewsFilepath)

    val reviewService = HttpRoutes
      .of[IO] {
        case req @ POST -> Root / "amazon" / "best-rated" =>
          for {
            params <- req.as[BestRatedRequest]
            products <- findTopProducts(reviewCollector, params)
            response <- Ok(products.asJson)
          } yield (response)
        case GET -> Root / "hello" / name =>
          Ok(s"Hello, $name.")
      }
      .orNotFound

    reviewService
  }

  private def findTopProducts(
      collector: ReviewCollector,
      request: BestRatedRequest
  ) = {
    // TODO: refactor date parsing to a better place!
    // "31.12.2020"
    val dateFmt = "dd.MM.yyyy"
    val formatter = DateTimeFormatter.ofPattern(dateFmt)
    val startTime: Long =
      LocalDate
        .parse(request.start, formatter)
        .toEpochSecond(
          LocalTime.MIN,
          ZoneOffset.UTC
        ) // start of day for start date
    val endTime: Long =
      LocalDate
        .parse(request.end, formatter)
        .toEpochSecond(
          LocalTime.MAX,
          ZoneOffset.UTC
        ) // end of day for end date
    val relevantReviews = collector.collect(startTime, endTime)
    relevantReviews.map { reviews =>
      ReviewSearch.findTopProducts(
        reviews,
        limit = request.limit,
        minReviews = request.min_number_reviews
      )
    }
  }

}
