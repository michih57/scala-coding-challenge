package com.github.michih57.hivemind
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.Try

object Utils {

  private val formatter = {
    val dateFmt = "dd.MM.yyyy"
    DateTimeFormatter.ofPattern(dateFmt)
  }

  def parseDate(dateStr: String): Try[LocalDate] = Try {
    LocalDate
      .parse(dateStr, formatter)
  }

}
