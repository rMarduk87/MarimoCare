package rpt.tool.marimocare.utils.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import rpt.tool.marimocare.BuildConfig
import rpt.tool.marimocare.utils.data.apimodels.ChatRequest
import rpt.tool.marimocare.utils.data.apimodels.ChatResponse

interface OpenAiApi {
    @Headers(
        "Content-Type: application/json",
        "HTTP-Referer: https://rpt.tool",
        "X-Title: Marimo Care"
    )
    @POST("chat/completions")
    suspend fun getChatResponse(
        @Header("Authorization") apiKey: String,
        @Body request: ChatRequest
    ): ChatResponse

    companion object {
        private const val BASE_URL = "https://openrouter.ai/api/v1/"
        private val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        private val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val instance: OpenAiApi by lazy {
            retrofit.create(OpenAiApi::class.java)
        }

        suspend fun getChatResponse(request: ChatRequest): ChatResponse {
            val token = "Bearer ${BuildConfig.OPENAI_API_KEY}"
            return instance.getChatResponse(token, request)
        }
    }
}
