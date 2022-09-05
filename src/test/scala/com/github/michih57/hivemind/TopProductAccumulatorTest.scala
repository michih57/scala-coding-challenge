package com.github.michih57.hivemind
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.collection.immutable.SortedSet

class TopProductAccumulatorTest extends AnyFunSuite with Matchers {

  test("order is correct") {
    val topProduct1 = TopProduct("prod1", 4.0)
    val topProduct2 = TopProduct("prod2", 3.5)

    val acc = new TopProductAccumulator(
      2,
      SortedSet.empty[TopProduct] + topProduct1 + topProduct2
    )

    acc.products.toList should be(List(topProduct2, topProduct1))
  }

  test("add product to full acc") {
    val topProduct1 = TopProduct("prod1", 4.0)
    val topProduct2 = TopProduct("prod2", 3.5)
    val topProduct3 = TopProduct("prod3", 5.0)

    val acc = new TopProductAccumulator(
      2,
      SortedSet.empty[TopProduct] + topProduct1 + topProduct2
    )
    val updatedAcc = acc.add(topProduct3)
    updatedAcc.products should have size 2
    updatedAcc.products.max should be(topProduct3)
  }

}
