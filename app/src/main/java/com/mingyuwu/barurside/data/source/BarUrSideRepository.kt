package com.mingyuwu.barurside.data.source

import androidx.lifecycle.MutableLiveData
import com.mingyuwu.barurside.data.Drink
import com.mingyuwu.barurside.data.Rating
import com.mingyuwu.barurside.data.User
import com.mingyuwu.barurside.data.Venue

interface BarUrSideRepository {
    fun getVenue(id: String): MutableLiveData<Venue>
    fun getRating(id: String, isVenue: Boolean): MutableLiveData<List<Rating>>
    fun getDrink(id: String): MutableLiveData<Drink>
    fun getUser(id: String): MutableLiveData<User>
}