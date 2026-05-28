package com.example.bob.core.agent

import com.example.bob.core.tools.ToolCall

sealed class AgentResult {
    data class FinalResponse(
        val message: String
    ) : AgentResult()

    data class ConfirmationRequired(
        val message: String,
        val toolCall: ToolCall
    ) : AgentResult()

    data class Error(
        val message: String
    ) : AgentResult()
}