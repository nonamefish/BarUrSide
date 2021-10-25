package com.mingyuwu.barurside

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.mingyuwu.barurside.databinding.ActivityMainBinding
import com.mingyuwu.barurside.discover.Theme
import com.mingyuwu.barurside.editrating.EditRatingFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setting binding
        binding = ActivityMainBinding.inflate(layoutInflater)

        // get navController and setting connection between bottom navigation item and navigation fragment
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController // container for navigation destination
        binding.bottomNav.setupWithNavController(navController)

        // set notification onclick listener
        binding.imgNotification.setOnClickListener {
            navController.navigate(
                MainNavigationDirections.navigateToDiscoverDetailFragment(
                    Theme.NOTIFICATION,"",null // TODO: set User ID
                )
            )
        }

        setContentView(binding.root)
    }

}