package com.mingyuwu.barurside.data.source.remote

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.mingyuwu.barurside.BarUrSideApplication
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.data.source.BarUrSideDataSource
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.mingyuwu.barurside.data.Result

const val TAG = "RemoteDataSource"

object BarUrSideRemoteDataSource : BarUrSideDataSource {
    private const val VENUE_ARTICLES = "venue"

    override suspend fun getVenue(id: String): MutableLiveData<Venue> {
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
                    liveData.value = document.toObject(Venue::class.java)
                    Log.d(TAG, "liveData.value ${liveData.value}")
                }

            }
        return liveData
    }
}