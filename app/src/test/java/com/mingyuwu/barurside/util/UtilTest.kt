package com.mingyuwu.barurside.util

import com.mingyuwu.barurside.util.Util.getDiffDay
import com.mingyuwu.barurside.util.Util.getDiffHour
import com.mingyuwu.barurside.util.Util.getDiffMinute
import junit.framework.TestCase
import org.junit.Test
import java.sql.Timestamp
import java.util.*

class UtilTest : TestCase() {

    @Test
    fun testGetDiffMinute() {
        // Given
        val cdate = Calendar.getInstance()
        cdate.time = Timestamp(System.currentTimeMillis())
        cdate.add(Calendar.MINUTE, -5)

        // When
        val diff = getDiffMinute(Timestamp(cdate.timeInMillis))

        // Then
        assertEquals(diff, 5)
    }

    @Test
    fun testGetDiffHour() {
        // Given
        val cdate = Calendar.getInstance()
        cdate.time = Timestamp(System.currentTimeMillis())
        cdate.add(Calendar.HOUR, -5)

        // When
        val diff = getDiffHour(Timestamp(cdate.timeInMillis))

        // Then
        assertEquals(diff, 5)
    }

    @Test
    fun testGetDiffDay() {
        // Given
        val cdate = Calendar.getInstance()
        cdate.time = Timestamp(System.currentTimeMillis())
        cdate.add(Calendar.DATE, -5)

        // When
        val diff = getDiffDay(Timestamp(cdate.timeInMillis))

        // Then
        assertEquals(diff, 5)
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