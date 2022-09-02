package com.github.michih57.hivemind

object ReviewSearch {

  // TODO: use refined to limit range of averageRating?
  case class TopProduct(asin: String, averageRating: Double)

  def findTopProducts(
      reviews: Seq[Review],
      limit: Int,
      minReviews: Int
  ): Seq[TopProduct] = {
    reviews
      .groupBy(_.asin)
      .toSeq
      .flatMap { case (asin, rs) =>
        val nrReviews = rs.size
        Option.when(nrReviews >= minReviews) {
          val averageRating = rs.map(_.overall).sum / nrReviews
          TopProduct(asin, averageRating)
        }
      }
      .sortBy(-_.averageRating)
      .take(limit)
  }

}
