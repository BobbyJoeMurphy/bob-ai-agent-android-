package com.example.bob.data.androidtools.remote.openai.model

data class OpenAiResponse(
    val output: List<OutputItem>
)

data class OutputItem(
    val content: List<ContentItem>
)

data class ContentItem(
    val text: String?
)