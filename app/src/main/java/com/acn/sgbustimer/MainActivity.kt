package com.acn.sgbustimer

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.acn.sgbustimer.controller.BusViewFragment
import com.acn.sgbustimer.menu.ClickListener
import com.acn.sgbustimer.menu.NavigationDrawerAdapter
import com.acn.sgbustimer.menu.NavigationDrawerRowListener
import com.acn.sgbustimer.model.NavigationItemModel

class MainActivity : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    private lateinit var adapter: NavigationDrawerAdapter

    // UI views
    private lateinit var main_toolbar: Toolbar
    private lateinit var nav_recycle_view: RecyclerView
    private lateinit var nav_header_img: ImageView
    private lateinit var nav_linear_layout: LinearLayout


    private var items = arrayListOf(
        NavigationItemModel(R.drawable.ic_home, "Home"),
//        NavigationItemModel(R.drawable.ic_music, "Music"),
//        NavigationItemModel(R.drawable.ic_movie, "Movies"),
//        NavigationItemModel(R.drawable.ic_book, "Books"),
//        NavigationItemModel(R.drawable.ic_profile, "Profile"),
//        NavigationItemModel(R.drawable.ic_settings, "Settings"),
//        NavigationItemModel(R.drawable.ic_social, "Like us on facebook")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)

        // Set the toolbar
        main_toolbar = findViewById(R.id.activity_main_toolbar)
        setSupportActionBar(main_toolbar)

        // Setup Recyclerview's Layout
        nav_recycle_view = findViewById(R.id.navigation_rv)
        nav_recycle_view.layoutManager = LinearLayoutManager(this)
        nav_recycle_view.setHasFixedSize(true)

        // Add Item Touch Listener
        nav_recycle_view.addOnItemTouchListener(NavigationDrawerRowListener(this, object : ClickListener {
            override fun onClick(view: View, position: Int) {
                when (position) {
                    0 -> {
                        // # Home Fragment
                        val busFragment = BusViewFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.activity_main_content_id, busFragment).commit()
                    }
//                    1 -> {
//                        // # Music Fragment
//                        val musicFragment = DemoFragment()
//                        supportFragmentManager.beginTransaction()
//                            .replace(R.id.activity_main_content_id, musicFragment).commit()
//                    }
//                    2 -> {
//                        // # Movies Fragment
//                        val moviesFragment = DemoFragment()
//                        supportFragmentManager.beginTransaction()
//                            .replace(R.id.activity_main_content_id, moviesFragment).commit()
//                    }
//                    3 -> {
//                        // # Books Fragment
//                        val booksFragment = DemoFragment()
//                        supportFragmentManager.beginTransaction()
//                            .replace(R.id.activity_main_content_id, booksFragment).commit()
//                    }
//                    4 -> {
//                        // # Profile Activity
//                        val intent = Intent(this@MainActivity, DemoActivity::class.java)
//                        startActivity(intent)
//                    }
//                    5 -> {
//                        // # Settings Fragment
//                        val settingsFragment = DemoFragment()
//                        supportFragmentManager.beginTransaction()
//                            .replace(R.id.activity_main_content_id, settingsFragment).commit()
//                    }
//                    6 -> {
//                        // # Open URL in browser
//                        val uri: Uri = Uri.parse("https://johnc.co/fb")
//                        val intent = Intent(Intent.ACTION_VIEW, uri)
//                        startActivity(intent)
//                    }
                }
                // Don't highlight the 'Profile' and 'Like us on Facebook' item row
                if (position != 6 && position != 4) {
                    updateAdapter(position)
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    drawerLayout.closeDrawer(GravityCompat.START)
                }, 200)
            }
        }))

        // Update Adapter with item data and highlight the default menu item ('Home' Fragment)
        updateAdapter(0)

        // Set 'Home' as the default fragment when the app starts
        val busFragment = BusViewFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.activity_main_content_id, busFragment).commit()


        // Close the soft keyboard when you open or close the Drawer
        val toggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(this, drawerLayout, main_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            override fun onDrawerClosed(drawerView: View) {
                // Triggered once the drawer closes
                super.onDrawerClosed(drawerView)
                try {
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                } catch (e: Exception) {
                    e.stackTrace
                }
            }

            override fun onDrawerOpened(drawerView: View) {
                // Triggered once the drawer opens
                super.onDrawerOpened(drawerView)
                try {
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                } catch (e: Exception) {
                    e.stackTrace
                }
            }
        }
        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()

        // Set Header Image
        nav_header_img = findViewById(R.id.navigation_header_img)
        nav_header_img.setImageResource(R.drawable.logo)

        // Set background of Drawer
        nav_linear_layout = findViewById(R.id.navigation_layout)
        nav_linear_layout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
    }

    private fun updateAdapter(highlightItemPos: Int) {
        adapter = NavigationDrawerAdapter(items, highlightItemPos)
        nav_recycle_view.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            // Checking for fragment count on back stack
            if (supportFragmentManager.backStackEntryCount > 0) {
                // Go to the previous fragment
                supportFragmentManager.popBackStack()
            } else {
                // Exit the app
                super.onBackPressed()
            }
        }
    }
}