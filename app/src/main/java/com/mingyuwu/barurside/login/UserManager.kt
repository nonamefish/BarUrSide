package com.mingyuwu.barurside.login

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.mingyuwu.barurside.BarUrSideApplication
import com.mingyuwu.barurside.data.User

object UserManager {

    private const val USER_DATA = "user_data"
    private const val USER_TOKEN = "user_token"

    var user = MutableLiveData<User>()

    var userToken: String? = null
        get() = BarUrSideApplication.instance
            .getSharedPreferences(USER_DATA, Context.MODE_PRIVATE)
            .getString(USER_TOKEN, null)
        set(value) {
            field = when (value) {
                null -> {
                    BarUrSideApplication.instance
                        .getSharedPreferences(USER_DATA, Context.MODE_PRIVATE).edit()
                        .remove(USER_TOKEN)
                        .apply()
                    null
                }
                else -> {
                    BarUrSideApplication.instance
                        .getSharedPreferences(USER_DATA, Context.MODE_PRIVATE).edit()
                        .putString(USER_TOKEN, value)
                        .apply()
                    value
                }
            }
        }
}