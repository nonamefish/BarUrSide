package com.mingyuwu.barurside.data.source

import androidx.lifecycle.MutableLiveData
import com.google.firebase.storage.StorageReference
import com.mingyuwu.barurside.data.*
import com.mingyuwu.barurside.filter.FilterParameter

class DefaultBarUrSideRepository(
    private val remoteDataSource: BarUrSideDataSource,
    private val localDataSource: BarUrSideDataSource
) : BarUrSideRepository {

    override fun getVenue(id: String): MutableLiveData<Venue> {
        return remoteDataSource.getVenue(id)
    }

    override fun getRatingByObject(id: String, isVenue: Boolean): MutableLiveData<List<RatingInfo>> {
        return remoteDataSource.getRatingByObject(id, isVenue)
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

    override suspend fun uploadPhoto(
        storageRef: StorageReference,
        userId: String,
        type: String,
        localImage: String
    ): Result<String> {
        return remoteDataSource.uploadPhoto(storageRef, userId, type, localImage)
    }

    override suspend fun getFriend(frds: List<String>): Result<List<User>> {
        return remoteDataSource.getFriend(frds)
    }

    override suspend fun getMenu(venueId: String): Result<List<Drink>> {
        return remoteDataSource.getMenu(venueId)
    }

    override suspend fun updateRating(
        id: String,
        isVenue: Boolean,
        rating: Rating
    ): Result<Boolean> {
        return remoteDataSource.updateObjectRating(id, isVenue, rating)
    }

    override suspend fun updateUserShare(
        userId: String,
        addShareCnt: Int,
        addShareImgCnt: Int
    ): Result<Boolean> {
        return remoteDataSource.updateUserShare(userId, addShareCnt, addShareImgCnt)
    }

    override suspend fun getVenueByFilter(filter: FilterParameter): Result<List<Venue>> {
        return remoteDataSource.getVenueByFilter(filter)
    }

    override suspend fun getVenueByLocation(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double
    ): Result<List<Venue>> {
        return remoteDataSource.getVenueByLocation(minLat, maxLat, minLng, maxLng)
    }

    override suspend fun getDrinkByRating(id: String): Result<Drink> {
        return remoteDataSource.getDrinkByRating(id)
    }

    override suspend fun getVenueBySearch(search: String): Result<List<Venue>> {
        return remoteDataSource.getVenueBySearch(search)
    }

    override suspend fun getHotVenueResult(): Result<List<Venue>> {
        return remoteDataSource.getHotVenueResult()
    }

    override suspend fun getHotDrinkResult(): Result<List<Drink>> {
        return remoteDataSource.getHotDrinkResult()
    }

    override suspend fun getHighRateVenueResult(): Result<List<Venue>> {
        return remoteDataSource.getHighRateVenueResult()
    }

    override suspend fun getHighRateDrinkResult(): Result<List<Drink>> {
        return remoteDataSource.getHighRateDrinkResult()
    }

    override fun getActivityResult(): MutableLiveData<List<Activity>> {
        return remoteDataSource.getActivityResult()
    }

    override suspend fun getDrinkBySearch(search: String): Result<List<Drink>> {
        return remoteDataSource.getDrinkBySearch(search)
    }

    override suspend fun postCollect(collect: Collect): Result<Boolean>{
        return remoteDataSource.postCollect(collect)
    }

    override suspend fun getCollect(userId: String): Result<List<Collect>> {
        return remoteDataSource.getCollect(userId)
    }

    override suspend fun removeCollect(id: String, userId: String): Result<Boolean> {
        return remoteDataSource.removeCollect(id, userId)
    }

    override suspend fun getVenueByIds(ids: List<String>): Result<List<Venue>> {
        return remoteDataSource.getVenueByIds(ids)
    }

    override suspend fun getDrinksByIds(ids: List<String>): Result<List<Drink>> {
        return remoteDataSource.getDrinksByIds(ids)
    }

    override suspend fun getRatingByUser(userId: String): Result<List<RatingInfo>> {
        return remoteDataSource.getRatingByUser(userId)
    }

    override suspend fun getActivityByUser(userId: String): Result<List<Activity>> {
        return remoteDataSource.getActivityByUser(userId)
    }

    override suspend fun getRatingByRecommend(): Result<List<RatingInfo>> {
        return remoteDataSource.getRatingByRecommend()
    }

    override suspend fun getRatingByFriends(userId: String): Result<List<RatingInfo>> {
        return remoteDataSource.getRatingByFriends(userId)
    }

    override suspend fun postActivity(activity: Activity): Result<Boolean> {
        return remoteDataSource.postActivity(activity)
    }

    override suspend fun modifyActivity(activityId: String, userId: String): Result<Boolean> {
        return remoteDataSource.modifyActivity(activityId,userId)
    }

    override suspend fun bookActivity(activityId: String, userId: String): Result<Boolean> {
        return remoteDataSource.bookActivity(activityId, userId)
    }

    override suspend fun addUser(user: User): Result<Boolean> {
        return remoteDataSource.addUser(user)
    }

    override suspend fun addFriend(notification: Notification): Result<Boolean> {
        return remoteDataSource.addFriend(notification)
    }

    override fun getNotification(userId: String): MutableLiveData<List<Notification>> {
        return remoteDataSource.getNotification(userId)
    }
}