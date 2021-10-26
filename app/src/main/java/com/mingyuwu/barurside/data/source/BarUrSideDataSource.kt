package com.mingyuwu.barurside.data.source

import androidx.lifecycle.MutableLiveData
import com.mingyuwu.barurside.data.Venue

interface BarUrSideDataSource {
    suspend fun getVenue(id: String): MutableLiveData<Venue>
}