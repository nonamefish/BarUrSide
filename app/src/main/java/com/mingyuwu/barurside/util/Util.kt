package com.mingyuwu.barurside.util

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.mingyuwu.barurside.BarUrSideApplication
import com.mingyuwu.barurside.Constants.TEMP_DIRECTORY
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.data.Result
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
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

    fun getDiffMinute(date: Timestamp): Long {
        val current = Timestamp(System.currentTimeMillis())
        val diff = current.time - date.time
        return TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS)
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

    suspend fun <T : Any> T.getResult(source: Task<*>): Result<T?> =
        suspendCoroutine { continuation ->
            source.addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    when (val result = task.result) {
                        is QuerySnapshot -> {
                            if (result.isEmpty) {
                                continuation.resume(Result.Success(null))
                            } else {
                                continuation.resume(
                                    Result.Success(result.toObjects(this::class.java)[0])
                                )
                            }
                        }
                        is DocumentSnapshot -> {
                            continuation.resume(Result.Success(result.toObject(this::class.java)))
                        }
                    }

                } else {
                    when (val exception = task.exception) {
                        null -> continuation.resume(
                            Result.Fail(getString(R.string.fail))
                        )
                        else -> {
                            Logger.w(
                                "[${this::class.simpleName}] Error getting documents. ${exception.message}"
                            )
                            continuation.resume(Result.Error(exception))
                        }
                    }
                }
            }
        }

    suspend fun <T : Any> T.getListResult(source: Task<QuerySnapshot>): Result<List<T>> =
        suspendCoroutine { continuation ->
            source.addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    if (task.result == null || task.result!!.isEmpty) {
                        continuation.resume(Result.Success(listOf()))
                    } else {
                        continuation.resume(Result.Success(task.result!!.toObjects(this::class.java)))
                    }

                } else {
                    when (val exception = task.exception) {

                        null -> continuation.resume(Result.Fail(getString(R.string.fail)))

                        else -> {
                            Logger.w(
                                "[${this::class.simpleName}] Error getting documents. ${exception.message}"
                            )
                            continuation.resume(Result.Error(exception))
                        }
                    }
                }
            }
        }

    fun <T : Any> T.getLiveDataResult(ref: Query): MutableLiveData<T> {
        val liveData = MutableLiveData<T>()

        ref.addSnapshotListener { snapshot, exception ->

            exception?.let {
                Logger.d("[${this::class.simpleName}] Error getting documents. ${it.message}")
            }

            for (document in snapshot!!) {
                val venue = document.toObject(this::class.java)
                liveData.value = venue
            }

            liveData.value = liveData.value
        }

        Logger.d("[${this::class.simpleName}] return $liveData")

        return liveData
    }

    fun <T : Any> T.getLiveDataListResult(ref: Query): MutableLiveData<List<T>> {
        val liveData = MutableLiveData<List<T>>()

        ref.addSnapshotListener { snapshot, exception ->

            exception?.let {
                Logger.d("[${this::class.simpleName}] Error getting documents. ${it.message}")
            }
                val list = mutableListOf<T>()
                for (document in snapshot!!) {
                    val data = document.toObject(this::class.java)
                    list.add(data)
                }

                liveData.value = list
        }

        return liveData
    }

    suspend fun Task<*>.taskSuccessReturn(ifSuccess: Boolean): Result<Boolean> =
        suspendCoroutine { continuation ->
            addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(Result.Success(ifSuccess))
                } else {
                    when (val exception = task.exception) {
                        null -> continuation.resume(Result.Fail(Util.getString(R.string.fail)))
                        else -> {
                            Logger.d("[${this::class.simpleName}] Error getting documents. ${exception.message}")
                            continuation.resume(Result.Error(exception))
                        }
                    }
                }
            }
        }

}
