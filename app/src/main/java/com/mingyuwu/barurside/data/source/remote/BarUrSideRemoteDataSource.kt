package com.mingyuwu.barurside.data.source.remote

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.mingyuwu.barurside.data.Drink
import com.mingyuwu.barurside.data.User
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.data.Rating
import com.mingyuwu.barurside.data.source.BarUrSideDataSource


const val TAG = "RemoteDataSource"

object BarUrSideRemoteDataSource : BarUrSideDataSource {
    private const val VENUE_ARTICLES = "venue"
    private const val DRINK_ARTICLES = "drink"
    private const val RATING_ARTICLES = "rating"

    override fun getVenue(id: String): MutableLiveData<Venue> {
        val liveData = MutableLiveData<Venue>()

        FirebaseFirestore.getInstance()
            .collection(VENUE_ARTICLES)
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
            .collection(DRINK_ARTICLES)
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
            .collection(DRINK_ARTICLES)
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


    override fun getRating(id: String, isVenue: Boolean): MutableLiveData<List<Rating>> {
        val liveData = MutableLiveData<List<Rating>>()

        FirebaseFirestore.getInstance()
            .collection(RATING_ARTICLES)
            .whereEqualTo("isVenue", isVenue)
            .whereEqualTo("objectId", id)
            .addSnapshotListener { snapshot, exception ->
                Log.d(TAG, "getRating snapshot ${snapshot!!.documents}")
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
}