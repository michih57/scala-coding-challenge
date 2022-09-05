package com.github.michih57.hivemind
import scala.collection.immutable.SortedSet

class TopProductAccumulator(
    val limit: Int,
    val products: SortedSet[TopProduct] = SortedSet.empty[TopProduct]
) {

  def add(product: TopProduct): TopProductAccumulator = {
    val newProducts = products + product
    val actualNewProducts = if (newProducts.sizeIs > limit) {
      // assuming limit > 0, there always exists a minimum
      val min = newProducts.min
      newProducts - min
    } else {
      newProducts
    }
    new TopProductAccumulator(limit, actualNewProducts)
  }

}
