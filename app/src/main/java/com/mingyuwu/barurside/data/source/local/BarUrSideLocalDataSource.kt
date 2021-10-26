package com.mingyuwu.barurside.data.source.local

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.data.source.BarUrSideDataSource

class BarUrSideLocalDataSource(val context: Context) : BarUrSideDataSource {
    override suspend fun getVenue(id: String): MutableLiveData<Venue> {
        TODO("Not yet implemented")
    }
}