package com.example.network

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import com.example.BuildConfig

// --- Gemini Request Data Classes (Moshi) ---
data class GeminiPart(
    val text: String
)

data class GeminiContent(
    val parts: List<GeminiPart>
)

data class GeminiGenerationConfig(
    val temperature: Float? = null,
    val topP: Float? = null,
    val topK: Int? = null
)

data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GeminiGenerationConfig? = null,
    val systemInstruction: GeminiContent? = null
)

// --- Gemini Response Data Classes (Moshi) ---
data class GeminiCandidate(
    val content: GeminiContent? = null,
    val finishReason: String? = null
)

data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null
)

// --- Retrofit Service ---
interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }

    suspend fun getAiExplanation(questionText: String, subject: String, selectedOption: String, correctOption: String): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return "Gemini API key is not configured in Secrets. Please add GEMINI_API_KEY to your AI Studio Secrets panel."
        }

        val prompt = """
            You are an expert SSC & Competitive Exam Tutor. Explain this question in detail:
            Subject: $subject
            Question: $questionText
            User Selected Option: $selectedOption
            Correct Option: $correctOption

            Please provide:
            1. An analysis of why the correct option is correct.
            2. If it is a mathematics (Quantitative Aptitude) or reasoning question, show step-by-step calculations, shortcuts, and core formulas.
            3. If it is an English question, explain the grammatical rule, vocabulary definitions, or common errors to watch out for.
            4. If the user answered incorrectly, gently point out the common trap they might have fallen into.
            
            Keep the tone encouraging, highly educational, clear, and structured with bold highlights and bullet points.
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))),
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = "You are a professional SSC Exam Coach who explains topics simply and provides quick mental calculation shortcuts.")))
        )

        return try {
            val response = service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "No explanation generated. Please try again."
        } catch (e: Exception) {
            "Error connecting to AI Tutor: ${e.localizedMessage ?: e.message}"
        }
    }

    suspend fun askTutorGeneralQuery(query: String, chatHistory: List<Pair<String, String>>): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return "Gemini API key is not configured. Please add GEMINI_API_KEY in the Secrets panel."
        }

        // Build conversation structure
        val contents = mutableListOf<GeminiContent>()
        
        // Add history turns to maintain context without overloading
        chatHistory.takeLast(4).forEach { (userMsg, aiMsg) ->
            contents.add(GeminiContent(parts = listOf(GeminiPart(text = userMsg))))
            contents.add(GeminiContent(parts = listOf(GeminiPart(text = aiMsg))))
        }
        
        // Add current query
        contents.add(GeminiContent(parts = listOf(GeminiPart(text = query))))

        val systemPrompt = """
            You are "SSC Exam Tutor", an AI companion designed to help students ace competitive exams like SSC CGL, CHSL, MTS, CPO, and Railway NTPC.
            Your capabilities:
            - Solve complex math (Quantitative Aptitude) questions step-by-step with formulas and speed-run shortcuts.
            - Explain general knowledge topics (Polity, History, Geography, General Science) with facts, dates, and mnemonic memory tricks.
            - Teach English grammar rules, idioms, vocabulary, and active/passive or direct/indirect speech conversions.
            - Generate brand-new mock questions with options when a student asks to practice a specific topic.
            
            Strict Guidelines:
            - Keep explanation clear, structured, and friendly.
            - Use Markdown (bold, lists, code block for math/reasoning steps) for premium readability.
            - Never make up facts. If you don't know, suggest checking standard NCERT/SSC guidelines.
        """.trimIndent()

        val request = GeminiRequest(
            contents = contents,
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt)))
        )

        return try {
            val response = service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "I couldn't process that query. Please ask me again."
        } catch (e: Exception) {
            "Failed to reach AI Tutor: ${e.localizedMessage ?: e.message}"
        }
    }
}
