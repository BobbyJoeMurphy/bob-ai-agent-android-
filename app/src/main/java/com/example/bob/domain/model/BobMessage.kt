package com.example.bob.domain.model

data class BobMessage(
    val id: String,
    val content: String,
    val sender: MessageSender,
    val timestamp: Long = System.currentTimeMillis()
)

enum class MessageSender {
    USER,
    BOB
}