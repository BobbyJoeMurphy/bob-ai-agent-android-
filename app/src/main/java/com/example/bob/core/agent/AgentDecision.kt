package com.example.bob.core.agent

import com.example.bob.core.tools.ToolCall

sealed class AgentDecision {
    data class FinalResponse(
        val message: String
    ) : AgentDecision()

    data class ToolRequest(
        val toolCall: ToolCall
    ) : AgentDecision()

    data class RequiresConfirmation(
        val message: String,
        val toolCall: ToolCall
    ) : AgentDecision()
}