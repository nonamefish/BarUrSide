package com.mingyuwu.barurside.data.source.local

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.firebase.storage.StorageReference
import com.mingyuwu.barurside.data.*
import com.mingyuwu.barurside.data.source.BarUrSideDataSource
import com.mingyuwu.barurside.filter.FilterParameter

class BarUrSideLocalDataSource(val context: Context) : BarUrSideDataSource {
    override fun getVenue(id: String): MutableLiveData<Venue> {
        TODO("Not yet implemented")
    }

    override fun getRatingByObject(
        id: String,
        isVenue: Boolean
    ): MutableLiveData<List<RatingInfo>> {
        TODO("Not yet implemented")
    }

    override suspend fun getVenueBySearch(search: String): Result<List<Venue>> {
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
    ): Result<String> {
        TODO("Not yet implemented")
    }

    override suspend fun getHotVenueResult(): Result<List<Venue>> {
        TODO("Not yet implemented")
    }

    override fun getActivityResult(): MutableLiveData<List<Activity>> {
        TODO("Not yet implemented")
    }

    override suspend fun getHotDrinkResult(): Result<List<Drink>> {
        TODO("Not yet implemented")
    }

    override suspend fun getHighRateVenueResult(): Result<List<Venue>> {
        TODO("Not yet implemented")
    }

    override suspend fun getHighRateDrinkResult(): Result<List<Drink>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUsersResult(frds: List<String>): Result<List<User>> {
        TODO("Not yet implemented")
    }

    override suspend fun getMenu(venueId: String): Result<List<Drink>> {
        TODO("Not yet implemented")
    }

    override suspend fun getVenueByLocation(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double
    ): Result<List<Venue>> {
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

    override suspend fun addCollect(collect: Collect): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getDrinkBySearch(search: String): Result<List<Drink>> {
        TODO("Not yet implemented")
    }

    override suspend fun getCollect(userId: String): Result<List<Collect>> {
        TODO("Not yet implemented")
    }

    override suspend fun removeCollect(id: String, userId: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getVenueByIds(ids: List<String>): Result<List<Venue>> {
        TODO("Not yet implemented")
    }

    override suspend fun getDrinksByIds(ids: List<String>): Result<List<Drink>> {
        TODO("Not yet implemented")
    }

    override suspend fun getRatingByUser(userId: String): Result<List<RatingInfo>> {
        TODO("Not yet implemented")
    }

    override suspend fun getActivityByUser(userId: String): Result<List<Activity>> {
        TODO("Not yet implemented")
    }

    override suspend fun getRatingByRecommend(): Result<List<RatingInfo>> {
        TODO("Not yet implemented")
    }

    override suspend fun getRatingByFriends(userId: String): Result<List<RatingInfo>> {
        TODO("Not yet implemented")
    }

    override suspend fun postActivity(
        activity: Activity,
        notification: Notification
    ): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun modifyActivity(activityId: String, userId: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun bookActivity(
        activityId: String,
        userId: String,
        notification: Notification
    ): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun addUser(user: User): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun addFriend(notification: Notification): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getNotification(userId: String): MutableLiveData<List<Notification>> {
        TODO("Not yet implemented")
    }

    override suspend fun replyAddFriend(notify: Notification, reply: Boolean): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun unfriend(ids: List<String>): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun addDrink(drink: Drink): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun addVenue(venue: Venue): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun checkNotification(ids: List<String>): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getNotificationChange(userId: String): MutableLiveData<List<Notification>> {
        TODO("Not yet implemented")
    }

    override suspend fun getActivityById(activityId: String): Result<Activity?> {
        TODO("Not yet implemented")
    }

    override fun postReport(report: Report){
        TODO("Not yet implemented")
    }
}
