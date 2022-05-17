package com.jyodroid.tvseries.com.jyodroid.tvseries.utils

import com.jyodroid.tvseries.utils.convertToCompleteStringDate
import com.jyodroid.tvseries.utils.convertToDate
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*

/**
 * Default locale, region for robolectric is en-rUS
 */
@RunWith(RobolectricTestRunner::class)
class DateTimeFormatterTest {

    @Test
    fun `When we want to convert a date in complete date string`() {
        val date = Date(1632507463167)

        val expectedDate = "Friday, Sep 24, 2021"

        val parsedDate = date.convertToCompleteStringDate()

        assertEquals(expectedDate, parsedDate)
    }

    @Test
    fun `When we want to convert a null date in complete date string we should get null string`() {
        val date = null

        val parsedDate = date?.convertToCompleteStringDate()

        assertEquals(null, parsedDate)
    }

    @Test
    fun `When we want to convert a sting date in Date with wrong format we should get null Date`() {
        val stringDate = "Friday, Sep 24, 2021"

        val parsedDate = stringDate.convertToDate()

        assertEquals(null, parsedDate)
    }

    @Test
    fun `When we want to convert a sting date in Date with correct format`() {
        val stringDate = "2021-09-24"

        val expectedDate = Date(1632507463167)
        val expectedCalendar = Calendar.getInstance()
        expectedCalendar.time = expectedDate

        val parsedDate = stringDate.convertToDate()

        val parsedCalendar = Calendar.getInstance()
        parsedCalendar.time = parsedDate

        assertEquals(expectedCalendar.get(Calendar.YEAR), parsedCalendar.get(Calendar.YEAR))
        assertEquals(expectedCalendar.get(Calendar.MONTH), parsedCalendar.get(Calendar.MONTH))
        assertEquals(
            expectedCalendar.get(Calendar.DAY_OF_YEAR),
            parsedCalendar.get(Calendar.DAY_OF_YEAR)
        )
    }
}