package com.mingyuwu.barurside.data.source.local

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.mingyuwu.barurside.data.Rating
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.data.source.BarUrSideDataSource

class BarUrSideLocalDataSource(val context: Context) : BarUrSideDataSource {
    override fun getVenue(id: String): MutableLiveData<Venue> {
        TODO("Not yet implemented")
    }
    override fun getRating(id: String, isVenue: Boolean): MutableLiveData<List<Rating>>{
        TODO("Not yet implemented")
    }
}