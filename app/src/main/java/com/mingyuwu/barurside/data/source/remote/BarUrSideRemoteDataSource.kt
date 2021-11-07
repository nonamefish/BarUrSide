package com.mingyuwu.barurside.data.source.remote

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.mingyuwu.barurside.BarUrSideApplication
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.data.*
import com.mingyuwu.barurside.data.source.BarUrSideDataSource
import com.mingyuwu.barurside.filter.FilterParameter
import java.io.File
import java.sql.Timestamp
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


const val TAG = "RemoteDataSource"

object BarUrSideRemoteDataSource : BarUrSideDataSource {
    private const val PATH_VENUE = "venue"
    private const val PATH_USER = "user"
    private const val PATH_DRINK = "drink"
    private const val PATH_RATING = "rating"
    private const val PATH_ACTIVITY = "activity"
    private const val PATH_COLLECT = "collect"
    private const val PATH_NOTIFICATION = "notification"

    override fun getVenue(id: String): MutableLiveData<Venue> {
        val liveData = MutableLiveData<Venue>()

        FirebaseFirestore.getInstance()
            .collection(PATH_VENUE)
            .whereEqualTo("id", id)
            .addSnapshotListener { snapshot, exception ->
                Log.d(TAG, "getVenue snapshot ${snapshot!!.documents}")
                exception?.let {
                    Log.d(TAG, "[${this::class.simpleName}] Error getting documents. ${it.message}")
                }

                for (document in snapshot!!) {
                    val venue = document.toObject(Venue::class.java)
                    liveData.value = venue

                }
                liveData.value = liveData.value
                Log.d(TAG, "getVenue liveData ${liveData}")
            }
        Log.d(TAG, "getVenue return ${liveData}")
        return liveData
    }

    override fun getActivityResult(): MutableLiveData<List<Activity>> {

        val liveData = MutableLiveData<List<Activity>>()

        FirebaseFirestore.getInstance()
            .collection(PATH_ACTIVITY)
            .whereGreaterThan("endTime", Timestamp(System.currentTimeMillis()))
            .orderBy("endTime")
            .addSnapshotListener { snapshot, exception ->

                exception?.let {
                    Log.d(TAG, "[${this::class.simpleName}] Error getting documents. ${it.message}")
                }
                val list = mutableListOf<Activity>()
                for (document in snapshot!!) {
                    val activity = document.toObject(Activity::class.java)
                    activity.startTimestamp = activity.startTime?.let { Timestamp(it.time) }
                    activity.endTimestamp = activity.endTime?.let { Timestamp(it.time) }
                    list.add(activity)
                }

                liveData.value = list
            }
        return liveData
    }

    override fun getDrink(id: String): MutableLiveData<Drink> {
        val liveData = MutableLiveData<Drink>()

        FirebaseFirestore.getInstance()
            .collection(PATH_DRINK)
            .whereEqualTo("id", id)
            .addSnapshotListener { snapshot, exception ->
                Log.d(TAG, "getDrink snapshot ${snapshot!!.documents}")
                exception?.let {
                    Log.d(TAG, "[${this::class.simpleName}] Error getting documents. ${it.message}")
                }

                for (document in snapshot!!) {
                    val drink = document.toObject(Drink::class.java)
                    liveData.value = drink
                }

            }

        return liveData
    }

    override fun getUser(id: String): MutableLiveData<User> {
        val liveData = MutableLiveData<User>()

        FirebaseFirestore.getInstance()
            .collection(PATH_USER)
            .whereEqualTo("id", id)
            .addSnapshotListener { snapshot, exception ->
                Log.d(TAG, "getUser snapshot ${snapshot!!.documents}")
                exception?.let {
                    Log.d(TAG, "[${this::class.simpleName}] Error getting documents. ${it.message}")
                }

                for (document in snapshot!!) {
                    val user = document.toObject(User::class.java)
                    liveData.value = user
                }
            }
        return liveData
    }

