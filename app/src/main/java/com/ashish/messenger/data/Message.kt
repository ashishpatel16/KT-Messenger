package com.ashish.messenger.data


data class Message(val messageId: String,
                   val text: String,
                   val author: String,
                   val timePosted : String,
                   val seenBy: List<String>,
                   val media: String? = null
                           )

data class MessageList(val messages: List<Message>)
