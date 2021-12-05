package com.mingyuwu.barurside.ext

import android.content.pm.PackageManager
import android.view.Gravity
import android.widget.Toast
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import com.mingyuwu.barurside.BarUrSideApplication
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.activity.ActivityTypeFilter
import com.mingyuwu.barurside.data.Activity
import com.mingyuwu.barurside.data.RatingInfo
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.discover.Theme
import com.mingyuwu.barurside.factory.*
import com.mingyuwu.barurside.filter.FilterParameter
import com.mingyuwu.barurside.util.AppPermission
import com.mingyuwu.barurside.util.Util
import com.permissionx.guolindev.PermissionX

fun Fragment.getVmFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as BarUrSideApplication).repository
    return ViewModelFactory(repository)
}

fun Fragment.getVmFactory(
    id: List<String>?,
    theme: Theme,
    filterParameter: FilterParameter?,
): DiscoverDetailViewModelFactory {
    val repository = (requireContext().applicationContext as BarUrSideApplication).repository
    return DiscoverDetailViewModelFactory(repository, id, theme, filterParameter)
}

fun Fragment.getVmFactory(isVenue: Boolean): CollectPageViewModelFactory {
    val repository = (requireContext().applicationContext as BarUrSideApplication).repository
    return CollectPageViewModelFactory(repository, isVenue)
}

fun Fragment.getVmFactory(type: ActivityTypeFilter): ActivityPageViewModelFactory {
    val repository = (requireContext().applicationContext as BarUrSideApplication).repository
    return ActivityPageViewModelFactory(repository, type)
}

fun Fragment.getVmFactory(id: String): InfoViewModelFactory {
    val repository = (requireContext().applicationContext as BarUrSideApplication).repository
    return InfoViewModelFactory(repository, id)
}

fun Fragment.getVmFactory(activity: Activity?, activityId: String?): ActivityViewModelFactory {
    val repository = (requireContext().applicationContext as BarUrSideApplication).repository
    return ActivityViewModelFactory(repository, activity, activityId)
}

fun Fragment.getVmFactory(venue: Venue): EditRatingViewModelFactory {
    val repository = (requireContext().applicationContext as BarUrSideApplication).repository
    return EditRatingViewModelFactory(repository, venue)
}

fun Fragment.getVmFactory(ratings: List<RatingInfo>): AllRatingViewModelFactory {
    return AllRatingViewModelFactory(ratings)
}

fun Fragment.isPermissionGranted(permission: AppPermission) =
    (this.context?.let {
        PermissionChecker.checkSelfPermission(it, permission.permissionName)
    } == PackageManager.PERMISSION_GRANTED)

fun Fragment.requestPermission(permission: AppPermission) =
    PermissionX.init(activity)
        .permissions(
            permission.permissionName
        )
        .onExplainRequestReason { scope, deniedList ->
            scope.showRequestReasonDialog(
                deniedList,
                Util.getString(permission.explanationMessageId),
                Util.getString(R.string.permission_confirm),
                Util.getString(permission.deniedMessageId)
            )
        }
        .onForwardToSettings { scope, deniedList ->
            scope.showForwardToSettingsDialog(
                deniedList,
                Util.getString(permission.explanationMessageId),
                Util.getString(R.string.permission_confirm),
                Util.getString(permission.deniedMessageId)
            )
        }
        .request { allGranted, _, deniedList ->
            if (!allGranted) {
                showToast("message: ${getString(R.string.permission_reject_toast, deniedList)}")
            }
        }

fun Fragment.showToast(message: String) {
    Toast.makeText(this.context, message, Toast.LENGTH_SHORT).apply {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }
}
