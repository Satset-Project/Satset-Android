package com.tikorst.satset.message

data class Message(
    val message: String? = null,
    val senderId: String? = null,
    val timestamp: Long? = null
)