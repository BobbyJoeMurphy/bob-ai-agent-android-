package com.example.bob.core.agent

import com.example.bob.core.tools.ToolResult

data class AgentStepResult(
    val decision: AgentDecision,
    val toolResult: ToolResult? = null
)