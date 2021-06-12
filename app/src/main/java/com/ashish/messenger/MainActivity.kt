package com.ashish.messenger

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.ashish.messenger.databinding.ActivityMainBinding
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private var user : FirebaseUser? = null
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var listener: NavController.OnDestinationChangedListener

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

    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(listener)
        if(user!=null) updateActiveStatus(true)
    }

    override fun onPause() {
        super.onPause()
        navController.removeOnDestinationChangedListener(listener)
        if(user!=null) updateActiveStatus(false)
    }

    private fun updateActiveStatus(isActive: Boolean) {
        Firebase.firestore
                .collection("users")
                .document(Firebase.auth.currentUser!!.uid)
                .update("online",isActive)
                .addOnSuccessListener {
                    Log.i(TAG, "updateActiveStatus: is User active? : $isActive")
                }
    }


    companion object{
        const val TAG = "MainActivity"
    }
}