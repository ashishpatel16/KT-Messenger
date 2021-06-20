package com.ashish.messenger

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onNewToken(p0: String) {

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.i(TAG, "onNewToken: ${token.toString()}")

            // Log and toast
            // val msg = getString(R.string.msg_token_fmt, token)
        })
        super.onNewToken(p0)
    }

    companion object{
        const val TAG = "FCM"
    }
}