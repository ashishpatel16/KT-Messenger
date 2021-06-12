package com.ashish.messenger.data

data class RecentMessage(val author: String, val text: String)

data class Conversation (val name: String?,
                         val dateCreated : String ="",
                         val admin: String?,
                         val members: List<String>,
                         val recentMessage: RecentMessage?,
                         val type: Int = 0
                        )

/**
   Type is by default 0, for private chats
    Type 1 is for group conversations
 */