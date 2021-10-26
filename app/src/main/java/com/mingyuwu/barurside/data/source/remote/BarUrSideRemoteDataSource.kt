package com.mingyuwu.barurside.data.source.remote

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.data.Rating
import com.mingyuwu.barurside.data.source.BarUrSideDataSource


const val TAG = "RemoteDataSource"

object BarUrSideRemoteDataSource : BarUrSideDataSource {
    private const val VENUE_ARTICLES = "venue"
    private const val RATING_ARTICLES = "rating"

    override fun getVenue(id: String): MutableLiveData<Venue> {
        val liveData = MutableLiveData<Venue>()

        FirebaseFirestore.getInstance()
            .collection(VENUE_ARTICLES)
            .whereEqualTo("id", id)
            .addSnapshotListener { snapshot, exception ->
                Log.d(TAG, "snapshot ${snapshot!!.documents}")
                exception?.let {
                    Log.d(TAG, "[${this::class.simpleName}] Error getting documents. ${it.message}")
                }

                for (document in snapshot!!) {
                    val venue = document.toObject(Venue::class.java)
                    liveData.value = venue
                    Log.d(TAG, "=========================================")
                    Log.d(TAG, "venue id: $id")
                    Log.d(TAG, "venue info: ${liveData.value}")
                    Log.d(TAG, "=========================================")
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
                Log.d(TAG, "snapshot ${snapshot!!.documents}")
                exception?.let {
                    Log.d(TAG, "[${this::class.simpleName}] Error getting documents. ${it.message}")
                }

                val list = mutableListOf<Rating>()
                for (document in snapshot!!) {
                    val rating = document.toObject(Rating::class.java)
                    list.add(rating)
                    Log.d(TAG, "=========================================")
                    Log.d(TAG, "rating id: $id")
                    Log.d(TAG, "rating value: ${liveData.value}")
                    Log.d(TAG, "=========================================")
                }
                liveData.value = list
            }

        return liveData
    }
}