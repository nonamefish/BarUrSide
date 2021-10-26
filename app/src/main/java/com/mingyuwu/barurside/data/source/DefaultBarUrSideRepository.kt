package com.mingyuwu.barurside.data.source

import androidx.lifecycle.MutableLiveData
import com.mingyuwu.barurside.data.Rating
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.data.source.local.BarUrSideLocalDataSource
import com.mingyuwu.barurside.data.source.remote.BarUrSideRemoteDataSource

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
}