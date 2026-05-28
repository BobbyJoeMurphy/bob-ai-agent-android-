package com.example.bob.core.llm

import com.example.bob.core.agent.AgentDecision
import com.example.bob.core.tools.ToolCall
import com.example.bob.core.tools.ToolResult
import com.example.bob.domain.model.ConversationMessage

interface LlmClient {

    suspend fun getDecision(
        message: String,
        history: List<ConversationMessage>
    ): AgentDecision

    suspend fun getDecisionAfterTool(
        originalMessage: String,
        toolCall: ToolCall,
        toolResult: ToolResult,
        history: List<ConversationMessage>
    ): AgentDecision

    suspend fun generateToolResponse(
        originalMessage: String,
        toolCall: ToolCall,
        toolResult: ToolResult,
        history: List<ConversationMessage>
    ): String
}