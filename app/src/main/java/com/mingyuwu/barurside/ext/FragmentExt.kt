package com.mingyuwu.barurside.ext

import androidx.fragment.app.Fragment
import com.mingyuwu.barurside.discover.Theme
import com.mingyuwu.barurside.discoverdetail.DiscoverDetailViewModel
import com.mingyuwu.barurside.factory.DiscoverDetailViewModelFactory
import com.mingyuwu.barurside.factory.ViewModelFactory


fun Fragment.getVmFactory(): ViewModelFactory {
//    val repository = (requireContext().applicationContext as StylishApplication).stylishRepository
    return ViewModelFactory()
}

fun Fragment.getVmFactory(theme: Theme): DiscoverDetailViewModelFactory {
//    val repository = (requireContext().applicationContext as StylishApplication).stylishRepository
    return DiscoverDetailViewModelFactory(theme)
}