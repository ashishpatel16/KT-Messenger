package com.ashish.messenger.data

data class User(val name:String? = null,
                val phone: String? = null,
                var profilePictureUrl : String? = null,
                var status : String? = null,
                var dateJoined : String? = null,
                var lastSeen : String? = null,
                var isOnline : Boolean = false
)
