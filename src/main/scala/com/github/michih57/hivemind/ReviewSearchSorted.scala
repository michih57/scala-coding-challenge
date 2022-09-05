package com.github.michih57.hivemind
import cats.effect.IO

import scala.collection.immutable.SortedSet

object ReviewSearchSorted {

  case class Filter(
      startDate: Long,
      endDate: Long,
      minReviews: Int,
      nrTopProducts: Int
  )

  /** Searches for top rated products.
    * Assumes that the reviews in the stream are sorted by the product id asin
    * @param params filter params
    * @param reviews asin-sorted reviews
    * @return the top rated products (as specified by params)
    */
  def search(
      params: Filter,
      reviews: fs2.Stream[IO, String]
  ): IO[Seq[TopProduct]] = {
    val reviewsInTimePeriod = reviews
      .filter(!_.isBlank)
      .map(ReviewParser.parseReview)
      .collect {
        case Some(review)
            if isInRange(review, params.startDate, params.endDate) =>
          review
      }
    accumulateTopProducts(
      reviewsInTimePeriod,
      params.minReviews,
      params.nrTopProducts
    )
  }

  private def accumulateTopProducts(
      reviews: fs2.Stream[IO, Review],
      minNrReviews: Int,
      nrTopProducts: Int
  ): IO[List[TopProduct]] = {
    val topProductsAcc = reviews
      .groupAdjacentBy(_.asin)
      .collect {
        case (asin, productReviews) if productReviews.size >= minNrReviews =>
          val nrReviews = productReviews.size
          val averageRating =
            productReviews.toList.map(_.overall).sum / nrReviews
          TopProduct(asin, averageRating)
      }
      .fold(new TopProductAccumulator(nrTopProducts)) { (acc, topProduct) =>
        acc.add(topProduct)
      }
    topProductsAcc.compile.toList.map(
      _.headOption
        .map(_.products)
        .getOrElse(SortedSet.empty[TopProduct])
        .toList
        .reverse
    )
  }

  private def isInRange(
      review: Review,
      startTime: Long,
      endTime: Long
  ): Boolean =
    review.unixReviewTime >= startTime && review.unixReviewTime <= endTime

}
