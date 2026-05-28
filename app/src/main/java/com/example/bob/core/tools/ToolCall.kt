package com.example.bob.core.tools

data class ToolCall(
    val name: String,
    val arguments: Map<String, String> = emptyMap()
)