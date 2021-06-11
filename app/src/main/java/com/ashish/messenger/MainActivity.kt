package com.ashish.messenger

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.graphics.Color
import android.graphics.ColorFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.*
import com.ashish.messenger.databinding.ActivityMainBinding
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private var user : FirebaseUser? = null
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var listener: NavController.OnDestinationChangedListener
    val url = "https://firebasestorage.googleapis.com/v0/b/ktmessenger-daf3e.appspot.com/o/profile_pictures%2Fa67837b5-01b9-44fb-87f2-793c5dfa84a5.png?alt=media&token=28625b9e-a40f-4efa-bfce-c70e4172f064"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        user = Firebase.auth.currentUser
        drawerLayout = binding.drawerLayout
        navController = this.findNavController(R.id.nav_host_fragment)


        appBarConfiguration = AppBarConfiguration(navController.graph,drawerLayout)
        val toolbar = binding.toolbar
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        listener = NavController.OnDestinationChangedListener { controller, destination, arguments ->
            if(destination.id == R.id.loginFragment) {
                Log.i(TAG, "onCreate: Logging Out")
                controller.graph.startDestination = R.id.loginFragment
                Firebase.auth.signOut()
            }
        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(findNavController(R.id.nav_host_fragment))
                || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }

    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }else {
            super.onBackPressed()
        }
    }


    companion object{
        const val TAG = "MainActivity"
    }
}