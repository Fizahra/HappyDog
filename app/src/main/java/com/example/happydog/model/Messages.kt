package com.example.happydog.model

data class Messages(
    val sender : String? = "",
    val receiver: String? = "",
    val message: String? = "",
    val time: String? = "",
    val imgUrl: String?  = "",
    ) {
    val id : String get() = "$sender-$receiver-$message-$time-$imgUrl"
}