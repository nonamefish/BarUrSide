package com.mingyuwu.barurside.data.source.remote

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.StorageReference
import com.mingyuwu.barurside.BarUrSideApplication
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.data.*
import com.mingyuwu.barurside.data.source.BarUrSideDataSource
import com.mingyuwu.barurside.filter.FilterParameter
import com.mingyuwu.barurside.util.Util.getListResult
import com.mingyuwu.barurside.util.Util.getLiveDataResult
import com.mingyuwu.barurside.util.Util.getLiveDataListResult
import com.mingyuwu.barurside.util.Util.getResult
import com.mingyuwu.barurside.util.Util.taskSuccessReturn
import com.google.firebase.firestore.FieldValue
import com.mingyuwu.barurside.util.Logger
import java.io.File
import java.sql.Timestamp
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.*


object BarUrSideRemoteDataSource : BarUrSideDataSource {

    private val db = FirebaseFirestore.getInstance()

    // firesotre collecttion
    private const val PATH_VENUE = "venue"
    private const val PATH_USER = "user"
    private const val PATH_DRINK = "drink"
    private const val PATH_RATING = "rating"
    private const val PATH_ACTIVITY = "activity"
    private const val PATH_COLLECT = "collect"
    private const val PATH_NOTIFICATION = "notification"

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var DataSourceJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(DataSourceJob + Dispatchers.Main)

    override fun getVenue(id: String): MutableLiveData<Venue> {
        return Venue().getLiveDataResult(
            db.collection(PATH_VENUE).whereEqualTo("id", id)
        )
    }

    override fun getActivityResult(): MutableLiveData<List<Activity>> {
        return Activity().getLiveDataListResult(
            db.collection(PATH_ACTIVITY)
                .whereGreaterThan("endTime", Timestamp(System.currentTimeMillis()))
                .orderBy("endTime")
        )
    }

    override fun getDrink(id: String): MutableLiveData<Drink> {
        return Drink().getLiveDataResult(
            db.collection(PATH_DRINK).whereEqualTo("id", id)
        )
    }

    override fun getUser(id: String): MutableLiveData<User> {
        return User().getLiveDataResult(
            db.collection(PATH_USER).whereEqualTo("id", id)
        )
    }

    override suspend fun getUsersResult(frds: List<String>): Result<List<User>> {
        return User().getListResult(
            db.collection(PATH_USER).whereIn("id", frds).get()
        )
    }

    override suspend fun getMenu(venueId: String): Result<List<Drink>> {
        return Drink().getListResult(
            db.collection(PATH_DRINK).whereEqualTo("venueId", venueId).get()
        )
    }

    override suspend fun getVenueByFilter(filter: FilterParameter): Result<List<Venue>> =
        suspendCoroutine { continuation ->
            val list = mutableListOf<Venue>()

            // filter venue
            db.collection(PATH_VENUE)
                .whereIn("style", filter.style ?: listOf())
                .whereEqualTo("level", filter.level)
                .get()
                .addOnCompleteListener { venueTask ->

                    var venueCnt = 0
                    if (venueTask.isSuccessful) {
                        for (document in venueTask.result!!) {
                            val venue = document.toObject(Venue::class.java)

                            if (filter.category.isNullOrEmpty()) {
                                list.add(venue)
                                venueCnt += 1
                                if (venueCnt == venueTask.result.size()) {
                                    continuation.resume(Result.Success(list))
                                }
                            } else {

                                // filter drink category
                                db
                                    .collection(PATH_DRINK)
                                    .whereIn("category", filter.category)
                                    .whereEqualTo("venueId", venue.id)
                                    .get()
                                    .addOnCompleteListener { drinkTask ->
                                        if (drinkTask.isSuccessful && drinkTask.result.size() > 0) {

                                            list.add(venue)
                                        }
                                        Log.d("Ming", "venue: ${venue.name}, venueCnt: $venueCnt")
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
                                    R.string.fail
                                )
                            )
                        )
                    }
                }
        }

