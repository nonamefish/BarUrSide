package com.mingyuwu.barurside.util

import android.Manifest
import com.mingyuwu.barurside.R

sealed class AppPermission(
    val permissionName: String,
    val requestCode: Int,
    val deniedMessageId: Int,
    val explanationMessageId: Int,
) {

    /**Location PERMISSIONS**/
    object AccessCoarseLocation : AppPermission(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        1,
        R.string.permission_reject,
        R.string.permission_location_collect
    )

    object AccessFineLocation : AppPermission(
        Manifest.permission.ACCESS_FINE_LOCATION,
        2,
        R.string.permission_reject,
        R.string.permission_location_collect
    )

    /**Image PERMISSIONS**/
    object ReadExternalStorage : AppPermission(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        3,
        R.string.permission_reject,
        R.string.permission_image
    )
}