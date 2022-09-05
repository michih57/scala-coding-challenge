package com.github.michih57.hivemind

// TODO: use refined to limit range of averageRating?
case class TopProduct(asin: String, averageRating: Double)
    extends Ordered[TopProduct] {
  override def compare(
      that: TopProduct
  ): Int = averageRating.compare(that.averageRating)
}
