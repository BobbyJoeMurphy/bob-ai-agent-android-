package com.example.bob.domain.model

data class ConversationMessage(
    val role: String,
    val content: String,
    val toolName: String? = null
)