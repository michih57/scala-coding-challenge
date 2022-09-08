# How to run

`sbt run "--reviewsFile /path/to/reviews.json"`

The reviews file gets processed (sorted) and written to a temporary file (shown in the logs). After preprocessing, the
server is started at port `8080`.

# Library Choices

- Server library: http4s for functional-style server. No prior experience, picked out of curiosity.
- File processing: fs2 for handling the review file as a stream. No prior experience, picked out of curiosity.
- Sorting of reviews file: [big-sorter](https://github.com/davidmoten/big-sorter). Was the first library that I found
  when searching for a solution to sort big files in Scala/Java (could also delegate to the `sort` via shell, but a Java
  library seemed cleaner)
- Testing: ScalaTest. It's just the one I'm most familiar with. (I know there are 'more functional' libraries out
  there.)
- Command line arguments parsing: [decline](https://github.com/bkirwi/decline). Picked because it belongs to the
  typelevel family of projects. Could also have done without this library, but it seemd cleaner.

# Approach

When the server is started, it preprocesses the reviews file to sort its lines by product. This way, when handling a
request, the file can essentially be processed in chunks, because all reviews for one product are adjacent lines. For
each product the average rating can thus be computed without having to have processed the complete file. This approach
should be fairly easy on memory usage - at least in the large sample file no product had more than 18K reviews. If
memory consumption needs to be brought down further, this could be done by splitting the chunk of reviews for one
product further into fixed-size chunks and compute partial averages, which can then be combined into the total average.
The big downside of this approach is that for each request, the whole file needs to be scanned. For the sample file this
results in response times in the range of 15-20 seconds. This response time may not be acceptable. However, I thought
since the main purpose of this assignment was to demonstrate my way of programming and to have some code to talk about,
it is still good enough (and I had to make a decision to limit the time spent).

An obvious alternative would be to use a database to leverage it's query capabilities. In this case a relational
database like PostgreSQL would fit nicely, as the requests translate quite directly into SQL.

# Other Remarks

## Async testing

Some tests use blocking evaluation calls on `IO` objects - it would make sense to leverage the capabilities of ScalaTest
for asynchronous testing

## potential overflow in average rating computation

For very large review files, or rather products with a large amount of reviews, there is the possibility of overflows
during the computation of their average rating. There are multiple ways to work around this issue, e.g. one could use
number types like `BigDecimal` that take care of this problem. I suspect that this is the reason that the example
response in the assignment has a very large precision (exceeding the precision of the standard JVM `double`) for the
average rating of the second product: `3.666666666666666666666666666666667`

## Error handling

Error handling is left quite basic - and may leak internal information. With more time I would refine that (e.g. make
sure to adhere to the standard HTTP response codes, so far only `BadRequest` and `InternalError` are used).