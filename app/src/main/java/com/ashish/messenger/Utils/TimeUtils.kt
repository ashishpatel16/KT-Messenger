package com.ashish.messenger.Utils

import android.content.Context
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*


fun getCurrentTime(): String {
    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
    val currentDate = sdf.format(Date())
    return currentDate.toString()
}

fun getTimeId(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS")
    val currentDate = sdf.format(Date())
    return currentDate.toString()
}

