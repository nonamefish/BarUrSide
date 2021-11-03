package com.mingyuwu.barurside.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mingyuwu.barurside.data.Relationship
import com.mingyuwu.barurside.data.User
import java.sql.Timestamp

object UserManager {
    private val _user = MutableLiveData<User>(User(
        "6BhbnIMi1Ai91Ky4w9rI",
        "Ming",
        "user/MingYuWu.jpg",
        listOf(
            Relationship("O6kHLHzc3Ollfe7rkfkR", Timestamp(System.currentTimeMillis())),
            Relationship("deXVITKTFc1qsEoeWnr0", Timestamp(System.currentTimeMillis()))
        ),
        6,
        7
    ))

    val user: LiveData<User>
        get() = _user
}