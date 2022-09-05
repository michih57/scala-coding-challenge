package com.github.michih57.hivemind
import io.circe.generic.auto._
import io.circe.parser.decode
import org.slf4j.LoggerFactory

object ReviewParser {

  private val log = LoggerFactory.getLogger("ReviewParser")

  def parseReview(reviewStr: String): Option[Review] = {
    val decoded = decode[Review](reviewStr)
    decoded match {
      case Left(error) =>
        log.warn("failed to parse review json", error)
        None
      case Right(review) => Some(review)
    }
  }

}
