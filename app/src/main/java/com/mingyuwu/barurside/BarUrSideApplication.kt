package com.mingyuwu.barurside

import android.app.Application
import android.content.Context
import com.mingyuwu.barurside.data.source.BarUrSideRepository
import com.mingyuwu.barurside.util.ServiceLocator
import kotlin.properties.Delegates

class BarUrSideApplication : Application() {

    companion object {
        var instance: BarUrSideApplication by Delegates.notNull()
        var appContext: Context? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        instance = this
    }

    // Depends on the flavor,
    val repository: BarUrSideRepository
        get() = ServiceLocator.provideRepository(this)
}
