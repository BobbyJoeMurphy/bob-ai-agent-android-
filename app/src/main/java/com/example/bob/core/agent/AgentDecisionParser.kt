package com.example.bob.core.agent

import com.example.bob.core.tools.ToolCall
import org.json.JSONObject

class AgentDecisionParser {

    fun parse(response: String): AgentDecision {
        return try {
            val cleanedResponse = response
                .replace("```json", "")
                .replace("```", "")
                .trim()

            val json = JSONObject(cleanedResponse)

            val type = json.optString("type")

            when (type) {

                "final_response" -> {
                    AgentDecision.FinalResponse(
                        message = json.optString(
                            "message",
                            "I couldn't understand that response."
                        )
                    )
                }

                "tool_call" -> {
                    parseToolCall(json)
                }

                "requires_confirmation" -> {
                    AgentDecision.RequiresConfirmation(
                        message = json.optString(
                            "message",
                            "Please confirm this action."
                        ),
                        toolCall = buildToolCall(json)
                    )
                }

                else -> {
                    if (json.has("tool")) {
                        parseToolCall(json)
                    } else {
                        AgentDecision.FinalResponse(
                            message = "I couldn't process that properly. Please try again."
                        )
                    }
                }
            }

        } catch (e: Exception) {
            AgentDecision.FinalResponse(
                message = response
            )
        }
    }

    private fun parseToolCall(json: JSONObject): AgentDecision.ToolRequest {
        return AgentDecision.ToolRequest(
            toolCall = buildToolCall(json)
        )
    }

    private fun buildToolCall(json: JSONObject): ToolCall {
        val toolName = json.optString("tool")

        val argsJson = json.optJSONObject("arguments")
        val arguments = mutableMapOf<String, String>()

        argsJson?.keys()?.forEach { key ->
            arguments[key] = argsJson.optString(key)
        }

        return ToolCall(
            name = toolName,
            arguments = arguments
        )
    }
}