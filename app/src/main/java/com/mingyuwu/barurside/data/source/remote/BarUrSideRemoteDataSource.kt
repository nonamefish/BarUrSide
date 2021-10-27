package com.mingyuwu.barurside.data.source.remote

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.mingyuwu.barurside.BarUrSideApplication
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.data.Drink
import com.mingyuwu.barurside.data.User
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.data.Rating
import com.mingyuwu.barurside.data.Result
import com.mingyuwu.barurside.data.source.BarUrSideDataSource
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
            for (friend in user.friends!!) {
                FirebaseFirestore.getInstance()
                    .collection(PATH_USER)
                    .whereEqualTo("id", friend.id)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (document in task.result!!) {
                                Log.d(TAG, document.id + " => " + document.data)

                                val friend = document.toObject(User::class.java)
                                list.add(friend)
                            }
                            if (list.size == user.friends.size) {
                                continuation.resume(Result.Success(list))
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


    override fun getRating(id: String, isVenue: Boolean): MutableLiveData<List<Rating>> {
        val liveData = MutableLiveData<List<Rating>>()

        FirebaseFirestore.getInstance()
            .collection(PATH_RATING)
            .whereEqualTo("isVenue", isVenue)
            .whereEqualTo("objectId", id)
            .addSnapshotListener { snapshot, exception ->
//                Log.d(TAG, "getRating snapshot ${snapshot!!.documents}")
                exception?.let {
                    Log.d(TAG, "[${this::class.simpleName}] Error getting documents. ${it.message}")
                }

                val list = mutableListOf<Rating>()
                for (document in snapshot!!) {
                    val rating = document.toObject(Rating::class.java)
                    list.add(rating)
                    Log.d(TAG, "=========================================")
                    Log.d(TAG, "rating isVenue: $isVenue")
                    Log.d(TAG, "rating objectId: $id")
                    Log.d(TAG, "rating value: ${rating}")
                    Log.d(TAG, "=========================================")
                }
                liveData.value = list
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
//                        Log.d(TAG, "Rating: $rating")

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
        uploadType: String,
        localImage: String
    ) {
        val file = Uri.fromFile(File(localImage))
        val eventsRef = storageRef.child("$uploadType/${file.lastPathSegment}" ?: "")
        val uploadTask = eventsRef.putFile(file)
        uploadTask
            .addOnSuccessListener {
                Log.i(TAG, "Success")
                Log.i(TAG, "eventsRef: $eventsRef")
            }
            .addOnFailureListener { exception ->
                Log.i(TAG, exception.toString())
            }
    }
}