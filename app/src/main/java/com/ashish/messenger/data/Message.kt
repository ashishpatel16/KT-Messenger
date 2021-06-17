package com.ashish.messenger.data

import com.google.firebase.firestore.DocumentId


data class Message(val messageId: String="",
                   val text: String="",
                   val author: String="",
                   val timePosted : String="",
                   val seenBy: List<String>? = null,
                   val mediaUrl: String? = null
                           )

data class MessageList(val messages: List<Message>? = null) {
}
