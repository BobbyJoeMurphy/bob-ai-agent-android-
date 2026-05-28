package com.example.bob.domain.usecase

import com.example.bob.core.agent.AgentManager
import com.example.bob.core.agent.AgentResult
import com.example.bob.domain.model.ConversationMessage

class SendUserMessageUseCase(
    private val agentManager: AgentManager
) {
    suspend fun executeTool(
        toolCall: com.example.bob.core.tools.ToolCall
    ): com.example.bob.core.tools.ToolResult {
        return agentManager.executeTool(toolCall)
    }

    suspend operator fun invoke(
        message: String,
        history: List<ConversationMessage>
    ): AgentResult {
        return agentManager.handleMessage(
            message = message,
            history = history
        )
    }
}