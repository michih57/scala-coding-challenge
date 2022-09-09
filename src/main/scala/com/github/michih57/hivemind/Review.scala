package com.github.michih57.hivemind

// TODO: a potential optimization would be to only parse the fields actually needed in the program
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
