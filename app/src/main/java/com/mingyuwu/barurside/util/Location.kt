package com.mingyuwu.barurside.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.mingyuwu.barurside.BarUrSideApplication
import com.mingyuwu.barurside.Constants.REQUEST_ENABLE_GPS
import com.mingyuwu.barurside.R

object Location {

    private var location = MutableLiveData<LatLng>()

    fun getLocation(activity: Activity): MutableLiveData<LatLng> {

        if (location.value == null) {
            // set request Location Updates
            checkGPSState(activity)
            startLocationUpdates()
        }

        return location
    }

    // check GPS state
    private fun checkGPSState(activity: Activity) {

        val context = BarUrSideApplication.instance.applicationContext
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            AlertDialog.Builder(context)
                .setTitle(Util.getString(R.string.request_gps_title))
                .setMessage(Util.getString(R.string.request_gps_content))
                .setPositiveButton(
                    Util.getString(R.string.request_gps_positive)
                ) { _, _ ->
                    startActivityForResult(
                        activity,
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                        REQUEST_ENABLE_GPS,
                        null
                    )
                }
                .setNegativeButton(Util.getString(R.string.request_gps_cancel), null)
                .show()
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {

        val mFusedLocationProviderClient = LocationServices
            .getFusedLocationProviderClient(
                BarUrSideApplication.instance.applicationContext
            )

        val locationRequest = LocationRequest
            .create()
            .apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

        mFusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    p0.lastLocation?.let {
                        location.value = LatLng(it.latitude, it.longitude)
                    }
                }
            },
            null
        )
    }

}