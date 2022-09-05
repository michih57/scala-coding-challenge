package com.github.michih57.hivemind
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.github.michih57.hivemind.ReviewSearchSorted._
import io.circe.generic.auto._
import io.circe.syntax._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class ReviewSearchSortedTest extends AnyFunSuite with Matchers {

  private val reviewerID = "j.doe"

  private val asin1 = "asin1"
  private val asin2 = "asin2"
  private val asin3 = "asin3"
  private val asin4 = "asin4"
  private val reviews = Seq(
    (asin1, 1),
    (asin1, 2),
    (asin1, 3),
    (asin2, 5),
    (asin2, 5),
    (asin2, 5),
    (asin2, 5),
    (asin3, 3),
    (asin3, 4),
    (asin3, 5),
    (asin4, 3),
    (asin4, 3)
  ).map { case (asin, rating) =>
    makeReview(asin, rating = rating)
  }

  test("simple search test") {
    val reviewStream: fs2.Stream[IO, String] =
      fs2.Stream.emits[IO, String](reviews.map(_.asJson.noSpaces))
    val expectedTopProducts =
      Seq(TopProduct(asin2, 5.0), TopProduct(asin3, 4.0))
    val result = ReviewSearchSorted
      .search(
        Filter(0L, 50L, minReviews = 3, nrTopProducts = 2),
        reviewStream
      )
      .unsafeRunSync()
    result should be(expectedTopProducts)
  }

  test("no product with enough reviews") {
    val reviewStream: fs2.Stream[IO, String] =
      fs2.Stream.emits[IO, String](reviews.map(_.asJson.noSpaces))
    val topProducts =
      ReviewSearchSorted
        .search(
          Filter(0L, 50L, nrTopProducts = 2, minReviews = 5),
          reviewStream
        )
        .unsafeRunSync()
    topProducts should be(empty)
  }

  test("all products in result") {
    val reviewStream: fs2.Stream[IO, String] =
      fs2.Stream.emits[IO, String](reviews.map(_.asJson.noSpaces))
    val topProducts =
      ReviewSearchSorted
        .search(
          Filter(0L, 50L, minReviews = 0, nrTopProducts = 10),
          reviewStream
        )
        .unsafeRunSync()
    val expectedTopProducts = List(
      TopProduct(asin2, 5.0),
      TopProduct(asin3, 4.0),
      TopProduct(asin4, 3.0),
      TopProduct(asin1, 2.0)
    )
    topProducts should be(expectedTopProducts)
  }

  test("filter by date") {
    val reviewsWithDifferentTimestamps = reviews.map { review =>
      if (review.asin == asin1) {
        review.copy(unixReviewTime = 100L)
      } else {
        review
      }
    }
    val reviewStream: fs2.Stream[IO, String] =
      fs2.Stream.emits[IO, String](
        reviewsWithDifferentTimestamps.map(_.asJson.noSpaces)
      )
    val topProducts =
      ReviewSearchSorted
        .search(
          Filter(99L, 101L, minReviews = 0, nrTopProducts = 10),
          reviewStream
        )
        .unsafeRunSync()
    val expectedTopProducts = List(
      TopProduct(asin1, 2.0)
    )
    topProducts should be(expectedTopProducts)
  }

  private def makeReview(asin: String, rating: Double): Review = {
    Review(
      reviewerID = reviewerID,
      asin = asin,
      reviewerName = None,
      helpful = List(1, 2),
      reviewText = "blah blah blah",
      overall = rating,
      summary = "blah...",
      unixReviewTime = 42L
    )
  }

}
