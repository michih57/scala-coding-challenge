package com.github.michih57.hivemind
import cats.effect.IO
import fs2.io.file.{Files, Path}

class ReviewCollector(reviewFilePath: Path) {

  def collect(startTime: Long, endTime: Long): IO[Seq[Review]] = {
    val lineStream = Files[IO].readUtf8Lines(
      reviewFilePath
    )
    val reviews = lineStream
      .filter(!_.isBlank)
      .map(ReviewParser.parseReview)
      .collect {
        case Some(review) if isInRange(review, startTime, endTime) => review
      }
      .compile
      .toVector
    reviews
  }

  private def isInRange(
      review: Review,
      startTime: Long,
      endTime: Long
  ): Boolean =
    review.unixReviewTime >= startTime && review.unixReviewTime <= endTime

}
