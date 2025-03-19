package com.silab

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.silab.data.datastore.DataStore
import com.silab.databinding.ActivityMainBinding
import com.silab.di.ViewModelFactory
import com.silab.ui.cart.CartViewModel
import com.silab.ui.home.HomeViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val cartViewModel: CartViewModel by viewModels {
        ViewModelFactory.getInstance(this, application)
    }
    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(this, application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val dataStore = DataStore.getInstance(applicationContext)
        lifecycleScope.launch {
            val token = dataStore.getToken()

            if (token.isNullOrEmpty()) {
                navView.visibility = View.GONE
                navController.navigate(R.id.navigation_home)
            } else {
                navView.visibility = View.VISIBLE
                val appBarConfiguration = AppBarConfiguration(
                    setOf(
                        R.id.navigation_home, R.id.navigation_borrow, R.id.navigation_profile
                    )
                )
                navView.setupWithNavController(navController)
            }
        }

        // Handle Intent
        val navigateTo = intent.getStringExtra("navigate_to")
        if (navigateTo == "borrow") {
            navigateToBorrowWithBottomNav()
        }
    }

    private fun navigateToBorrowWithBottomNav() {
        binding.navView.selectedItemId = R.id.navigation_borrow
    }
}
