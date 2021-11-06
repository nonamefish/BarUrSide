package com.mingyuwu.barurside

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mingyuwu.barurside.login.UserManager
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.mingyuwu.barurside.data.Activity
import com.mingyuwu.barurside.data.User
import com.mingyuwu.barurside.data.mockdata.ActivityData
import com.mingyuwu.barurside.databinding.ActivityMainBinding
import com.mingyuwu.barurside.discover.Theme
import com.mingyuwu.barurside.ext.getVmFactory
import com.mingyuwu.barurside.login.TAG

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var userToken = UserManager.userToken
    val viewModel by viewModels<MainViewModel> { getVmFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

        // setting binding
        binding = ActivityMainBinding.inflate(layoutInflater)

        // set notification onclick listener
        binding.imgNotification.setOnClickListener {
            navController.navigate(
                MainNavigationDirections.navigateToDiscoverDetailFragment(
                    Theme.NOTIFICATION, null, null // TODO: set User ID
                )
            )
        }

        // navigate to Start
        viewModel.navigateToStart.observe(this, Observer {
            it?.let {
                navController.navigate(MainNavigationDirections.navigateToActivityFragment())
                viewModel.onLeft()
            }
        })

        // navigate to Start
        viewModel.navigateToLogin.observe(this, Observer {
            it?.let {
                navController.navigate(MainNavigationDirections.navigateToLoginFragment())
                viewModel.onLeft()
            }
        })


        // Check if user is signed in (non-null) and update UI accordingly.
        var currentUser = auth.currentUser

        if (currentUser == null) {
            if (userToken == null) {
                viewModel.navigateToLogin.value = true
            } else {
                firebaseAuthWithGoogle(userToken!!)
            }
        } else {
            viewModel.getUserData(currentUser.email!!)
            viewModel.navigateToStart.value = true
        }

        // get navController and setting connection between bottom navigation item and navigation fragment
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController // container for navigation destination
        binding.bottomNav.setupWithNavController(navController)

        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

    }


    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    Log.d(TAG, "signInWithCredential:success, user:$user")
                    viewModel.navigateToStart.value = true
                } else {
                    // If sign in fails, display a message to the user.
                    viewModel.navigateToLogin.value = true
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }
}