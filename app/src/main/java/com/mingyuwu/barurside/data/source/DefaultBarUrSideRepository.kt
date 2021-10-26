package com.mingyuwu.barurside.data.source

import androidx.lifecycle.MutableLiveData
import com.mingyuwu.barurside.data.Drink
import com.mingyuwu.barurside.data.Rating
import com.mingyuwu.barurside.data.User
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

    override fun getDrink(id: String): MutableLiveData<Drink>{
        return remoteDataSource.getDrink(id)
    }

    override fun getUser(id: String): MutableLiveData<User>{
        return remoteDataSource.getUser(id)
    }
}