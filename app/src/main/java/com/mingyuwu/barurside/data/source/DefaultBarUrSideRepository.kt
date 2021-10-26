package com.mingyuwu.barurside.data.source

import androidx.lifecycle.MutableLiveData
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.data.source.local.BarUrSideLocalDataSource
import com.mingyuwu.barurside.data.source.remote.BarUrSideRemoteDataSource

class DefaultBarUrSideRepository(
    private val remoteDataSource: BarUrSideDataSource,
    private val localDataSource: BarUrSideDataSource
) : BarUrSideRepository {

    override suspend fun getVenue(id: String): MutableLiveData<Venue> {
        return remoteDataSource.getVenue(id)
    }
}