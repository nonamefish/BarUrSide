package com.mingyuwu.barurside.ext

import android.app.Activity
import android.view.Gravity
import android.widget.Toast
import com.mingyuwu.barurside.factory.ViewModelFactory


fun Activity.getVmFactory(): ViewModelFactory {
//    val repository = (applicationContext as StylishApplication).stylishRepository
    return ViewModelFactory()
}

fun Activity?.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).apply {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }
}