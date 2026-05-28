package com.example.bob.feature.chat

import com.example.bob.core.tools.ToolCall
import com.example.bob.domain.model.BobMessage

data class ChatUiState(
    val messages: List<BobMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val pendingConfirmation: ToolCall? = null
)