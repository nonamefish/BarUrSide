package com.mingyuwu.barurside

import android.content.Context
import android.content.Intent
import android.content.pm.verify.domain.DomainVerificationManager
import android.content.pm.verify.domain.DomainVerificationUserState
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import com.mingyuwu.barurside.login.UserManager
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mingyuwu.barurside.databinding.ActivityMainBinding
import com.mingyuwu.barurside.discover.Theme
import com.mingyuwu.barurside.ext.getVmFactory
import com.mingyuwu.barurside.login.TAG
import com.mingyuwu.barurside.util.CurrentFragmentType

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var userToken = UserManager.userToken
    val viewModel by viewModels<MainViewModel> { getVmFactory() }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        // setting binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // set notification onclick listener
        binding.imgNotification.setOnClickListener {
            navController.navigate(
                MainNavigationDirections.navigateToDiscoverDetailFragment(
                    Theme.NOTIFICATION, null, null
                )
            )
        }

        // set toolbar back button on click listener
        binding.imgBack.setOnClickListener {
            navController.popBackStack()
        }

        // navigate to Start
        viewModel.navigateToStart.observe(this, Observer {
            it?.let {
                navController.navigate(MainNavigationDirections.navigateToActivityFragment())
                viewModel.onLeft()
            }
        })

        // navigate to Login
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
            onGetUserDataFinished()
            viewModel.navigateToStart.value = true
        }


        // get navController and setting connection between bottom navigation item and navigation fragment
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController // container for navigation destination
        binding.bottomNav.setupWithNavController(navController)

        setContentView(binding.root)
        setupBottomNav()
        setupNavController()
    }

    private fun setupBottomNav() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigate_activity -> {
                    navController.navigate(MainNavigationDirections.navigateToActivityFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigate_map -> {
                    navController.navigate(MainNavigationDirections.navigateToMapFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigate_discover -> {
                    navController.navigate(MainNavigationDirections.navigateToDiscoverFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigate_collect -> {
                    navController.navigate(MainNavigationDirections.navigateToCollectFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigate_profile -> {
                    navController.navigate(
                        MainNavigationDirections.navigateToProfileFragment(UserManager.user.value?.id)
                    )
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
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

    private fun setupNavController() {
        findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener { navController: NavController, _: NavDestination, _: Bundle? ->
            viewModel.currentFragmentType.value = when (navController.currentDestination?.id) {
                R.id.activityFragment -> CurrentFragmentType.ACTIVITY
                R.id.addActivityFragment -> CurrentFragmentType.ADD_ACTIVITY
                R.id.collectFragment -> CurrentFragmentType.COLLECT
                R.id.profileFragment -> CurrentFragmentType.PROFILE
                R.id.discoverFragment -> CurrentFragmentType.DISCOVER
                R.id.discoverDetailFragment -> CurrentFragmentType.DISCOVER_DETAIL
                R.id.drinkFragment -> CurrentFragmentType.DRINK
                R.id.venueFragment -> CurrentFragmentType.VENUE
                R.id.editRatingFragment -> CurrentFragmentType.EDIT_RATING
                R.id.mapFragment -> CurrentFragmentType.MAP
                R.id.allRatingFragment -> CurrentFragmentType.ALL_RATING
                R.id.loginFragment -> CurrentFragmentType.LOGIN
                R.id.addDrinkFragment -> CurrentFragmentType.ADD_OBJECT
                R.id.addVenueFragment -> CurrentFragmentType.ADD_OBJECT
                else -> viewModel.currentFragmentType.value
            }
        }
    }

    fun onGetUserDataFinished() {
        UserManager.user.observe(this, Observer {
            it?.let {
                viewModel.getNotification(it.id)
                binding.viewModel = viewModel
                startService(Intent(this, BarUrSideService::class.java))
                Log.d("Ming","intent: $intent")
                intent.data?.let{
                    handleIntent(intent)
                }
            }
        })
    }


    private fun handleIntent(intent: Intent) {
        val appLinkAction = intent.action
        val appLinkData: Uri? = intent.data
        if (Intent.ACTION_VIEW == appLinkAction) {
            appLinkData?.lastPathSegment?.let {
                when (it) {
                    "activity" -> {
                        val id = intent.data?.getQueryParameter("id")
                        navController.navigate(
                            MainNavigationDirections.navigateToActivityDetailDialog(
                                null,
                                id,
                                Theme.NONE
                            )
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val intent = intent
        onNewIntent(intent)
    }
}