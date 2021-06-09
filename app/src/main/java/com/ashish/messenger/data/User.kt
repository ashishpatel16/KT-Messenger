package com.ashish.messenger.data

data class User(val name:String,
                val phone: String,
                var profilePictureUrl : String = "",
                var status : String = "",
                var dateJoined : String = "",
                var lastSeen : String = "",
                var isOnline : Boolean = false
)
