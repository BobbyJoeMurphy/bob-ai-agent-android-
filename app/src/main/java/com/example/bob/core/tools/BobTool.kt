package com.example.bob.core.tools

interface BobTool {

    val name: String

    val description: String

    val requiresConfirmation: Boolean

    val requiredPermissions: List<String>

    suspend fun execute(
        arguments: Map<String, String>
    ): ToolResult
}