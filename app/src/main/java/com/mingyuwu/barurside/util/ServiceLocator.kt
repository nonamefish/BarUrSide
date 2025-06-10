package com.mingyuwu.barurside.util

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.mingyuwu.barurside.data.source.BarUrSideDataSource
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import com.mingyuwu.barurside.data.source.DefaultBarUrSideRepository
import com.mingyuwu.barurside.data.source.local.BarUrSideLocalDataSource
import com.mingyuwu.barurside.data.source.remote.BarUrSideRemoteDataSource

object ServiceLocator {

    @Volatile
    var repository: BarUrSideRepository? = null
        @VisibleForTesting set

    fun provideRepository(context: Context): BarUrSideRepository {
        synchronized(this) {
            return repository
                ?: repository
                ?: createPublisherRepository(context)
        }
    }

    private fun createPublisherRepository(context: Context): BarUrSideRepository {
        return DefaultBarUrSideRepository(
            BarUrSideRemoteDataSource,
            createLocalDataSource(context)
        )
    }

    private fun createLocalDataSource(context: Context): BarUrSideDataSource {
        return BarUrSideLocalDataSource(context)
    }
}
