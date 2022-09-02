package com.github.michih57.hivemind

import io.circe.generic.auto._
import io.circe.parser._
import org.slf4j.LoggerFactory

case class Review(
    reviewerID: String,
    asin: String,
    reviewerName: Option[String],
    helpful: List[Int], // TODO: maybe find a way to fix length of list to 2?
    reviewText: String,
    overall: Double,
    summary: String,
    unixReviewTime: Long
)

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
