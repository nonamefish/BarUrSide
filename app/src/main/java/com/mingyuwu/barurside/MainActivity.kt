package com.mingyuwu.barurside

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mingyuwu.barurside.databinding.ActivityMainBinding
import com.mingyuwu.barurside.discover.Theme
import com.mingyuwu.barurside.ext.getVmFactory
import com.mingyuwu.barurside.login.UserManager
import com.mingyuwu.barurside.util.CurrentFragmentType
import com.mingyuwu.barurside.util.Logger
import com.mingyuwu.barurside.util.Util.popUpMenuOtherUser
import com.mingyuwu.barurside.util.Util.popUpMenuUser

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
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
                    Theme.NOTIFICATION, null, null
                )
            )
        }

        // set toolbar back button on click listener
        binding.imgBack.setOnClickListener {
            navController.popBackStack()
        }

        // observe currentFragmentType，搬移 BindingAdapter 的顯示/隱藏邏輯
        viewModel.currentFragmentType.observe(this) { type ->
            // Toolbar 本身顯示/隱藏
            binding.toolbarMain.visibility = if (
                type == CurrentFragmentType.MAP || type == CurrentFragmentType.LOGIN
            ) View.GONE else View.VISIBLE

            // Toolbar Back Icon
            binding.imgBack.visibility = if (type in listOf(
                    CurrentFragmentType.DISCOVER_DETAIL,
                    CurrentFragmentType.OTHER_PROFILE,
                    CurrentFragmentType.EDIT_RATING,
                    CurrentFragmentType.ALL_RATING,
                    CurrentFragmentType.DRINK,
                    CurrentFragmentType.VENUE
                )
            ) View.VISIBLE else View.GONE

            // Toolbar BarUrSide Title
            binding.txtBarursideTitle.visibility =
                if (type == CurrentFragmentType.ACTIVITY) View.VISIBLE else View.GONE

            // Toolbar Toolbar Title
            binding.txtToolbarTitle.visibility =
                if (type != CurrentFragmentType.ACTIVITY) View.VISIBLE else View.GONE
            binding.txtToolbarTitle.text = when (type) {
                CurrentFragmentType.DISCOVER_DETAIL -> viewModel.discoverType.value
                else -> type.value
            } ?: ""

            // Notification Icon 區塊（整個 ConstraintLayout）
            binding.layoutNotify.visibility = if (type in listOf(
                    CurrentFragmentType.ACTIVITY,
                    CurrentFragmentType.DISCOVER,
                    CurrentFragmentType.COLLECT
                )
            ) View.VISIBLE else View.GONE

            // Notification Badge
            viewModel.notificationSize?.observe(this) { size ->
                binding.imgBadge.visibility = if (size > 0) View.VISIBLE else View.GONE
                binding.txtNotify.visibility = if (size > 0) View.VISIBLE else View.GONE
                binding.txtNotify.text = size.toString()
            }

            // Report Icon
            binding.imgReport.visibility = if (type in listOf(
                    CurrentFragmentType.OTHER_PROFILE,
                    CurrentFragmentType.USER_PROFILE
                )
            ) View.VISIBLE else View.GONE

            // BottomNav 顯示/隱藏
            binding.bottomNav.visibility = if (type in listOf(
                    CurrentFragmentType.LOGIN,
                    CurrentFragmentType.EDIT_RATING,
                    CurrentFragmentType.ALL_RATING,
                    CurrentFragmentType.ADD_OBJECT
                )
            ) View.GONE else View.VISIBLE
        }

        // navigate to Start
        viewModel.navigateToStart.observe(
            this, {
                it?.let {
                    navController.navigate(MainNavigationDirections.navigateToActivityFragment())
                    viewModel.onLeft()
                }
            }
        )

        // navigate to Login
        viewModel.navigateToLogin.observe(
            this, {
                it?.let {
                    navController.navigate(MainNavigationDirections.navigateToLoginFragment())
                    viewModel.onLeft()
                }
            }
        )

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser

        if (currentUser == null) {
            viewModel.navigateToLogin.value = true
        } else {
            viewModel.getUserData(currentUser.email!!)
            onGetUserDataFinished()
            viewModel.navigateToStart.value = true
        }

        // get navController and setting connection between bottom navigation item and navigation fragment
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
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

    private fun setupNavController() {
        findNavController(R.id.navHostFragment)
            .addOnDestinationChangedListener {
                    navController: NavController,
                    _: NavDestination,
                    arguments: Bundle?,
                ->
                viewModel.currentFragmentType.value = when (navController.currentDestination?.id) {
                    R.id.activityFragment -> CurrentFragmentType.ACTIVITY
                    R.id.addActivityFragment -> CurrentFragmentType.ADD_ACTIVITY
                    R.id.collectFragment -> CurrentFragmentType.COLLECT
                    R.id.profileFragment -> {
                        val id = arguments?.get("id") as String?
                        if (id == UserManager.user.value?.id) {
                            // set report onclick listener
                            binding.imgReport.setOnClickListener {
                                popUpMenuUser(binding.imgReport, this)
                            }
                            CurrentFragmentType.USER_PROFILE
                        } else {
                            // set report onclick listener
                            binding.imgReport.setOnClickListener {
                                popUpMenuOtherUser(binding.imgReport, this, id ?: "")
                            }
                            CurrentFragmentType.OTHER_PROFILE
                        }
                    }

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
            this, {
                it?.let {

                    viewModel.getNotification(it.id)

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
