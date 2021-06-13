package com.ashish.messenger.data

data class Chat(val participant1: String,
                val participant2: String,
                val recentMessage: String?,
                val recentMessageAuthor: String,
                )
