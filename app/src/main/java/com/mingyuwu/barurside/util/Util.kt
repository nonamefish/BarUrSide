package com.mingyuwu.barurside.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.mingyuwu.barurside.BarUrSideApplication

object Util {

    /**
     * Determine and monitor the connectivity status
     *
     * https://developer.android.com/training/monitoring-device-state/connectivity-monitoring
     */

    fun getString(resourceId: Int): String {
        return BarUrSideApplication.instance.getString(resourceId)
    }

    fun getColor(resourceId: Int): Int {
        return BarUrSideApplication.instance.getColor(resourceId)
    }
}
