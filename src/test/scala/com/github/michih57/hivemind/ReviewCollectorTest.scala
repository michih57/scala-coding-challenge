package com.github.michih57.hivemind
import cats.effect.unsafe.implicits.global
import fs2.io.file.Path
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import java.time.{LocalDateTime, ZoneOffset}

class ReviewCollectorTest extends AnyFunSuite with Matchers {

  val testReviewsFile = Path(
    "src/test/resources/video_game_reviews_example.json"
  )

  // FIXME: use https://github.com/davidmoten/big-sorter for preprocessing
  //  (can then do everything in one pass over the reviews file)

  test("collect all reviews") {
    val now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
    val reviews = new ReviewCollector(testReviewsFile).collect(0, now)

    reviews.unsafeRunSync() should have size 15
  }

  test("collect reviews in range") {
    val startTime = 1400000000
    val endTime = 1500000000
    val reviews =
      new ReviewCollector(testReviewsFile).collect(startTime, endTime)

    reviews.unsafeRunSync() should have size 6
  }

}
