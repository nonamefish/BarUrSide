package com.mingyuwu.barurside

import android.app.Application
import android.content.Context

class BarUrSideApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        /* If you has other classes that need context object to initialize when application is created,
         you can use the appContext here to process. */
    }

    companion object {
        var appContext: Context? = null
            private set
    }
}