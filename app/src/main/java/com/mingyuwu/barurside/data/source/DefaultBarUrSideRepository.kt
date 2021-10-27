package com.mingyuwu.barurside.data.source

import androidx.lifecycle.MutableLiveData
import com.google.firebase.storage.StorageReference
import com.mingyuwu.barurside.data.Drink
import com.mingyuwu.barurside.data.Rating
import com.mingyuwu.barurside.data.User
import com.mingyuwu.barurside.data.Result
import com.mingyuwu.barurside.data.Venue

class DefaultBarUrSideRepository(
    private val remoteDataSource: BarUrSideDataSource,
    private val localDataSource: BarUrSideDataSource
) : BarUrSideRepository {

    override fun getVenue(id: String): MutableLiveData<Venue> {
        return remoteDataSource.getVenue(id)
    }

    override fun getRating(id: String, isVenue: Boolean): MutableLiveData<List<Rating>> {
        return remoteDataSource.getRating(id, isVenue)
    }

    override fun getDrink(id: String): MutableLiveData<Drink> {
        return remoteDataSource.getDrink(id)
    }

    override fun getUser(id: String): MutableLiveData<User> {
        return remoteDataSource.getUser(id)
    }

    override suspend fun postRating(rating: Rating): Result<Boolean> {
        return remoteDataSource.postRating(rating)
    }

    override suspend fun uploadPhoto(storageRef: StorageReference, type: String, localImage: String) {
        return remoteDataSource.uploadPhoto(storageRef, type, localImage)
    }

    override suspend fun getFriend(user: User): Result<List<User>> {
        return remoteDataSource.getFriend(user)
    }

    override suspend fun getMenu(venueId: String): Result<List<Drink>> {
        return remoteDataSource.getMenu(venueId)
    }
}