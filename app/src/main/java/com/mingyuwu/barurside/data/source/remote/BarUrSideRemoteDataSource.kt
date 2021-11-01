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

    override suspend fun getFriend(user: User): Result<List<User>> =
        suspendCoroutine { continuation ->
            val list = mutableListOf<User>()

            FirebaseFirestore.getInstance()
                .collection(PATH_USER)
                .whereIn("id", user.friends!!.map { it.id })
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


    override fun getRating(id: String, isVenue: Boolean): MutableLiveData<List<RatingInfo>> {
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
                        Log.d("Ming", "updateUserShare!!!!!!")
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

    override suspend fun getHotVenueResult(): Result<List<Venue>> =
        suspendCoroutine { continuation ->

            val list = mutableListOf<Venue>()

            // get date
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MONTH, -1)

            // filter rating
            FirebaseFirestore.getInstance()
                .collection(PATH_RATING)
                .whereEqualTo("isVenue",true)
                .whereGreaterThan("postDate", calendar.time)
                .get()
                .addOnCompleteListener { venueTask ->

                    if (venueTask.isSuccessful) {

                        val venueRtgs = venueTask.result.toObjects(Venue::class.java)
                        val hotVenueList = venueRtgs.groupingBy { it.id }.eachCount().toList()
                            .sortedByDescending { it.second }.take(10)

                        // filter venue
                        FirebaseFirestore.getInstance()
                            .collection(PATH_VENUE)
                            .whereIn("id", hotVenueList)
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
                .whereEqualTo("isVenue",false)
                .whereGreaterThan("postDate", calendar.time)
                .get()
                .addOnCompleteListener { venueTask ->

                    if (venueTask.isSuccessful) {

                        val venueRtgs = venueTask.result.toObjects(Venue::class.java)
                        val hotVenueList = venueRtgs.groupingBy { it.id }.eachCount().toList()
                            .sortedByDescending { it.second }.take(10)

                        // filter drink
                        FirebaseFirestore.getInstance()
                            .collection(PATH_VENUE)
                            .whereIn("id", hotVenueList)
                            .get()
                            .addOnCompleteListener { task ->
                                for (document in task.result!!) {
                                    val drink = document.toObject(Drink::class.java)
                                    list.add(drink)
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
}