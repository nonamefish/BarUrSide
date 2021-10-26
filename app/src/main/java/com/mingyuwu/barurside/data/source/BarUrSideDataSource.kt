package com.mingyuwu.barurside.data.source

import androidx.lifecycle.MutableLiveData
import com.mingyuwu.barurside.data.Rating
import com.mingyuwu.barurside.data.Venue

interface BarUrSideDataSource {
    fun getVenue(id: String): MutableLiveData<Venue>
    fun getRating(id: String, isVenue: Boolean): MutableLiveData<List<Rating>>
}