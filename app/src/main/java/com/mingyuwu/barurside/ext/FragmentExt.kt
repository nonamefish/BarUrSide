package com.mingyuwu.barurside.ext

import androidx.fragment.app.Fragment
import com.mingyuwu.barurside.BarUrSideApplication
import com.mingyuwu.barurside.activity.ActivityTypeFilter
import com.mingyuwu.barurside.data.Activity
import com.mingyuwu.barurside.data.Venue
import com.mingyuwu.barurside.discover.Theme
import com.mingyuwu.barurside.factory.*
import com.mingyuwu.barurside.filter.FilterParameter


fun Fragment.getVmFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as BarUrSideApplication).repository
    return ViewModelFactory(repository)
}

fun Fragment.getVmFactory(
    id: List<String>?,
    theme: Theme,
    filterParameter: FilterParameter?
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

fun Fragment.getVmFactory(activity: Activity): ActivityViewModelFactory {
    val repository = (requireContext().applicationContext as BarUrSideApplication).repository
    return ActivityViewModelFactory(repository, activity)
}

fun Fragment.getVmFactory(venue: Venue): EditRatingViewModelFactory {
    val repository = (requireContext().applicationContext as BarUrSideApplication).repository
    return EditRatingViewModelFactory(repository, venue)
}
