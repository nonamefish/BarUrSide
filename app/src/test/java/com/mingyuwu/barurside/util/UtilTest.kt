package com.mingyuwu.barurside.util

import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.util.Util.getDiffMinute
import junit.framework.TestCase
import org.junit.Test
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class UtilTest : TestCase() {

    @Test
    fun testGetDiffMinute() {
        // Given
        val cdate = Calendar.getInstance()
        cdate.time = Timestamp(System.currentTimeMillis())
        cdate.add(Calendar.MINUTE, -5)

        // When
        val diff = getDiffMinute(Timestamp(cdate.timeInMillis))
        println("date: ${Timestamp(cdate.timeInMillis)}")

        // Then
        assertEquals(diff, 5)
    }

    @Test
    fun testGetDiffHour() {
    }

    @Test
    fun testGetDiffDay() {
    }

    @Test
    fun testConvertStringToTimestamp() {
    }

    @Test
    fun testCalculateDateByPeriod() {
    }

    @Test
    fun testGetRectangleRange() {
    }
}