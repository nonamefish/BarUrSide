package com.mingyuwu.barurside

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.mingyuwu.barurside.databinding.ActivityMainBinding
import com.mingyuwu.barurside.discover.Theme

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
//        addData()
        setContentView(binding.root)
    }

//    fun addData() {
//        val articles = FirebaseFirestore.getInstance()
//            .collection("rating")
//
//        for( dt in  RatingData.rating.rating){
//            val document = articles.document()
//            val data = Rating.toHashMap(dt)
//            document.set(data)
//        }
//    }

}