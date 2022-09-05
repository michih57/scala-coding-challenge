package com.github.michih57.hivemind
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import java.time.{LocalDate, Month}

class UtilsTest extends AnyFunSuite with Matchers {

  test("simple date parsing") {
    val parsed = Utils.parseDate("01.01.2010")
    parsed.isSuccess should be(true)
    assertDate(parsed.get, day = 1, month = Month.JANUARY, year = 2010)
  }

  test("edge case date parsing") {
    val parsed = Utils.parseDate("29.02.2004")
    parsed.isSuccess should be(true)
    assertDate(parsed.get, day = 29, month = Month.FEBRUARY, year = 2004)
  }

  test("bad month in date string") {
    val parsed = Utils.parseDate("12.13.2020")
    parsed.isFailure should be(true)
  }

  test("single-digit day of year") {
    val parsed = Utils.parseDate("1.01.2020")
    parsed.isFailure should be(true)
  }

  test("single-digit month") {
    val parsed = Utils.parseDate("01.1.2020")
    parsed.isFailure should be(true)
  }

  private def assertDate(date: LocalDate, day: Int, month: Month, year: Int) = {
    date.getDayOfMonth should be(day)
    date.getMonth should be(month)
    date.getYear should be(year)
  }

}
