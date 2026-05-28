package com.example.bob.data.androidtools.test

import com.example.bob.core.tools.BobTool
import com.example.bob.core.tools.ToolResult

class EchoTool : BobTool {

    override val name: String = "echo_tool"

    override val description: String =
        "Repeats back the provided text."

    override val requiresConfirmation: Boolean = false

    override val requiredPermissions: List<String> =
        emptyList()

    override suspend fun execute(
        arguments: Map<String, String>
    ): ToolResult {

        val text = arguments["text"].orEmpty()

        return ToolResult(
            success = true,
            content = "Echo tool result: $text"
        )
    }
}