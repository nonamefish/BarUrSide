package com.mingyuwu.barurside.util

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import com.mingyuwu.barurside.BarUrSideApplication
import android.graphics.BitmapFactory
import android.util.Log
import java.io.*
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


object Util {

    /**
     * Determine and monitor the connectivity status
     *
     * https://developer.android.com/training/monitoring-device-state/connectivity-monitoring
     */

    fun getString(resourceId: Int): String {
        return BarUrSideApplication.instance.getString(resourceId)
    }

    fun getColor(resourceId: Int): Int {
        return BarUrSideApplication.instance.getColor(resourceId)
    }

    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap? {
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
        val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
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
        val dateFormat = SimpleDateFormat("yyyy/MM/dd a hh:mm", Locale.TAIWAN)
        val parsedDate = dateFormat.parse(time)
        return Timestamp(parsedDate.time)
    }

    fun calculateDateByPeriod(timestamp: Timestamp, unit: String, amount: Int) : Timestamp? {
        val cdate = Calendar.getInstance()
        cdate.time = timestamp

        when(unit){
            "DAY" -> cdate.add(Calendar.DATE, amount)
            "MONTH" -> cdate.add(Calendar.MONTH, amount)
            "YEAR" -> cdate.add(Calendar.YEAR, amount)
            else -> return null
        }

        return Timestamp(cdate.timeInMillis)

    }

}
