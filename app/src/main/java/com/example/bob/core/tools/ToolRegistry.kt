package com.example.bob.core.tools

class ToolRegistry(
    tools: List<BobTool> = emptyList()
) {
    private val toolMap = tools.associateBy { it.name }

    fun getTool(name: String): BobTool? {
        return toolMap[name]
    }

    fun getAllTools(): List<BobTool> {
        return toolMap.values.toList()
    }

    suspend fun execute(toolCall: ToolCall): ToolResult {
        val tool = toolMap[toolCall.name]
            ?: return ToolResult(
                success = false,
                content = "Tool not found: ${toolCall.name}"
            )

        return tool.execute(toolCall.arguments)
    }
}