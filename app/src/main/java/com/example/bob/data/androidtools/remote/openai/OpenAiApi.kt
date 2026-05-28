package com.example.bob.data.androidtools.remote.openai

import com.example.bob.data.androidtools.remote.openai.dto.OpenAiRequest
import com.example.bob.data.androidtools.remote.openai.model.OpenAiResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAiApi {

    @Headers("Content-Type: application/json")
    @POST("v1/responses")
    suspend fun createResponse(
        @Body request: OpenAiRequest
    ): OpenAiResponse
}