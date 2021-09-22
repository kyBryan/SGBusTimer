package com.acn.sgbustimer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.widget.ImageButton
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.acn.sgbustimer.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration : AppBarConfiguration
    private lateinit var navController : NavController
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        drawerLayout = binding.drawerLayout
        navController = this.findNavController(R.id.navHostFragment)
        //NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        toolbar = binding.toolbar

        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // prevent nav gesture if not on start destination
        navController.addOnDestinationChangedListener { nc: NavController, nd: NavDestination, bundle: Bundle? ->
            if (nd.id == nc.graph.startDestination) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null))

            } else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                supportActionBar?.setHomeAsUpIndicator(R.drawable.closeicon)
                toolbar.setBackgroundColor(getResources().getColor(R.color.toolBarColorYellow, null))
            }
        }
        NavigationUI.setupWithNavController(binding.navView, navController)

        // setup navigation close button
        val navCloseBtn = binding.navView.getHeaderView(0).findViewById<ImageButton>(R.id.navCloseBtn)
        navCloseBtn.setOnClickListener(){
            Timber.i("Navigation Drawer Close Button Pressed")
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }



    override fun onSupportNavigateUp(): Boolean {
        Timber.i("Enter Support Navigation Up")
        val navController = this.findNavController(R.id.navHostFragment)
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }
}