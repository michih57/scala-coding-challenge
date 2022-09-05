package com.github.michih57.hivemind
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class Explorations extends AnyFunSuite with Matchers {

  ignore("blub") {
    val reviews = fs2.io.file
      .Files[IO]
      .readUtf8Lines(
        fs2.io.file.Path("/tmp/amazon-reviews-sorted.json")
      )

    val result = reviews
      .filter(!_.isBlank)
      .map(ReviewParser.parseReview)
      .collect { case Some(review) =>
        review
      }
      .groupAdjacentBy(_.asin)
      .map(_._1)
      .compile
      .toList

    val allProducts = result.unsafeRunSync()
    println(allProducts.size)
  }

}
