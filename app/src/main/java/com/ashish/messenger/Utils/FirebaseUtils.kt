package com.ashish.messenger.Utils

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

fun getUId(): String {
    return Firebase.auth.currentUser?.uid.toString()
}
