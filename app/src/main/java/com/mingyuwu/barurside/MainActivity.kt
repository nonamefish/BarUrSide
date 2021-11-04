package com.mingyuwu.barurside

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.mingyuwu.barurside.data.Activity
import com.mingyuwu.barurside.data.mockdata.ActivityData
import com.mingyuwu.barurside.databinding.ActivityMainBinding
import com.mingyuwu.barurside.discover.Theme
import com.mingyuwu.barurside.ext.getVmFactory

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

        // get navController and setting connection between bottom navigation item and navigation fragment
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController // container for navigation destination
        binding.bottomNav.setupWithNavController(navController)

        // set notification onclick listener
        binding.imgNotification.setOnClickListener {
            navController.navigate(
                MainNavigationDirections.navigateToDiscoverDetailFragment(
                    Theme.NOTIFICATION,null,null // TODO: set User ID
                )
            )
        }
//        addData()
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        // Check if user is signed in (non-null) and update UI accordingly.
        var currentUser = auth.currentUser

        if(currentUser==null){
            navController.navigate(MainNavigationDirections.navigateToLoginFragment())
        }
    }

    fun addData() {
        val articles = FirebaseFirestore.getInstance()
            .collection("activity")

        for( dt in  ActivityData.activity.activity){
            val document = articles.document()
            val data = Activity.toHashMap(dt)
            document.set(data)
        }
    }
}