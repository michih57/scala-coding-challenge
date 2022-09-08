package com.github.michih57.hivemind

import cats.effect._
import com.github.davidmoten.bigsorter.Sorter
import com.monovore.decline._
import org.slf4j.LoggerFactory

import java.io.File
import java.nio.file.Files
import scala.util.{Failure, Success, Try}

object Main extends IOApp {

  private val log = LoggerFactory.getLogger("Main")

  // TODO: this is just for convenience during development
  //   should be removed in a real production environment
  val defaultReviewFilePath =
    "src/test/resources/video_game_reviews_example.json"
//  val defaultReviewFilePath = "../amazon-reviews.json"

  def run(args: List[String]): IO[ExitCode] = {
    log.debug(s"program arguments: ${args.mkString(" ")}")
    val cmd = buildCmd()
    cmd.parse(args) match {
      case Left(help) =>
        System.err.println(help)
        sys.exit(1)
      case Right(reviewsFilePath) =>
        doPreprocessing(reviewsFilePath) match {
          case Success(sortedReviewsFile) =>
            log.info("preprocessing done, starting server...")
            ReviewService.run(sortedReviewsFile)
          case Failure(exc) =>
            log.error("failed to start server, exiting...", exc)
            sys.exit(2)
        }
    }

  }

  private def buildCmd() = {
    val reviewFileOpt = Opts.option[String](
      "reviewsFile",
      help = "Path to the file containing reviews.",
      short = "r"
    )

    val options = reviewFileOpt.withDefault(defaultReviewFilePath)

    Command(
      name = "best-rated",
      header = "Find top rated products from a reviews file."
    )(options)
  }

  private def doPreprocessing(reviewsFilePath: String): Try[File] = {
    val reviewsFile = new File(reviewsFilePath)
    if (reviewsFile.exists()) {
      Try {
        val sortedFilePath = Files.createTempFile("sorted_reviews_", ".json")
        val sortedFile = sortedFilePath.toFile

        log.info(
          s"sorting reviews file ${reviewsFile.getAbsolutePath} to: ${sortedFilePath.toAbsolutePath}..."
        )
        Sorter
          .linesUtf8()
          .input(reviewsFile)
          .output(sortedFilePath.toFile)
          .sort()
        sortedFile
      }
    } else {
      Failure(new Exception("review file does not exist"))
    }
  }

}
