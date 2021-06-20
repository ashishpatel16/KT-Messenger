package com.ashish.messenger.ui

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.ashish.messenger.MyFirebaseMessagingService
import com.ashish.messenger.R
import com.ashish.messenger.databinding.ActivityMainBinding
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private var user : FirebaseUser? = null
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var listener: NavController.OnDestinationChangedListener? = null
    private lateinit var header: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        user = Firebase.auth.currentUser
        Log.i(TAG, "onCreate: User : $user")
        navController = this.findNavController(R.id.nav_host_fragment)

        logFcmToken()

        setUpUI()

    }


    public fun setUpUI() {
        if(user != null) {
            drawerLayout = binding.drawerLayout
            header = binding.navView.getHeaderView(0)

            val toolbar = binding.toolbar
            toolbar.visibility = View.VISIBLE
            toolbar.setTitleTextColor(Color.WHITE)
            setSupportActionBar(toolbar)
            appBarConfiguration = AppBarConfiguration(setOf(R.id.chatsFragment, R.id.contactsFragment), drawerLayout)

            binding.navView.setupWithNavController(navController)
            setupActionBarWithNavController(navController, appBarConfiguration)

            setUpShareButton()

            listener = NavController.OnDestinationChangedListener { controller, destination, arguments ->
                if (destination.id == R.id.loginFragment) {
                    Log.i(TAG, "onCreate: Logging Out")
                    supportActionBar?.hide()
                    controller.graph.startDestination = R.id.loginFragment
                    Firebase.auth.signOut()
                }else if (destination.id == R.id.loginFragment) {

                    Log.i(TAG, "onCreate: Changing title")
                    supportActionBar?.title = "Chat"
                }
            }
            updateNavHeader()
        }
    }

    private fun logFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(MyFirebaseMessagingService.TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.i(MyFirebaseMessagingService.TAG, "onNewToken: ${token.toString()}")

            // Log and toast
            // val msg = getString(R.string.msg_token_fmt, token)
        })
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

        if(user!=null) {
            updateActiveStatus(true)
            navController.addOnDestinationChangedListener(listener!!)
        }
    }

    override fun onPause() {
        super.onPause()

        if(user!=null) {
            updateActiveStatus(false)
            navController.removeOnDestinationChangedListener(listener!!)
        }
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController)
                || super.onOptionsItemSelected(item)
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }

    private fun setUpShareButton() {
        val shareMenuItem = binding.navView.menu.findItem(R.id.nav_share)
        shareMenuItem.setOnMenuItemClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
                    .putExtra(Intent.EXTRA_TEXT, "Hey, Check out this Messenger app today!")
            Log.i(TAG, "onOptionsItemSelected: Sharing is caring")
            startActivity(shareIntent)
            it.isCheckable = false
            return@setOnMenuItemClickListener true
        }
    }


    private fun updateNavHeader() {
        val img = header.findViewById<ShapeableImageView>(R.id.imageprofile_picture)
        val tvName = header.findViewById<TextView>(R.id.textView_name)
        val tvStatus = header.findViewById<TextView>(R.id.textView_status)

        Firebase.firestore.collection("users")
            .document(Firebase.auth.currentUser!!.uid)
            .addSnapshotListener{it,e ->
                if (e != null) {
                    Log.w(ChatFragment.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                Glide.with(this)
                    .load(it?.get("profilePictureUrl").toString())
                    .into(img)

                tvName.text = it?.get("name").toString()
                tvStatus.text = it?.get("status").toString()
                }
    }


    companion object{
        const val TAG = "MainActivity"
    }
}