    override suspend fun getFriend(frds: List<String>): Result<List<User>> =
        suspendCoroutine { continuation ->
            val list = mutableListOf<User>()

            FirebaseFirestore.getInstance()
                .collection(PATH_USER)
                .whereIn("id", frds)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        for (document in task.result!!) {
                            Log.d(TAG, document.id + " => " + document.data)
                            val friend = document.toObject(User::class.java)
                            list.add(friend)
                        }
                        continuation.resume(Result.Success(list))

                    } else {
                        task.exception?.let {
                            Log.w(
                                TAG,
                                "[${this::class.simpleName}] Error getting documents. ${it.message}"
                            )
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(
                            Result.Fail(
                                BarUrSideApplication.instance.getString(
                                    R.string.fail_nothing
                                )
                            )
                        )
                    }
                }
        }

    override suspend fun getMenu(venueId: String): Result<List<Drink>> =
        suspendCoroutine { continuation ->
            val list = mutableListOf<Drink>()
            FirebaseFirestore.getInstance()
                .collection(PATH_DRINK)
                .whereEqualTo("venueId", venueId)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            Log.d(TAG, document.id + " => " + document.data)

                            val drink = document.toObject(Drink::class.java)
                            list.add(drink)
                        }
                        continuation.resume(Result.Success(list))
                    } else {
                        task.exception?.let {
                            Log.w(
                                TAG,
                                "[${this::class.simpleName}] Error getting documents. ${it.message}"
                            )
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(
                            Result.Fail(
                                BarUrSideApplication.instance.getString(
                                    R.string.fail_nothing
                                )
                            )
                        )
                    }
                }
        }

    override suspend fun getVenueByFilter(filter: FilterParameter): Result<List<Venue>> =
        suspendCoroutine { continuation ->

            val list = mutableListOf<Venue>()

            // filter venue
            FirebaseFirestore.getInstance()
                .collection(PATH_VENUE)
                .whereIn("style", filter.style ?: listOf())
                .whereEqualTo("level", filter.level)
                .get()
                .addOnCompleteListener { venueTask ->

                    if (venueTask.isSuccessful) {
                        for (document in venueTask.result!!) {
                            val venue = document.toObject(Venue::class.java)

                            if (filter.category == null) {
                                list.add(venue)

                            } else {
                                var venueCnt = 0
                                // filter drink category
                                FirebaseFirestore.getInstance()
                                    .collection(PATH_DRINK)
                                    .whereIn("category", filter.category)
                                    .whereEqualTo("venueId", venue.id)
                                    .get()
                                    .addOnCompleteListener { drinkTask ->
                                        if (drinkTask.isSuccessful && drinkTask.result.size() > 0) {
                                            list.add(venue)

                                        } else {
                                            drinkTask.exception?.let {
                                                continuation.resume(Result.Error(it))
                                                return@addOnCompleteListener
                                            }
                                            continuation.resume(
                                                Result.Fail(
                                                    BarUrSideApplication.instance.getString(
                                                        R.string.fail_nothing
                                                    )
                                                )
                                            )
                                        }
                                        venueCnt += 1
                                        if (venueCnt == venueTask.result.size()) {
                                            continuation.resume(Result.Success(list))
                                        }
                                    }
                            }
                        }
                    } else {
                        venueTask.exception?.let {
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(
                            Result.Fail(
                                BarUrSideApplication.instance.getString(
                                    R.string.fail_nothing
                                )
                            )
                        )
                    }
                }
        }


    override fun getRatingByObject(
        id: String,
        isVenue: Boolean
    ): MutableLiveData<List<RatingInfo>> {
        val liveData = MutableLiveData<List<RatingInfo>>()

        // get rating data
        FirebaseFirestore.getInstance()
            .collection(PATH_RATING)
            .whereEqualTo("isVenue", isVenue)
            .whereEqualTo("objectId", id)
            .addSnapshotListener { snapshot, exception ->

                exception?.let {
                    Log.d(TAG, "[${this::class.simpleName}] Error getting documents. ${it.message}")
                }

                val list = mutableListOf<RatingInfo>()
                for (document in snapshot!!) {
                    val rating = document.toObject(RatingInfo::class.java)
                    rating.postTimestamp = rating.postDate?.let { Timestamp(it.time) }

                    // get user info
                    FirebaseFirestore.getInstance()
                        .collection(PATH_USER)
                        .whereEqualTo("id", rating.userId)
                        .get()
                        .addOnCompleteListener { task ->
                            for (document in task.result!!) {
                                val user = document.toObject(User::class.java)
                                rating.userInfo = user

                                // get object info : venue
                                when (isVenue) {
                                    true -> {
                                        FirebaseFirestore.getInstance()
                                            .collection(PATH_VENUE)
                                            .whereEqualTo("id", id)
                                            .get()
                                            .addOnCompleteListener { task ->
                                                for (document in task.result!!) {
                                                    val venue = document.toObject(Venue::class.java)
                                                    rating.objectName = venue.name
                                                    rating.objectImg = venue.images?.get(0) ?: ""
                                                }
                                                list.add(rating)
                                                liveData.value = list
                                            }
                                    }
                                    // get object info : drink
                                    false -> {
                                        FirebaseFirestore.getInstance()
                                            .collection(PATH_DRINK)
                                            .whereEqualTo("id", id)
                                            .get()
                                            .addOnCompleteListener { task ->
                                                for (document in task.result!!) {
                                                    val drink = document.toObject(Drink::class.java)
                                                    rating.objectName = drink.name
                                                    rating.objectImg = drink.images?.get(0) ?: ""
                                                }
                                                list.add(rating)
                                                liveData.value = list
                                            }
                                    }
                                }
                            }
                        }
                }
            }

        return liveData
    }

    override suspend fun getRatingByUser(userId: String): Result<List<RatingInfo>> =
        suspendCoroutine { continuation ->

            FirebaseFirestore.getInstance()
                .collection(PATH_RATING)
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener { rtgTask ->
                    if (rtgTask.isSuccessful) {
                        val list = mutableListOf<RatingInfo>()

                        for (document in rtgTask.result!!) {
                            val rating = document.toObject(RatingInfo::class.java)

                            rating.postTimestamp = rating.postDate?.let { Timestamp(it.time) }

                            // get object info : venue
                            when (rating.isVenue) {
                                true -> {
                                    FirebaseFirestore.getInstance()
                                        .collection(PATH_VENUE)
                                        .whereEqualTo("id", rating.objectId)
                                        .get()
                                        .addOnCompleteListener { task ->
                                            for (document in task.result!!) {
                                                val venue = document.toObject(Venue::class.java)
                                                rating.objectName = venue.name
                                                rating.objectImg = venue.images?.get(0) ?: ""
                                            }
                                            list.add(rating)

                                            if (document == rtgTask.result!!.last()) {

                                                continuation.resume(Result.Success(list))
                                            }
                                        }
                                }
                                // get object info : drink
                                false -> {
                                    FirebaseFirestore.getInstance()
                                        .collection(PATH_DRINK)
                                        .whereEqualTo("id", rating.objectId)
                                        .get()
                                        .addOnCompleteListener { task ->
                                            for (document in task.result!!) {
                                                val drink = document.toObject(Drink::class.java)
                                                rating.objectName = drink.name
                                                rating.objectImg = drink.images?.get(0) ?: ""
                                            }
                                            list.add(rating)

                                            if (document == rtgTask.result!!.last()) {

                                                continuation.resume(Result.Success(list))
                                            }

                                        }
                                }
                            }

                        }
                    } else {
                        rtgTask.exception?.let {
                            Log.w(
                                TAG,
                                "[${this::class.simpleName}] Error getting documents. ${it.message}"
                            )
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(
                            Result.Fail(
                                BarUrSideApplication.instance.getString(
                                    R.string.fail_nothing
                                )
                            )
                        )
                    }
                }
        }

    override suspend fun postRating(rating: Rating): Result<Boolean> =
        suspendCoroutine { continuation ->
            val ratings = FirebaseFirestore.getInstance().collection(PATH_RATING)
            val document = ratings.document()

            rating.id = document.id
            rating.postDate = Timestamp(System.currentTimeMillis())

            document
                .set(Rating.toHashMap(rating))
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {

                            Log.w(
                                TAG,
                                "[${this::class.simpleName}] Error getting documents. ${it.message}"
                            )
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(BarUrSideApplication.instance.getString(R.string.fail_nothing)))
                    }
                }
        }

    override suspend fun getCollect(userId: String): Result<List<Collect>> =
        suspendCoroutine { continuation ->
            val list = mutableListOf<Collect>()
            FirebaseFirestore.getInstance()
                .collection(PATH_COLLECT)
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        for (document in task.result!!) {

                            val collect = document.toObject(Collect::class.java)
                            list.add(collect)
                        }
                        continuation.resume(Result.Success(list))
                    } else {
                        task.exception?.let {
                            Log.w(
                                TAG,
                                "[${this::class.simpleName}] Error getting documents. ${it.message}"
                            )
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(
                            Result.Fail(
                                BarUrSideApplication.instance.getString(
                                    R.string.fail_nothing
                                )
                            )
                        )
                    }
                }
        }

    override suspend fun removeCollect(id: String, userId: String): Result<Boolean> =
        suspendCoroutine { continuation ->

            FirebaseFirestore.getInstance()
                .collection(PATH_COLLECT)
                .whereEqualTo("objectId", id)
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        for (document in task.result!!) {
                            Log.d(TAG, "document: $document")
                            document.reference.delete()
                        }
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {
                            Log.w(
                                TAG,
                                "[${this::class.simpleName}] Error getting documents. ${it.message}"
                            )
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(
                            Result.Fail(
                                BarUrSideApplication.instance.getString(
                                    R.string.fail_nothing
                                )
                            )
                        )
                    }
                }
        }

    override suspend fun uploadPhoto(
        storageRef: StorageReference,
        userId: String,
        uploadType: String,
        localImage: String
    ): Result<String> =
        suspendCoroutine { continuation ->
            val file = Uri.fromFile(File(localImage))
            val eventsRef = storageRef.child("$uploadType/$userId/${file.lastPathSegment}" ?: "")
            val uploadTask = eventsRef.putFile(file)
            uploadTask
                .addOnSuccessListener {

                    eventsRef.downloadUrl.addOnSuccessListener {
                        Log.i(TAG, "Success: $it")
                        continuation.resume(Result.Success(it.toString()))
                    }.addOnFailureListener { exception ->
                        Log.i(TAG, exception.toString())
                    }

                }
                .addOnFailureListener { exception ->
                    Log.i(TAG, exception.toString())
                }
        }

    override suspend fun updateObjectRating(
        id: String,
        isVenue: Boolean,
        rating: Rating
    ): Result<Boolean> =
        suspendCoroutine { continuation ->

            when (isVenue) {
                true -> {
                    FirebaseFirestore.getInstance()
                        .collection(PATH_VENUE)
                        .document(id)
                        .get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                var venue = task.result.toObject(Venue::class.java)
                                val imgs = when (venue?.images) {
                                    null -> rating?.images
                                    else -> rating?.images?.plus(venue?.images as List<String>)
                                }

                                val newRtg =
                                    (venue?.rtgCount?.times(venue?.avgRating)
                                        ?.plus(rating!!.rating!!))?.div(venue?.rtgCount!!.plus(1))

                                FirebaseFirestore.getInstance().collection(PATH_VENUE)
                                    .document(id)
                                    .update(
                                        mapOf(
                                            "images" to imgs,
                                            "avgRating" to newRtg,
                                            "rtgCount" to venue?.rtgCount!!.plus(1)
                                        )
                                    )

                                continuation.resume(Result.Success(true))
                            } else {
                                task.exception?.let {
                                    Log.w(
                                        TAG,
                                        "[${this::class.simpleName}] Error getting documents. ${it.message}"
                                    )
                                    continuation.resume(Result.Error(it))
                                    return@addOnCompleteListener
                                }
                                continuation.resume(
                                    Result.Fail(
                                        BarUrSideApplication.instance.getString(
                                            R.string.fail_nothing
                                        )
                                    )
                                )
                            }
                        }
                }
                false -> {
                    FirebaseFirestore.getInstance()
                        .collection(PATH_DRINK)
                        .document(id)
                        .get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val drink = task.result.toObject(Venue::class.java)
                                val newRtg =
                                    (drink?.rtgCount?.times(drink?.avgRating)
                                        ?.plus(rating!!.rating!!))?.div(drink?.rtgCount!!.plus(1))

                                FirebaseFirestore.getInstance().collection(PATH_DRINK)
                                    .document(id)
                                    .update(
                                        mapOf(
                                            "images" to rating?.images?.plus(drink?.images as List<String>),
                                            "avgRating" to newRtg,
                                            "rtgCount" to drink?.rtgCount!!.plus(1)
                                        )
                                    )

                                continuation.resume(Result.Success(true))

                            } else {
                                task.exception?.let {
                                    Log.w(
                                        TAG,
                                        "[${this::class.simpleName}] Error getting documents. ${it.message}"
                                    )
                                    continuation.resume(Result.Error(it))
                                    return@addOnCompleteListener
                                }
                                continuation.resume(
                                    Result.Fail(
                                        BarUrSideApplication.instance.getString(
                                            R.string.fail_nothing
                                        )
                                    )
                                )
                            }
                        }
                }
            }
        }

    override suspend fun updateUserShare(
        userId: String,
        addShareCnt: Int,
        addShareImgCnt: Int
    ): Result<Boolean> =
        suspendCoroutine { continuation ->

            FirebaseFirestore.getInstance()
                .collection(PATH_USER)
                .document(userId)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = task.result.toObject(User::class.java)

                        FirebaseFirestore.getInstance()
                            .collection(PATH_USER)
                            .document(userId)
                            .update(
                                hashMapOf(
                                    "shareCount" to user?.shareCount?.plus(addShareCnt),
                                    "shareImageCount" to user?.shareImageCount?.plus(addShareImgCnt)
                                ) as Map<String, Any>
                            )

                        continuation.resume(Result.Success(true))

                    } else {
                        task.exception?.let {
                            Log.w(
                                TAG,
                                "[${this::class.simpleName}] Error getting documents. ${it.message}"
                            )
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(
                            Result.Fail(
                                BarUrSideApplication.instance.getString(
                                    R.string.fail_nothing
                                )
                            )
                        )
                    }
                }
        }

    override suspend fun getVenueByLocation(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double
    ): Result<List<Venue>> =
        suspendCoroutine { continuation ->
            val list = mutableListOf<Venue>()
            val ref = FirebaseFirestore.getInstance().collection(PATH_VENUE)

            ref.whereGreaterThan("latitude", minLat)
                .whereLessThan("latitude", maxLat)

            ref.whereGreaterThan("longitude", minLng)
                .whereLessThan("longitude", maxLng)
                .get()
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            val venue = document.toObject(Venue::class.java)
                            list.add(venue)
                        }
                        continuation.resume(Result.Success(list))
                    } else {
                        task.exception?.let {
                            Log.w(
                                TAG,
                                "[${this::class.simpleName}] Error getting documents. ${it.message}"
                            )
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(
                            Result.Fail(
                                BarUrSideApplication.instance.getString(
                                    R.string.fail_nothing
                                )
                            )
                        )
                    }
                }
        }

    override suspend fun getDrinkByRating(id: String): Result<Drink> =
        suspendCoroutine { continuation ->

            FirebaseFirestore.getInstance()
                .collection(PATH_DRINK)
                .whereEqualTo("id", id)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            val drink = document.toObject(Drink::class.java)
                            continuation.resume(Result.Success(drink))
                        }
                    } else {
                        task.exception?.let {
                            Log.w(
                                TAG,
                                "[${this::class.simpleName}] Error getting documents. ${it.message}"
                            )
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(
                            Result.Fail(
                                BarUrSideApplication.instance.getString(
                                    R.string.fail_nothing
                                )
                            )
                        )
                    }
                }
        }

    override suspend fun getVenueBySearch(search: String): Result<List<Venue>> =
        suspendCoroutine { continuation ->

            val list = mutableListOf<Venue>()

            // filter venue
            FirebaseFirestore.getInstance()
                .collection(PATH_VENUE)
                .orderBy("name")
                .startAt(search)
                .endAt("$search\uf8ff")
                .get()
                .addOnCompleteListener { venueTask ->

                    if (venueTask.isSuccessful) {
                        for (document in venueTask.result!!) {
                            val venue = document.toObject(Venue::class.java)
                            list.add(venue)
                        }
                        continuation.resume(Result.Success(list))
                    } else {
                        venueTask.exception?.let {
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(
                            Result.Fail(
                                BarUrSideApplication.instance.getString(
                                    R.string.fail_nothing
                                )
                            )
                        )
                    }
                }
        }

    override fun getNotification(userId: String): MutableLiveData<List<Notification>> {
        val liveData = MutableLiveData<List<Notification>>()

        // notification from user
        FirebaseFirestore.getInstance()
            .collection(PATH_NOTIFICATION)
            .whereEqualTo("toId", userId)
            .orderBy("date",Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->

                exception?.let {
                    Log.d(TAG, "[${this::class.simpleName}] Error getting documents. ${it.message}")
                }
                val list = mutableListOf<Notification>()
                for (document in snapshot!!) {
                    val notification = document.toObject(Notification::class.java)
                    notification.timestamp = notification.date?.let { Timestamp(it.time) }
                    list.add(notification)
                }

                // notification send user
                FirebaseFirestore.getInstance()
                    .collection(PATH_NOTIFICATION)
                    .whereEqualTo("fromId", userId)
                    .orderBy("date",Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, exception ->
                        exception?.let {
                            Log.d(
                                TAG,
                                "[${this::class.simpleName}] Error getting documents. ${it.message}"
                            )
                        }
                        for (document in snapshot!!) {
                            val notification = document.toObject(Notification::class.java)
                            notification.timestamp = notification.date?.let { Timestamp(it.time) }
                            list.add(notification)
                        }
                        liveData.value = list
                    }
            }
        return liveData
    }


    override suspend fun getVenueByIds(ids: List<String>): Result<List<Venue>> =
        suspendCoroutine { continuation ->

            val list = mutableListOf<Venue>()

            // filter venue
            FirebaseFirestore.getInstance()
                .collection(PATH_VENUE)
                .whereIn("id", ids)
                .get()
                .addOnCompleteListener { venueTask ->

                    if (venueTask.isSuccessful) {
                        for (document in venueTask.result!!) {
                            val venue = document.toObject(Venue::class.java)
                            list.add(venue)
                        }
                        continuation.resume(Result.Success(list))
                    } else {
                        venueTask.exception?.let {
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(
                            Result.Fail(
                                BarUrSideApplication.instance.getString(
                                    R.string.fail_nothing
                                )
                            )
                        )
                    }
                }
        }

    override suspend fun getDrinksByIds(ids: List<String>): Result<List<Drink>> =
        suspendCoroutine { continuation ->

            val list = mutableListOf<Drink>()

            // filter venue
            FirebaseFirestore.getInstance()
                .collection(PATH_DRINK)
                .whereIn("id", ids)
                .get()
                .addOnCompleteListener { drinkTask ->

                    if (drinkTask.isSuccessful) {
                        for (document in drinkTask.result!!) {
                            val drink = document.toObject(Drink::class.java)
                            list.add(drink)
                        }
                        continuation.resume(Result.Success(list))
                    } else {
                        drinkTask.exception?.let {
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(
                            Result.Fail(
                                BarUrSideApplication.instance.getString(
                                    R.string.fail_nothing
                                )
                            )
                        )
                    }
                }
        }


    override suspend fun getDrinkBySearch(search: String): Result<List<Drink>> =
        suspendCoroutine { continuation ->

            val list = mutableListOf<Drink>()

            // filter venue
            FirebaseFirestore.getInstance()
                .collection(PATH_DRINK)
                .orderBy("name")
                .startAt(search.uppercase())
                .endAt("${search.lowercase()}\uf8ff")
                .get()
                .addOnCompleteListener { venueTask ->

                    if (venueTask.isSuccessful) {
                        for (document in venueTask.result!!) {
                            val drink = document.toObject(Drink::class.java)
                            list.add(drink)
                        }
                        continuation.resume(Result.Success(list))
                    } else {
                        venueTask.exception?.let {
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(
                            Result.Fail(
                                BarUrSideApplication.instance.getString(
                                    R.string.fail_nothing
                                )
                            )
                        )
                    }
                }
        }

    override suspend fun getHotVenueResult(): Result<List<Venue>> =
        suspendCoroutine { continuation ->

            val list = mutableListOf<Venue>()

            // get date
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MONTH, -1)

            // filter rating
            FirebaseFirestore.getInstance()
                .collection(PATH_RATING)
                .whereEqualTo("isVenue", true)
                .whereGreaterThan("postDate", calendar.time)
                .get()
                .addOnCompleteListener { venueTask ->

                    if (venueTask.isSuccessful) {

                        val venueRtgs = venueTask.result.toObjects(Rating::class.java)
                        val hotVenueList = venueRtgs.groupingBy { it.objectId }.eachCount().toList()
                            .sortedByDescending { it.second }.take(10)

                        // filter venue
                        FirebaseFirestore.getInstance()
                            .collection(PATH_VENUE)
                            .whereIn("id", hotVenueList.map { it.first })
                            .get()
                            .addOnCompleteListener { task ->
                                for (document in task.result!!) {
                                    val venue = document.toObject(Venue::class.java)
                                    list.add(venue)
                                }
                                continuation.resume(Result.Success(list))
                            }
                    } else {
                        venueTask.exception?.let {
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(
                            Result.Fail(
                                BarUrSideApplication.instance.getString(
                                    R.string.fail_nothing
                                )
                            )
                        )
                    }
                }
        }

    override suspend fun getHotDrinkResult(): Result<List<Drink>> =
        suspendCoroutine { continuation ->

            val list = mutableListOf<Drink>()

            // get date
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MONTH, -1)

            // filter rating
            FirebaseFirestore.getInstance()
                .collection(PATH_RATING)
                .whereEqualTo("isVenue", false)
                .whereGreaterThan("postDate", calendar.time)
                .get()
                .addOnCompleteListener { drinkTask ->

                    if (drinkTask.isSuccessful) {

                        val drinkRtgs = drinkTask.result.toObjects(Rating::class.java)
                        val hotDrinkList = drinkRtgs.groupingBy { it.objectId }.eachCount().toList()
                            .sortedByDescending { it.second }.take(10)

                        // filter drink
                        FirebaseFirestore.getInstance()
                            .collection(PATH_DRINK)
                            .whereIn("id", hotDrinkList.map { it.first })
                            .get()
                            .addOnCompleteListener { task ->
                                for (document in task.result!!) {
                                    val drink = document.toObject(Drink::class.java)
                                    list.add(drink)
                                }
                                continuation.resume(Result.Success(list))
                            }
                    } else {
                        drinkTask.exception?.let {
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(
                            Result.Fail(
                                BarUrSideApplication.instance.getString(
                                    R.string.fail_nothing
                                )
                            )
                        )
                    }
                }
        }


    override suspend fun getHighRateVenueResult(): Result<List<Venue>> =
        suspendCoroutine { continuation ->

            val list = mutableListOf<Venue>()

            // filter venue
            FirebaseFirestore.getInstance()
                .collection(PATH_VENUE)
                .orderBy("avgRating", Query.Direction.DESCENDING)
                .orderBy("rtgCount", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener { venueTask ->

                    if (venueTask.isSuccessful) {

                        for (document in venueTask.result!!) {
                            Log.d(TAG, document.id + " => " + document.data)
                            val venue = document.toObject(Venue::class.java)
                            list.add(venue)
                        }
                        continuation.resume(Result.Success(list))
                    } else {
                        venueTask.exception?.let {
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(
                            Result.Fail(
                                BarUrSideApplication.instance.getString(
                                    R.string.fail_nothing
                                )
                            )
                        )
                    }
                }
        }

    override suspend fun getHighRateDrinkResult(): Result<List<Drink>> =
        suspendCoroutine { continuation ->

            val list = mutableListOf<Drink>()

            // filter venue
            FirebaseFirestore.getInstance()
                .collection(PATH_DRINK)
                .orderBy("avgRating", Query.Direction.DESCENDING)
                .orderBy("rtgCount", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener { drinkTask ->

                    if (drinkTask.isSuccessful) {
                        for (document in drinkTask.result!!) {
                            val drink = document.toObject(Drink::class.java)
                            list.add(drink)
                        }
                        continuation.resume(Result.Success(list))
                    } else {
                        drinkTask.exception?.let {
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(
                            Result.Fail(
                                BarUrSideApplication.instance.getString(
                                    R.string.fail_nothing
                                )
                            )
                        )
                    }
                }
        }


    override suspend fun getActivityByUser(userId: String): Result<List<Activity>> =
        suspendCoroutine { continuation ->
            val list = mutableListOf<Activity>()

            // filter activity
            FirebaseFirestore.getInstance()
                .collection(PATH_ACTIVITY)
//                .whereArrayContains("bookers", mapOf("id" to userId, "date" to null))
                .get()
                .addOnCompleteListener { activityTask ->
                    if (activityTask.isSuccessful) {
                        for (document in activityTask.result!!) {
                            val activity = document.toObject(Activity::class.java)
                            activity.bookers?.let { bookers ->
                                if (bookers.any { it.id == userId }) {
                                    list.add(activity)
                                }
                            }
                        }
                        continuation.resume(Result.Success(list))
                    } else {
                        activityTask.exception?.let {
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(
                            Result.Fail(
                                BarUrSideApplication.instance.getString(
                                    R.string.fail_nothing
                                )
                            )
                        )
                    }
                }
        }

    override suspend fun getRatingByRecommend(): Result<List<RatingInfo>> =
        suspendCoroutine { continuation ->
            val list = mutableListOf<RatingInfo>()
            val firestore = FirebaseFirestore.getInstance()
            var check = 0

            // get recommend user data
            firestore.collection(PATH_USER)
                .orderBy("shareCount", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener { userTask ->

                    if (userTask.isSuccessful) {
                        for (docUser in userTask.result!!) {
                            val user = docUser.toObject(User::class.java)

                            // get rating data
                            firestore.collection(PATH_RATING)
                                .whereEqualTo("userId", user.id)
                                .whereEqualTo("isVenue", true)
                                .orderBy("postDate", Query.Direction.DESCENDING)
                                .limit(1)
                                .get()
                                .addOnCompleteListener { rtgTask ->
                                    if (rtgTask.result.size() == 0) {
                                        check += 1
                                    }
                                    for (docRtg in rtgTask.result!!) {
                                        var rtg = docRtg.toObject(RatingInfo::class.java)
                                        rtg.postTimestamp = rtg.postDate?.let { Timestamp(it.time) }

                                        // get user info
                                        firestore.collection(PATH_USER)
                                            .whereEqualTo("id", rtg.userId)
                                            .get()
                                            .addOnCompleteListener { task ->
                                                for (document in task.result!!) {
                                                    val user = document.toObject(User::class.java)
                                                    rtg.userInfo = user

                                                    // get venue Info
                                                    firestore.collection(PATH_VENUE)
                                                        .whereEqualTo("id", rtg.objectId)
                                                        .limit(1)
                                                        .get()
                                                        .addOnCompleteListener { venueTask ->
                                                            check += 1
                                                            for (docVenue in venueTask.result!!) {
                                                                val venue =
                                                                    docVenue.toObject(Venue::class.java)
                                                                rtg.objectName = venue.name
                                                                rtg.objectImg =
                                                                    venue.images?.get(0) ?: ""
                                                                list.add(rtg)
                                                            }

                                                            if (check == userTask.result.size()) {
                                                                continuation.resume(
                                                                    Result.Success(list.sortedByDescending { it.postDate })
                                                                )
                                                            }
                                                        }
                                                }
                                            }
                                    }
                                }
                        }
                    } else {
                        userTask.exception?.let {
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(
                            Result.Fail(
                                BarUrSideApplication.instance.getString(
                                    R.string.fail_nothing
                                )
                            )
                        )
                    }
                }
        }

    override suspend fun getRatingByFriends(userId: String): Result<List<RatingInfo>> =
        suspendCoroutine { continuation ->
            val list = mutableListOf<RatingInfo>()
            val firestore = FirebaseFirestore.getInstance()
            var check = 0

            // get friend list
            firestore.collection(PATH_USER)
                .whereEqualTo("id", userId)
                .limit(10)
                .get()
                .addOnCompleteListener { userTask ->
                    if (userTask.isSuccessful) {

                        for (document in userTask.result!!) {
                            val friends = document.toObject(User::class.java).friends?.map { it.id }
                            friends?.let {

                                // get friend's rating
                                firestore.collection(PATH_RATING)
                                    .whereIn("userId", it)
                                    .orderBy("postDate", Query.Direction.DESCENDING)
                                    .limit(10)
                                    .get()
                                    .addOnCompleteListener { rtgTask ->

                                        if (rtgTask.isSuccessful) {
                                            if (rtgTask.result.size() == 0) {
                                                check += 1
                                            }

                                            for (document in rtgTask.result!!) {

                                                val rtg =
                                                    document.toObject(RatingInfo::class.java)
                                                rtg.postTimestamp =
                                                    rtg.postDate?.let { Timestamp(it.time) }

                                                // get user info
                                                firestore.collection(PATH_USER)
                                                    .whereEqualTo("id", rtg.userId)
                                                    .get()
                                                    .addOnCompleteListener { task ->
                                                        for (document in task.result!!) {
                                                            val user =
                                                                document.toObject(User::class.java)
                                                            rtg.userInfo = user

                                                            // get object info : venue
                                                            when (rtg.isVenue) {
                                                                true -> {
                                                                    firestore.collection(PATH_VENUE)
                                                                        .whereEqualTo(
                                                                            "id",
                                                                            rtg.objectId
                                                                        )
                                                                        .get()
                                                                        .addOnCompleteListener { task ->
                                                                            check += 1

                                                                            for (document in task.result!!) {
                                                                                val venue =
                                                                                    document.toObject(
                                                                                        Venue::class.java
                                                                                    )
                                                                                rtg.objectName =
                                                                                    venue.name
                                                                                rtg.objectImg =
                                                                                    venue.images?.get(
                                                                                        0
                                                                                    ) ?: ""
                                                                            }

                                                                            list.add(rtg)

                                                                            if (check == rtgTask.result.size()) {
                                                                                continuation.resume(
                                                                                    Result.Success(
                                                                                        list
                                                                                    )
                                                                                )
                                                                            }
                                                                        }
                                                                }

                                                                // get object info : drink
                                                                false -> {
                                                                    firestore.collection(PATH_DRINK)
                                                                        .whereEqualTo(
                                                                            "id",
                                                                            rtg.objectId
                                                                        )
                                                                        .get()
                                                                        .addOnCompleteListener { task ->
                                                                            check += 1
                                                                            for (document in task.result!!) {
                                                                                val drink =
                                                                                    document.toObject(
                                                                                        Drink::class.java
                                                                                    )
                                                                                rtg.objectName =
                                                                                    drink.name
                                                                                rtg.objectImg =
                                                                                    drink.images?.get(
                                                                                        0
                                                                                    ) ?: ""
                                                                            }
                                                                            list.add(rtg)
                                                                            if (check == rtgTask.result.size()) {
                                                                                continuation.resume(
                                                                                    Result.Success(
                                                                                        list
                                                                                    )
                                                                                )
                                                                            }
                                                                        }
                                                                }
                                                            }
                                                        }
                                                    }
                                            }
                                        }
                                    }
                            }
                        }
                    } else {
                        userTask.exception?.let {
                            Log.w(
                                TAG,
                                "[${this::class.simpleName}] Error getting documents. ${it.message}"
                            )
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(
                            Result.Fail(
                                BarUrSideApplication.instance.getString(
                                    R.string.fail_nothing
                                )
                            )
                        )
                    }
                }
        }

    override suspend fun postCollect(collect: Collect): Result<Boolean> =
        suspendCoroutine { continuation ->
            val postCollect =
                FirebaseFirestore.getInstance().collection(PATH_COLLECT)
            val document = postCollect.document()

            collect.id = document.id

            document
                .set(Collect.toHashMap(collect))
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "postCollect: isSuccessful")
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {
                            Log.d(TAG, "postCollect: $it")
                            Log.w(
                                TAG,
                                "[${this::class.simpleName}] Error getting documents. ${it.message}"
                            )
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(
                            Result.Fail(
                                BarUrSideApplication.instance.getString(
                                    R.string.fail_nothing
                                )
                            )
                        )
                    }
                }
        }

    override suspend fun postActivity(activity: Activity): Result<Boolean> =
        suspendCoroutine { continuation ->
            val postCollect =
                FirebaseFirestore.getInstance().collection(PATH_ACTIVITY)
            val document = postCollect.document()
            activity.id = document.id

            document
                .set(Activity.toHashMap(activity))
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "postCollect: isSuccessful")
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {
                            Log.d(TAG, "postCollect: $it")
                            Log.w(
                                TAG,
                                "[${this::class.simpleName}] Error getting documents. ${it.message}"
                            )
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(
                            Result.Fail(
                                BarUrSideApplication.instance.getString(
                                    R.string.fail_nothing
                                )
                            )
                        )
                    }
                }
        }

    override suspend fun addFriend(notification: Notification): Result<Boolean> =
        suspendCoroutine { continuation ->
            val notifications = FirebaseFirestore.getInstance().collection(PATH_NOTIFICATION)
            val document = notifications.document()

            notification.id = document.id

            document
                .set(Notification.toHashMap(notification))
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {

                            Log.w(
                                TAG,
                                "[${this::class.simpleName}] Error getting documents. ${it.message}"
                            )
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(
                            Result.Fail(
                                BarUrSideApplication.instance.getString(R.string.fail_nothing)
                            )
                        )
                    }
                }
        }

    override suspend fun modifyActivity(activityId: String, userId: String): Result<Boolean> =
        suspendCoroutine { continuation ->
//            Log.d("Ming", "activityId: $activityId, userId: $userId")
            FirebaseFirestore.getInstance()
                .collection(PATH_ACTIVITY)
                .whereEqualTo("id", activityId)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            val activity = document.toObject(Activity::class.java)

                            if (activity.sponsor == userId) {
                                document.reference.delete()
                            } else {
                                document.reference
                                    .update("bookers", activity.bookers?.filter { it.id != userId })
                                    .addOnSuccessListener {
                                        Log.d(
                                            TAG,
                                            "DocumentSnapshot successfully updated!"
                                        )
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w(
                                            TAG,
                                            "Error updating document",
                                            e
                                        )
                                    }
                            }
                        }
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {
                            Log.w(
                                TAG,
                                "[${this::class.simpleName}] Error getting documents. ${it.message}"
                            )
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(
                            Result.Fail(
                                BarUrSideApplication.instance.getString(
                                    R.string.fail_nothing
                                )
                            )
                        )
                    }
                }
        }

    override suspend fun bookActivity(activityId: String, userId: String): Result<Boolean> =
        suspendCoroutine { continuation ->
//            Log.d("Ming", "activityId: $activityId, userId: $userId")
            FirebaseFirestore.getInstance()
                .collection(PATH_ACTIVITY)
                .whereEqualTo("id", activityId)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            val activity = document.toObject(Activity::class.java)

                            document.reference
                                .update(
                                    "bookers", activity.bookers?.plus(
                                        Relationship(userId, Timestamp(System.currentTimeMillis()))
                                    )
                                )
                                .addOnSuccessListener {
                                    Log.d(
                                        TAG,
                                        "DocumentSnapshot successfully updated!"
                                    )
                                }
                                .addOnFailureListener { e ->
                                    Log.w(
                                        TAG,
                                        "Error updating document",
                                        e
                                    )
                                }
                        }
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {
                            Log.w(
                                TAG,
                                "[${this::class.simpleName}] Error getting documents. ${it.message}"
                            )
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(
                            Result.Fail(
                                BarUrSideApplication.instance.getString(
                                    R.string.fail_nothing
                                )
                            )
                        )
                    }
                }
        }

    override suspend fun addUser(user: User): Result<Boolean> =
        suspendCoroutine { continuation ->
            val firestore = FirebaseFirestore.getInstance()

            firestore.collection(PATH_USER)
                .whereEqualTo("id", user.id)
                .get()
                .addOnCompleteListener { userTask ->
                    if (userTask.isSuccessful && userTask.result.isEmpty) {

                        val postUser = firestore.collection(PATH_USER).document(user.id)

                        postUser.set(User.toHashMap(user))
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    continuation.resume(Result.Success(true))
                                } else {
                                    task.exception?.let {
                                        Log.w(
                                            TAG,
                                            "[${this::class.simpleName}] Error getting documents. ${it.message}"
                                        )
                                        continuation.resume(Result.Error(it))
                                        return@addOnCompleteListener
                                    }
                                    continuation.resume(
                                        Result.Fail(
                                            BarUrSideApplication.instance.getString(
                                                R.string.fail_nothing
                                            )
                                        )
                                    )
                                }
                            }
                    } else {
                        continuation.resume(Result.Success(true))
                    }
                }
        }

    override suspend fun replyAddFriend(notify: Notification, reply: Boolean): Result<Boolean> =
        suspendCoroutine { continuation ->

            // delete frd request in notification
            FirebaseFirestore.getInstance()
                .collection(PATH_NOTIFICATION)
                .whereEqualTo("id", notify.id)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            document.reference
                                .set(Notification.toHashMap(notify))
                                .addOnSuccessListener {
                                    Log.d(TAG, "add successfully updated!")
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "error updated!")
                                }
                        }

                        // add to user's friend list
                        if (reply) {
                            FirebaseFirestore.getInstance()
                                .collection(PATH_USER)
                                .whereEqualTo("id", notify.fromId)
                                .get()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        for (document in task.result!!) {
                                            val user = document.toObject(User::class.java)
                                            val frd = Relationship(
                                                notify.toId,
                                                Timestamp(System.currentTimeMillis())
                                            )
                                            document.reference
                                                .update(
                                                    "friends",
                                                    user.friends?.plus(frd) ?: listOf(frd)
                                                )
                                                .addOnSuccessListener {
                                                    Log.d(TAG, "add successfully updated!")
                                                }
                                                .addOnFailureListener { e ->
                                                    Log.w(TAG, "error updated!")
                                                }
                                        }
                                    }

                                    FirebaseFirestore.getInstance()
                                        .collection(PATH_USER)
                                        .whereEqualTo("id", notify.toId)
                                        .get()
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                for (document in task.result!!) {
                                                    val user = document.toObject(User::class.java)
                                                    val frd = Relationship(
                                                        notify.fromId,
                                                        Timestamp(System.currentTimeMillis())
                                                    )
                                                    document.reference
                                                        .update(
                                                            "friends",
                                                            user.friends?.plus(frd) ?: listOf(frd)
                                                        )
                                                        .addOnSuccessListener {
                                                            Log.d(TAG, "add successfully updated!")
                                                        }
                                                        .addOnFailureListener { e ->
                                                            Log.w(TAG, "error updated!")
                                                        }
                                                }
                                            }
                                            continuation.resume(Result.Success(true))
                                        }
                                }
                        } else {
                            task.exception?.let {
                                Log.w(
                                    TAG,
                                    "[${this::class.simpleName}] Error getting documents. ${it.message}"
                                )
                                continuation.resume(Result.Error(it))
                                return@addOnCompleteListener
                            }
                            continuation.resume(
                                Result.Fail(
                                    BarUrSideApplication.instance.getString(
                                        R.string.fail_nothing
                                    )
                                )
                            )
                        }
                    }
                }
        }
}