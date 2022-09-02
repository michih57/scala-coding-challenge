package com.github.michih57.hivemind
import com.github.michih57.hivemind.ReviewSearch.TopProduct
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class ReviewSearchTest extends AnyFunSuite with Matchers {

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

  test("simple review search") {
    val topProducts =
      ReviewSearch.findTopProducts(reviews, limit = 2, minReviews = 3)
    val expectedTopProducts =
      List(TopProduct(asin2, 5.0), TopProduct(asin3, 4.0))
    topProducts should be(expectedTopProducts)
  }

  test("no product with enough reviews") {
    val topProducts =
      ReviewSearch.findTopProducts(reviews, limit = 2, minReviews = 5)
    topProducts should be(empty)
  }

  test("all products in result") {
    val topProducts =
      ReviewSearch.findTopProducts(reviews, limit = 10, minReviews = 0)
    val expectedTopProducts = List(
      TopProduct(asin2, 5.0),
      TopProduct(asin3, 4.0),
      TopProduct(asin4, 3.0),
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