    override fun getRatingByObject(
        id: String,
        isVenue: Boolean,
    ): MutableLiveData<List<RatingInfo>> {
        val liveData = MutableLiveData<List<RatingInfo>>()

        // get rating data
        db.collection(PATH_RATING)
            .whereEqualTo("isVenue", isVenue)
            .whereEqualTo("objectId", id)
            .addSnapshotListener { snapshot, exception ->

                exception?.let {
                    Logger.d("[${this::class.simpleName}] Error getting documents. ${it.message}")
                }

                val list = mutableListOf<RatingInfo>()
                for (document in snapshot!!) {

                    coroutineScope.launch {

                        val rating = document.toObject(RatingInfo::class.java)
                        rating.postTimestamp = rating.postDate?.let { Timestamp(it.time) }

                        // get user info
                        val result = getUsersResult(listOf(rating.userId))
                        if (result is Result.Success && !result.data.isNullOrEmpty()) {
                            rating.userInfo = result.data.get(0)
                        }

                        when (isVenue) {
                            // get object venue info
                            true -> {
                                val result = getVenueByIds(listOf(id))
                                if (result is Result.Success && !result.data.isNullOrEmpty()) {

                                    val venue = result.data
                                    rating.objectName = venue[0].name

                                    if (!venue[0].images.isNullOrEmpty()) {
                                        rating.objectImg = venue[0].images!![0]
                                    }

                                    list.add(rating)
                                    liveData.value = list
                                }
                            }
                            // get object drink info
                            false -> {
                                val result = getDrinksByIds(listOf(id))
                                if (result is Result.Success && !result.data.isNullOrEmpty()) {

                                    val drink = result.data
                                    rating.objectName = drink[0].name

                                    if (!drink[0].images.isNullOrEmpty()) {
                                        rating.objectImg = drink[0].images!![0]
                                    }

                                    list.add(rating)
                                    liveData.value = list
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

                        if (rtgTask.result.size() == 0) {
                            continuation.resume(Result.Success(list))
                        }

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
                                                if (!venue.images.isNullOrEmpty()) {
                                                    rating.objectImg =
                                                        venue.images!![0]
                                                }
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
                                                if (!drink.images.isNullOrEmpty()) {
                                                    rating.objectImg =
                                                        drink.images!![0]
                                                }
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
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(
                            Result.Fail(
                                BarUrSideApplication.instance.getString(
                                    R.string.fail
                                )
                            )
                        )
                    }
                }
        }

    override suspend fun postRating(rating: Rating): Result<Boolean> =
        suspendCoroutine { continuation ->
            val ratings = db.collection(PATH_RATING)
            val document = ratings.document()

            rating.id = document.id
            rating.postDate = Timestamp(System.currentTimeMillis())

            document.set(Rating.toHashMap(rating))
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {

                            Logger.w(
                                "[${this::class.simpleName}] Error getting documents. ${it.message}"
                            )
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(BarUrSideApplication.instance.getString(R.string.fail)))
                    }
                }
        }

    override suspend fun getCollect(userId: String): Result<List<Collect>> {
        return Collect().getListResult(
            db.collection(PATH_COLLECT).whereEqualTo("userId", userId).get()
        )
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
                            document.reference.delete()
                        }

                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(
                            Result.Fail(BarUrSideApplication.instance.getString(R.string.fail)
                            )
                        )
                    }
                }
        }


    override suspend fun uploadPhoto(
        storageRef: StorageReference,
        userId: String,
        uploadType: String,
        localImage: String,
    ): Result<String> =
        suspendCoroutine { continuation ->

            val file = Uri.fromFile(File(localImage))
            Log.d("Ming", "file: ${file.path}")
            val eventsRef = storageRef.child("$uploadType/$userId/${file.lastPathSegment}" ?: "")
            val uploadTask = eventsRef.putFile(file)
            uploadTask
                .addOnSuccessListener {

                    eventsRef.downloadUrl.addOnSuccessListener {
                        Logger.i("Success: $it")
                        continuation.resume(Result.Success(it.toString()))
                    }.addOnFailureListener { exception ->
                        Logger.i(exception.toString())
                    }
                }
                .addOnFailureListener { exception ->
                    Logger.i(exception.toString())
                }
            File(localImage).delete()
        }

    override suspend fun updateObjectRating(
        id: String,
        isVenue: Boolean,
        rating: Rating,
    ): Result<Boolean> =
        suspendCoroutine { continuation ->

            when (isVenue) {
                true -> {
                    db.collection(PATH_VENUE)
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
                                    (
                                            venue?.rtgCount?.times(venue?.avgRating)
                                                ?.plus(rating!!.rating!!)
                                            )?.div(venue?.rtgCount!!.plus(1))

                                db.collection(PATH_VENUE)
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
                                    Logger.w(
                                        "[${this::class.simpleName}] Error getting documents. ${it.message}"
                                    )
                                    continuation.resume(Result.Error(it))
                                    return@addOnCompleteListener
                                }
                                continuation.resume(
                                    Result.Fail(
                                        BarUrSideApplication.instance.getString(
                                            R.string.fail
                                        )
                                    )
                                )
                            }
                        }
                }
                false -> {
                    db.collection(PATH_DRINK)
                        .document(id)
                        .get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val drink = task.result.toObject(Venue::class.java)
                                val newRtg =
                                    (
                                            drink?.rtgCount?.times(drink?.avgRating)
                                                ?.plus(rating!!.rating!!)
                                            )?.div(drink?.rtgCount!!.plus(1))

                                db.collection(PATH_DRINK)
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
                                    Logger.w(
                                        "[${this::class.simpleName}] Error getting documents. ${it.message}"
                                    )
                                    continuation.resume(Result.Error(it))
                                    return@addOnCompleteListener
                                }
                                continuation.resume(
                                    Result.Fail(
                                        BarUrSideApplication.instance.getString(
                                            R.string.fail
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
        addShareImgCnt: Int,
    ): Result<Boolean> =
        suspendCoroutine { continuation ->

            db.collection(PATH_USER)
                .document(userId)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = task.result.toObject(User::class.java)

                        db
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
                            Logger.w(
                                "[${this::class.simpleName}] Error getting documents. ${it.message}"
                            )
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(
                            Result.Fail(
                                BarUrSideApplication.instance.getString(
                                    R.string.fail
                                )
                            )
                        )
                    }
                }
        }

    override suspend fun getVenueByLocation(
        minLat: Double, maxLat: Double,
        minLng: Double, maxLng: Double,
    ): Result<List<Venue>> {

        val ref = db.collection(PATH_VENUE)

        ref.whereGreaterThan("latitude", minLat).whereLessThan("latitude", maxLat)

        ref.whereGreaterThan("longitude", minLng).whereLessThan("longitude", maxLng)

        return Venue().getListResult(ref.get())
    }

    override suspend fun getVenueBySearch(search: String): Result<List<Venue>> {
        return Venue().getListResult(
            db.collection(PATH_VENUE).orderBy("name")
                .startAt(search.uppercase()).endAt("${search.lowercase()}\uf8ff").get()
        )
    }

    override fun getNotification(userId: String): MutableLiveData<List<Notification>> {
        val liveData = MutableLiveData<List<Notification>>()

        // notification from user
        db.collection(PATH_NOTIFICATION)
            .whereEqualTo("toId", userId)
            .orderBy("date", Query.Direction.DESCENDING)
            .limit(80)
            .addSnapshotListener { snapshot, exception ->

                exception?.let {
                    Logger.d("[${this::class.simpleName}] Error getting documents. ${it.message}")
                }
                val list = mutableListOf<Notification>()

                for (document in snapshot!!) {
                    val notification = document.toObject(Notification::class.java)
                    notification.timestamp = notification.date?.let { Timestamp(it.time) }
                    list.add(notification)
                }

                // notification send user
                db.collection(PATH_NOTIFICATION)
                    .whereEqualTo("fromId", userId)
                    .orderBy("date", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, exception ->
                        exception?.let {
                            Logger.d(
                                "[${this::class.simpleName}] Error getting documents. ${it.message}"
                            )
                        }

                        for (document in snapshot!!) {
                            val notification = document.toObject(Notification::class.java)
                            notification.timestamp = notification.date?.let { Timestamp(it.time) }
                            list.add(notification)
                        }
                        liveData.value = list
                        liveData.value = liveData.value
                    }
            }

        return liveData
    }

    override suspend fun getVenueByIds(ids: List<String>): Result<List<Venue>> {
        return Venue().getListResult(
            db.collection(PATH_VENUE).whereIn("id", ids).get()
        )
    }

    override suspend fun getDrinksByIds(ids: List<String>): Result<List<Drink>> {
        return Drink().getListResult(
            db.collection(PATH_DRINK).whereIn("id", ids).get()
        )
    }

    override suspend fun getDrinkBySearch(search: String): Result<List<Drink>> {
        return Drink().getListResult(
            db.collection(PATH_DRINK).orderBy("name")
                .startAt(search.uppercase()).endAt("${search.lowercase()}\uf8ff").get()
        )
    }

    override suspend fun getHotVenueResult(): Result<List<Venue>> =
        suspendCoroutine { continuation ->

            val list = mutableListOf<Venue>()

            // get date
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MONTH, -1)

            // filter rating
            db.collection(PATH_RATING)
                .whereEqualTo("isVenue", true)
                .whereGreaterThan("postDate", calendar.time)
                .get()
                .addOnCompleteListener { venueTask ->

                    if (venueTask.isSuccessful) {

                        val venueRtgs = venueTask.result.toObjects(Rating::class.java)
                        val hotVenueList = venueRtgs.groupingBy { it.objectId }.eachCount().toList()
                            .sortedByDescending { it.second }.take(10)

                        if (!hotVenueList.isNullOrEmpty()) {

                            coroutineScope.launch {
                                val result = Venue().getListResult(
                                    db.collection(PATH_VENUE)
                                        .whereIn("id", hotVenueList.map { it.first }).get()
                                )

                                continuation.resume(result)
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
                                    R.string.fail
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
            db.collection(PATH_RATING)
                .whereEqualTo("isVenue", false)
                .whereGreaterThan("postDate", calendar.time)
                .get()
                .addOnCompleteListener { drinkTask ->

                    if (drinkTask.isSuccessful) {

                        val drinkRtgs = drinkTask.result.toObjects(Rating::class.java)
                        val hotDrinkList = drinkRtgs.groupingBy { it.objectId }.eachCount().toList()
                            .sortedByDescending { it.second }.take(10)

                        if (!hotDrinkList.isNullOrEmpty()) {
                            // filter drink
                            db
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
                        }
                    } else {
                        drinkTask.exception?.let {
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(
                            Result.Fail(
                                BarUrSideApplication.instance.getString(
                                    R.string.fail
                                )
                            )
                        )
                    }
                }
        }

    override suspend fun getHighRateVenueResult(): Result<List<Venue>> {
        return Venue().getListResult(
            db.collection(PATH_VENUE)
                .orderBy("avgRating", Query.Direction.DESCENDING)
                .orderBy("rtgCount", Query.Direction.DESCENDING).limit(10).get()
        )
    }

    override suspend fun getHighRateDrinkResult(): Result<List<Drink>> {
        return Drink().getListResult(
            db.collection(PATH_DRINK)
                .orderBy("avgRating", Query.Direction.DESCENDING)
                .orderBy("rtgCount", Query.Direction.DESCENDING).limit(10).get()
        )
    }

    override suspend fun getActivityByUser(userId: String): Result<List<Activity>> =
        suspendCoroutine { continuation ->
            val list = mutableListOf<Activity>()

            // filter activity
            db.collection(PATH_ACTIVITY)
                .get()
                .addOnCompleteListener { activityTask ->
                    if (activityTask.isSuccessful) {
                        for (document in activityTask.result!!) {
                            val activity = document.toObject(Activity::class.java)
                            activity.bookers?.let { bookers ->
                                if (bookers.any { it.id == userId }) {
                                    activity.startTimestamp =
                                        activity.startTime?.let { Timestamp(it.time) }
                                    activity.endTimestamp =
                                        activity.endTime?.let { Timestamp(it.time) }
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
                            Result.Fail(BarUrSideApplication.instance.getString(R.string.fail)
                            )
                        )
                    }
                }
        }

    override suspend fun getActivityById(activityId: String): Result<Activity?> {
        return Activity().getResult(
            db.collection(PATH_ACTIVITY).whereEqualTo("id", activityId).get()
        )
    }

    override suspend fun getRatingByRecommend(): Result<List<RatingInfo>> =
        suspendCoroutine { continuation ->
            val list = mutableListOf<RatingInfo>()
            var check = 0

            // get recommend user data
            db.collection(PATH_USER)
                .orderBy("shareCount", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener { userTask ->

                    if (userTask.isSuccessful) {
                        for (docUser in userTask.result!!) {
                            val user = docUser.toObject(User::class.java)

                            // get rating data
                            db.collection(PATH_RATING)
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
                                        db.collection(PATH_USER)
                                            .whereEqualTo("id", rtg.userId)
                                            .get()
                                            .addOnCompleteListener { task ->
                                                for (document in task.result!!) {
                                                    val user = document.toObject(User::class.java)
                                                    rtg.userInfo = user

                                                    // get venue Info
                                                    db.collection(PATH_VENUE)
                                                        .whereEqualTo("id", rtg.objectId)
                                                        .limit(1)
                                                        .get()
                                                        .addOnCompleteListener { venueTask ->
                                                            check += 1
                                                            for (docVenue in venueTask.result!!) {
                                                                val venue =
                                                                    docVenue.toObject(Venue::class.java)
                                                                rtg.objectName = venue.name

                                                                if (!venue.images.isNullOrEmpty()) {
                                                                    rtg.objectImg =
                                                                        venue.images!![0]
                                                                }
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
                                    R.string.fail
                                )
                            )
                        )
                    }
                }
        }

    override suspend fun getRatingByFriends(userId: String): Result<List<RatingInfo>> =
        suspendCoroutine { continuation ->
            var list = mutableListOf<RatingInfo>()
            var check = 0

            // get friend list
            db.collection(PATH_USER)
                .whereEqualTo("id", userId)
                .limit(10)
                .get()
                .addOnCompleteListener { userTask ->
                    if (userTask.isSuccessful) {

                        for (document in userTask.result!!) {
                            val friends = document.toObject(User::class.java).friends?.map { it.id }

                            if (!friends.isNullOrEmpty()) {
                                db.collection(PATH_RATING)
                                    .whereIn("userId", friends)
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
                                                db.collection(PATH_USER)
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
                                                                    db.collection(PATH_VENUE)
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
                                                                                if (!venue.images.isNullOrEmpty()) {
                                                                                    rtg.objectImg =
                                                                                        venue.images!![0]
                                                                                }
                                                                            }

                                                                            list.add(rtg)

                                                                            if (check == rtgTask.result.size()) {
                                                                                continuation.resume(
                                                                                    Result.Success(
                                                                                        list.sortedByDescending { it.postDate }
                                                                                    )
                                                                                )
                                                                            }
                                                                        }
                                                                }

                                                                // get object info : drink
                                                                false -> {
                                                                    db.collection(PATH_DRINK)
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
                                                                                if (!drink.images.isNullOrEmpty()) {
                                                                                    rtg.objectImg =
                                                                                        drink.images!![0]
                                                                                }
                                                                            }
                                                                            list.add(rtg)
                                                                            if (check == rtgTask.result.size()) {
                                                                                continuation.resume(
                                                                                    Result.Success(
                                                                                        list.sortedByDescending { it.postDate }
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
                            } else {
                                continuation.resume(
                                    Result.Success(list.sortedByDescending { it.postDate })
                                )
                            }
                        }
                    } else {
                        userTask.exception?.let {
                            Logger.w(
                                "[${this::class.simpleName}] Error getting documents. ${it.message}"
                            )
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(
                            Result.Fail(
                                BarUrSideApplication.instance.getString(
                                    R.string.fail
                                )
                            )
                        )
                    }
                }
        }

    override suspend fun addCollect(collect: Collect): Result<Boolean> {
        val document = db.collection(PATH_COLLECT).document()
        collect.id = document.id

        return document.set(Collect.toHashMap(collect)).taskSuccessReturn(true)
    }

    override suspend fun addVenue(venue: Venue): Result<Boolean> {
        val document = db.collection(PATH_VENUE).document()
        venue.id = document.id
        venue.menuId = document.id

        return document.set(Venue.toHashMap(venue)).taskSuccessReturn(true)
    }

    override suspend fun addDrink(drink: Drink): Result<Boolean> {
        val document = db.collection(PATH_DRINK).document()
        drink.id = document.id

        return document.set(Drink.toHashMap(drink)).taskSuccessReturn(true)
    }


    override suspend fun postActivity(
        activity: Activity,
        notification: Notification,
    ): Result<Boolean> = suspendCoroutine { continuation ->

        val postActivity = db.collection(PATH_ACTIVITY)
        val docActivity = postActivity.document()
        activity.id = docActivity.id
        notification.fromId = docActivity.id

        // add activity info to firebase
        docActivity
            .set(Activity.toHashMap(activity))
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    // add notification info to firebase
                    val postNotify =
                        db.collection(PATH_NOTIFICATION)
                    val docNotify = postNotify.document()
                    notification.id = docNotify.id
                    docNotify.set(Notification.toHashMap(notification))
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                continuation.resume(Result.Success(true))
                            } else {
                                task.exception?.let {
                                    Logger.d("Error getting documents. ${it.message}")
                                    continuation.resume(Result.Error(it))
                                    return@addOnCompleteListener
                                }
                                continuation.resume(
                                    Result.Fail(
                                        BarUrSideApplication.instance
                                            .getString(
                                                R.string.fail
                                            )
                                    )
                                )
                            }
                        }
                } else {
                    task.exception?.let {
                        Logger.d("Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(
                        Result.Fail(
                            BarUrSideApplication.instance.getString(
                                R.string.fail
                            )
                        )
                    )
                }
            }
    }

    override suspend fun addFriend(notification: Notification): Result<Boolean> {
        val document = db.collection(PATH_NOTIFICATION).document()
        notification.id = document.id

        return document.set(Notification.toHashMap(notification)).taskSuccessReturn(true)
    }

    override suspend fun modifyActivity(activityId: String, userId: String): Result<Boolean> =
        suspendCoroutine { continuation ->

            db.collection(PATH_ACTIVITY)
                .whereEqualTo("id", activityId)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            val activity = document.toObject(Activity::class.java)

                            if (activity.sponsor == userId) {
                                document.reference.delete()

                                // modify notification for booker
                                db.collection(PATH_NOTIFICATION)
                                    .whereEqualTo("fromId", activityId)
                                    .get()
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            for (document in task.result!!) {
                                                document.reference
                                                    .update(
                                                        mapOf(
                                                            "content" to "活動：<b>${activity.name}</b> 發起者已取消該活動",
                                                            "type" to "activity_cancel",
                                                            "isCheck" to false,
                                                            "date" to Timestamp(System.currentTimeMillis())
                                                        )
                                                    )
                                            }
                                            continuation.resume(Result.Success(true))
                                        }
                                    }
                            } else {
                                document.reference
                                    .update("bookers", activity.bookers?.filter { it.id != userId })
                                    .addOnSuccessListener {
                                        Logger.d("DocumentSnapshot successfully updated!")
                                    }
                                    .addOnFailureListener { e ->
                                        Logger.d("Error updating document: $e")
                                    }

                                // delete notification for user
                                db.collection(PATH_NOTIFICATION)
                                    .whereEqualTo("fromId", activityId)
                                    .whereEqualTo("toId", userId)
                                    .get()
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            for (document in task.result!!) {
                                                document.reference.delete()
                                            }
                                        }
                                        continuation.resume(Result.Success(true))
                                    }
                            }
                        }
                    } else {
                        task.exception?.let {
                            Logger.d("Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(
                            Result.Fail(
                                BarUrSideApplication.instance.getString(
                                    R.string.fail
                                )
                            )
                        )
                    }
                }
        }

    override suspend fun bookActivity(
        activityId: String, userId: String, notification: Notification,
    ): Result<Boolean> = suspendCoroutine { continuation ->

        // book to activity
        db.collection(PATH_ACTIVITY).whereEqualTo("id", activityId).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {

                        val activity = document.toObject(Activity::class.java)
                        val docNotify = db.collection(PATH_NOTIFICATION).document()
                        notification.id = docNotify.id

                        coroutineScope.launch {

                            // update activity bookers
                            document.reference.update("bookers", activity.bookers?.plus(
                                Relationship(userId, Timestamp(System.currentTimeMillis()))
                            )).taskSuccessReturn(true)

                            // add activity notification to new booker
                            docNotify.set(Notification.toHashMap(notification))
                                .taskSuccessReturn(true)
                        }
                    }
                } else {
                    task.exception?.let {
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(
                        Result.Fail(BarUrSideApplication.instance.getString(R.string.fail))
                    )
                }
            }
    }

    override suspend fun addUser(user: User): Result<Boolean> = suspendCoroutine { continuation ->

        // check user is not in collection, then add
        db.collection(PATH_USER)
            .whereEqualTo("id", user.id)
            .get()
            .addOnCompleteListener { userTask ->

                if (userTask.isSuccessful && userTask.result.isEmpty) {
                    coroutineScope.launch {
                        db.collection(PATH_USER).document(user.id).set(User.toHashMap(user))
                            .taskSuccessReturn(true)
                    }
                } else {
                    continuation.resume(Result.Success(true))
                }
            }
    }

    override suspend fun replyAddFriend(notify: Notification, reply: Boolean): Result<Boolean> =
        suspendCoroutine { continuation ->

            coroutineScope.launch {
                val result = db.collection(PATH_NOTIFICATION).document(notify.id)
                    .set(Notification.toHashMap(notify)).taskSuccessReturn(true)

                if (reply) {
                    db.collection(PATH_USER).document(notify.fromId).update(
                        "friends", FieldValue.arrayUnion(
                            Relationship(
                                notify.toId,
                                Timestamp(System.currentTimeMillis())
                            ))).taskSuccessReturn(true)

                    db.collection(PATH_USER).document(notify.toId).update(
                        "friends", FieldValue.arrayUnion(
                            Relationship(
                                notify.fromId,
                                Timestamp(System.currentTimeMillis())
                            ))).taskSuccessReturn(true)
                }
                continuation.resume(Result.Success(true))
            }
        }

    override suspend fun unfriend(ids: List<String>): Result<Boolean> =
        suspendCoroutine { continuation ->

            db.collection(PATH_USER).whereEqualTo("id", ids[0]).get()
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        for (document in task.result!!) {
                            val user = document.toObject(User::class.java)
                            coroutineScope.launch {
                                document.reference.update("friends",
                                    user.friends?.filter { it.id != ids[1] } ?: user.friends
                                ).taskSuccessReturn(true)
                            }
                        }
                    }

                    db.collection(PATH_USER).whereEqualTo("id", ids[1]).get()
                        .addOnCompleteListener { task ->

                            if (task.isSuccessful) {

                                for (document in task.result!!) {
                                    val user = document.toObject(User::class.java)
                                    coroutineScope.launch {
                                        document.reference.update("friends",
                                            user.friends?.filter { it.id != ids[0] } ?: user.friends
                                        ).taskSuccessReturn(true)
                                    }
                                }
                            }

                            continuation.resume(Result.Success(true))
                        }
                }
        }

    override suspend fun checkNotification(ids: List<String>): Result<Boolean> =
        suspendCoroutine { continuation ->

            for (limitIds in ids.windowed(10, step = 10, partialWindows = true)) {
                db.collection(PATH_NOTIFICATION)
                    .whereIn("id", limitIds)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (document in task.result!!) {
                                val notification = document.toObject(Notification::class.java)

                                if (notification.isCheck == false) {
                                    if (notification.type == "activity") {
                                        document.reference.update(
                                            mapOf(
                                                "isCheck" to true,
                                                "date" to Timestamp(System.currentTimeMillis())
                                            )
                                        )
                                            .addOnSuccessListener {
                                                Logger.d("add successfully updated!")
                                            }
                                            .addOnFailureListener { e ->
                                                Logger.d("error updated!")
                                            }
                                    } else {
                                        document.reference.update("isCheck", true)
                                            .addOnSuccessListener {
                                                Logger.d("add successfully updated!")
                                            }
                                            .addOnFailureListener { e ->
                                                Logger.d("error updated!")
                                            }
                                    }
                                }
                            }
                            continuation.resume(Result.Success(true))
                        } else {
                            task.exception?.let {
                                Logger.w(
                                    "[${this::class.simpleName}] Error getting documents. ${it.message}"
                                )
                                continuation.resume(Result.Error(it))
                                return@addOnCompleteListener
                            }
                            continuation.resume(
                                Result.Fail(
                                    BarUrSideApplication.instance.getString(
                                        R.string.fail
                                    )
                                )
                            )
                        }
                    }
            }
        }

    override fun getNotificationChange(userId: String): MutableLiveData<List<Notification>> {
        return Notification().getLiveDataListResult(
            db.collection(PATH_NOTIFICATION)
                .whereEqualTo("toId", userId).whereEqualTo("isCheck", false)
        )
    }
}
