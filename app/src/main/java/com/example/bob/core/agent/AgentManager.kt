package com.example.bob.core.agent

import com.example.bob.core.llm.LlmClient
import com.example.bob.core.tools.ToolRegistry
import com.example.bob.domain.model.ConversationMessage

class AgentManager(
    private val llmClient: LlmClient,
    private val toolRegistry: ToolRegistry = ToolRegistry()
) {
    suspend fun handleMessage(
        message: String,
        history: List<ConversationMessage>
    ): AgentResult {
        val firstDecision = llmClient.getDecision(message, history)

        return handleDecision(
            originalMessage = message,
            decision = firstDecision,
            history = history,
            depth = 0
        )
    }

    private suspend fun handleDecision(
        originalMessage: String,
        decision: AgentDecision,
        history: List<ConversationMessage>,
        depth: Int
    ): AgentResult {
        if (depth >= 2) {
            return AgentResult.Error("I reached my action limit for this request.")
        }

        return when (decision) {

            is AgentDecision.FinalResponse -> {
                AgentResult.FinalResponse(decision.message)
            }

            is AgentDecision.RequiresConfirmation -> {
                AgentResult.ConfirmationRequired(
                    message = decision.message,
                    toolCall = decision.toolCall
                )
            }

            is AgentDecision.ToolRequest -> {
                val tool = toolRegistry.getTool(decision.toolCall.name)

                when {
                    tool == null -> {
                        AgentResult.Error("I don’t have access to that tool yet.")
                    }

                    tool.requiresConfirmation -> {
                        AgentResult.ConfirmationRequired(
                            message = "I need your confirmation before I can do that.",
                            toolCall = decision.toolCall
                        )
                    }

                    else -> {
                        val toolResult = toolRegistry.execute(decision.toolCall)

                        val nextDecision = llmClient.getDecisionAfterTool(
                            originalMessage = originalMessage,
                            toolCall = decision.toolCall,
                            toolResult = toolResult,
                            history = history
                        )

                        handleDecision(
                            originalMessage = originalMessage,
                            decision = nextDecision,
                            history = history,
                            depth = depth + 1
                        )
                    }
                }
            }
        }
    }

    suspend fun executeTool(
        toolCall: com.example.bob.core.tools.ToolCall
    ): com.example.bob.core.tools.ToolResult {
        return toolRegistry.execute(toolCall)
    }
}