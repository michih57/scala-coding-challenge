package com.github.michih57.hivemind
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class ReviewParserTest extends AnyFunSuite with Matchers {

  val isHelpfulCount = 16
  val isNotHelpfulCount = 40

  val reviewerName = "Gaylord Bashirian"

  val reviewSample =
    Review(
      reviewerID = "B07844AAA04E4",
      asin = "B000Q75VCO",
      reviewerName = Some(reviewerName),
      helpful = List(isHelpfulCount, isNotHelpfulCount),
      reviewText =
        "Words are in my not-so-humble opinion, the most inexhaustible form of magic we have, capable both of inflicting injury and remedying it.",
      overall = 2.0,
      summary = "Ut deserunt adipisci aut.",
      unixReviewTime = 1475261866
    )

  val reviewSampleStr =
    s"""{"asin":"${reviewSample.asin}","helpful":[$isHelpfulCount,$isNotHelpfulCount],"overall":2.0,"reviewText":"${reviewSample.reviewText}","reviewerID":"${reviewSample.reviewerID}","reviewerName":"${reviewerName}","summary":"${reviewSample.summary}","unixReviewTime":${reviewSample.unixReviewTime}}"""

  test("parse valid review") {
    val parsedReview = ReviewParser.parseReview(reviewSampleStr)
    parsedReview should be(Some(reviewSample))
  }

  test("parse invalid review") {
    val invalidSampleStr =
      reviewSampleStr.replace(s""""asin":"${reviewSample.asin}",""", "")
    ReviewParser.parseReview(invalidSampleStr) should be(None)
  }

}
