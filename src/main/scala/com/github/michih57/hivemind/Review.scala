package com.github.michih57.hivemind

case class Review(
    reviewerID: String,
    asin: String,
    reviewerName: Option[String],
    helpful: List[Int], // TODO: maybe find a way to fix length of list to 2?
    reviewText: String,
    overall: Double,
    summary: String,
    unixReviewTime: Long
)
