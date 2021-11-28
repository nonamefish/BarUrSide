package com.mingyuwu.barurside

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mingyuwu.barurside.databinding.ActivityMainBinding
import com.mingyuwu.barurside.discover.Theme
import com.mingyuwu.barurside.ext.getVmFactory
import com.mingyuwu.barurside.login.UserManager
import com.mingyuwu.barurside.util.CurrentFragmentType
import com.mingyuwu.barurside.util.Logger

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
        viewModel.navigateToStart.observe(
            this,
            Observer {
                it?.let {
                    navController.navigate(MainNavigationDirections.navigateToActivityFragment())
                    viewModel.onLeft()
                }
            }
        )

        // navigate to Login
        viewModel.navigateToLogin.observe(
            this,
            Observer {
                it?.let {
                    navController.navigate(MainNavigationDirections.navigateToLoginFragment())
                    viewModel.onLeft()
                }
            }
        )

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser

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
                R.id.activityFragment -> {
                    navController.navigate(MainNavigationDirections.navigateToActivityFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.mapFragment -> {
                    navController.navigate(MainNavigationDirections.navigateToMapFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.discoverFragment -> {
                    navController.navigate(MainNavigationDirections.navigateToDiscoverFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.collectFragment -> {
                    navController.navigate(MainNavigationDirections.navigateToCollectFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.profileFragment -> {
                    navController.navigate(
                        MainNavigationDirections.navigateToProfileFragment(
                            UserManager.user.value?.id
                        )
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
                    Logger.d("signInWithCredential:success, user:$user")
                    viewModel.navigateToStart.value = true
                } else {
                    // If sign in fails, display a message to the user.
                    viewModel.navigateToLogin.value = true
                    Logger.d("signInWithCredential:failure ${task.exception}")
                }
            }
    }

    private fun setupNavController() {
        findNavController(R.id.nav_host_fragment)
            .addOnDestinationChangedListener { navController: NavController,
                _: NavDestination,
                _: Bundle? ->
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
        UserManager.user.observe(
            this,
            Observer {
                it?.let {

                    viewModel.getNotification(it.id)

                    binding.viewModel = viewModel

                    startService(Intent(this, BarUrSideService::class.java))

                    intent.data?.let {
                        handleIntent(intent)
                    }

                    Logger.d("intent: $intent")
                }
            }
        )
    }

    private fun handleIntent(intent: Intent) {

        val appLinkAction = intent.action
        val appLinkData: Uri? = intent.data

        if (Intent.ACTION_VIEW == appLinkAction) {

            appLinkData?.lastPathSegment?.let {
                when (it) {
                    getString(R.string.activity) -> {
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
