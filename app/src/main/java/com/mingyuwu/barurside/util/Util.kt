package com.mingyuwu.barurside.util

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng
import com.mingyuwu.barurside.BarUrSideApplication
import com.mingyuwu.barurside.Constants.TEMP_DIRECTORY
import com.mingyuwu.barurside.R
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.cos

object Util {

    /**
     * Determine and monitor the connectivity status
     *
     * https://developer.android.com/training/monitoring-device-state/connectivity-monitoring
     */

    fun getString(resourceId: Int, vararg formatArgs: Any = emptyArray()): String {
        return try {
            BarUrSideApplication.instance.getString(resourceId, *formatArgs)
        } catch (e: IllegalFormatConversionException) {
            Logger.d("$resourceId IllegalAccessException: $e")
            ""
        }
    }

    fun getColor(resourceId: Int): Int {
        return BarUrSideApplication.instance.getColor(resourceId)
    }

    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    fun randomName(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    fun saveBitmap(bitmap: Bitmap, name: String): String {
        val fOut: FileOutputStream
        val cw = ContextWrapper(BarUrSideApplication.appContext)
        val directory = cw.getDir(TEMP_DIRECTORY, Context.MODE_PRIVATE)
        val path = File(directory, name)

        try {
            fOut = FileOutputStream(path)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
            try {
                fOut.flush()
                fOut.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        return path.path
    }

    fun getDiffHour(date: Timestamp): Long {
        val current = Timestamp(System.currentTimeMillis())
        val diff = current.time - date.time
        return TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS)
    }

    fun getDiffDay(date: Timestamp): Long {
        val current = Timestamp(System.currentTimeMillis())
        val diff = current.time - date.time
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
    }

    fun getDiffMinute(date: Timestamp): Long {
        val current = Timestamp(System.currentTimeMillis())
        val diff = current.time - date.time
        return TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS)
    }

    fun convertStringToTimestamp(time: String): Timestamp {
        val dateFormat = SimpleDateFormat(getString(R.string.datetime_format), Locale.TAIWAN)
        val parsedDate = dateFormat.parse(time) ?: Timestamp(0)
        return Timestamp(parsedDate.time)
    }

    fun calculateDateByPeriod(timestamp: Timestamp, unit: DateUnit, amount: Int): Timestamp {
        val cdate = Calendar.getInstance()
        cdate.time = timestamp

        when (unit) {
            DateUnit.DAY -> cdate.add(Calendar.DATE, amount)
            DateUnit.MONTH -> cdate.add(Calendar.MONTH, amount)
            DateUnit.YEAR -> cdate.add(Calendar.YEAR, amount)
        }

        return Timestamp(cdate.timeInMillis)
    }

    fun getRectangleRange(location: LatLng, distance: Double): List<Double> {

        val minLat = location.latitude - (distance / 111.11)
        val maxLat = location.latitude + (distance / 111.11)
        val minLng = location.longitude - (distance / 111.11 / cos(location.latitude))
        val maxLng = location.longitude + (distance / 111.11 / cos(location.latitude))

        return listOf(minLat, maxLat, minLng, maxLng)
    }
}
