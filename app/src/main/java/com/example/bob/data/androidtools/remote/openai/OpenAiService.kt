package com.example.bob.data.androidtools.remote.openai

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.bob.BuildConfig

object OpenAiService {

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .addHeader(
                    "Authorization",
                    "Bearer ${BuildConfig.OPENAI_API_KEY}"
                )
                .build()

            chain.proceed(request)
        }
        .build()

    val api: OpenAiApi = Retrofit.Builder()
        .baseUrl("https://api.openai.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OpenAiApi::class.java)
}