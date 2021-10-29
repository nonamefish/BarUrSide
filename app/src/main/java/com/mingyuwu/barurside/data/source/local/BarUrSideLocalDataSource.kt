package com.mingyuwu.barurside.data.source.local

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.firebase.storage.StorageReference
import com.mingyuwu.barurside.data.Drink
import com.mingyuwu.barurside.data.Rating
import com.mingyuwu.barurside.data.Result
import com.mingyuwu.barurside.data.User
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.data.source.BarUrSideDataSource
import com.mingyuwu.barurside.filter.FilterParameter

class BarUrSideLocalDataSource(val context: Context) : BarUrSideDataSource {
    override fun getVenue(id: String): MutableLiveData<Venue> {
        TODO("Not yet implemented")
    }

    override fun getRating(id: String, isVenue: Boolean): MutableLiveData<List<Rating>> {
        TODO("Not yet implemented")
    }

    override fun getDrink(id: String): MutableLiveData<Drink> {
        TODO("Not yet implemented")
    }

    override fun getUser(id: String): MutableLiveData<User> {
        TODO("Not yet implemented")
    }

    override suspend fun postRating(rating: Rating): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getVenueByFilter(filter: FilterParameter): Result<List<Venue>> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadPhoto(
        storageRef: StorageReference,
        userId: String,
        type: String,
        localImage: String
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun getFriend(user: User): Result<List<User>> {
        TODO("Not yet implemented")
    }

    override suspend fun getMenu(venueId: String): Result<List<Drink>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateObjectRating(
        id: String,
        isVenue: Boolean,
        rating: Rating
    ): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUserShare(
        userId: String,
        addShareCnt: Int,
        addShareImgCnt: Int
    ): Result<Boolean> {
        TODO("Not yet implemented")
    }
}