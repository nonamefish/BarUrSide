package com.mingyuwu.barurside.ext

import android.app.Activity
import com.mingyuwu.barurside.BarUrSideApplication
import com.mingyuwu.barurside.factory.ViewModelFactory

fun Activity.getVmFactory(): ViewModelFactory {
    val repository = (applicationContext as BarUrSideApplication).repository
    return ViewModelFactory(repository)
}
