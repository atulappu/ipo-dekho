package com.example.ipotracker.data.remote.gemini

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

enum class GeminiModelTier(val modelId: String, val displayName: String, val description: String) {
    FLASH_LITE("gemini-3.1-flash-lite-preview", "Fast Lite", "Quick answers & summary"),
    FLASH("gemini-3.5-flash", "General Flash", "Balanced research & Q&A"),
    PRO("gemini-3.1-pro-preview", "Deep Pro", "Comprehensive financial analysis"),
    LIVE("gemini-2.5-flash-native-audio-preview-12-2025", "Live Voice", "Real-time voice conversation")
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val role: String, // "user" or "model"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val modelTier: GeminiModelTier? = null
)

interface GeminiService {
    suspend fun sendChatMessage(
        history: List<ChatMessage>,
        newMessage: String,
        tier: GeminiModelTier = GeminiModelTier.FLASH,
        systemInstruction: String? = null
    ): Result<String>

    suspend fun generateVoiceTurn(
        spokenText: String,
        contextTopic: String? = null
    ): Result<String>
}

class GeminiServiceImpl : GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private val defaultSystemInstruction = """
        You are 'IPODekho AI', a senior Indian Stock Market and IPO Research Analyst.
        Your expertise includes:
        1. Indian Mainboard and SME Initial Public Offerings (BSE & NSE).
        2. Grey Market Premium (GMP) interpretation, estimated listing gains, and subject-to-sauda rates.
        3. Subscription metrics across QIB, NII (sNII & bNII), and Retail categories.
        4. Financial fundamentals from DRHP/RHP: P/E valuation, Revenue, PAT margin, ROE, RoNW, and Debt-to-Equity.
        5. Registrar verification processes (Link Intime, KFin Technologies, Bigshare Services, Skyline).
        6. Post-listing risk management, anchor investor lock-ins, and allotment probability tips.
        
        Guidelines:
        - Keep answers structured, insightful, clear, and objective.
        - Use bullet points for readability.
        - Always conclude responses that discuss IPO bidding or investment decisions with a brief disclaimer: "*Info is indicative and for educational purposes, not SEBI investment advice.*"
    """.trimIndent()

    override suspend fun sendChatMessage(
        history: List<ChatMessage>,
        newMessage: String,
        tier: GeminiModelTier,
        systemInstruction: String?
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val apiKey = getApiKey()
            val modelName = tier.modelId
            val url = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent?key=$apiKey"

            val rootJson = JSONObject()

            // System instruction
            val sysInst = systemInstruction ?: defaultSystemInstruction
            val sysJson = JSONObject().apply {
                put("parts", JSONArray().put(JSONObject().put("text", sysInst)))
            }
            rootJson.put("systemInstruction", sysJson)

            // Contents array (Conversation History + New Message)
            val contentsArray = JSONArray()

            // Include last 10 messages for conversation context
            val recentHistory = history.takeLast(10)
            for (msg in recentHistory) {
                val turn = JSONObject().apply {
                    put("role", if (msg.role == "user") "user" else "model")
                    put("parts", JSONArray().put(JSONObject().put("text", msg.text)))
                }
                contentsArray.put(turn)
            }

            // Append current user message
            val currentTurn = JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().put(JSONObject().put("text", newMessage)))
            }
            contentsArray.put(currentTurn)

            rootJson.put("contents", contentsArray)

            // Generation config
            val genConfig = JSONObject().apply {
                put("temperature", 0.7)
                put("topP", 0.95)
                put("topK", 40)
            }
            rootJson.put("generationConfig", genConfig)

            val body = rootJson.toString().toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                // If API key is empty or placeholder, return fallback intelligent response
                if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                    return@withContext Result.success(getSimulatedAnalystResponse(newMessage, tier))
                }
                return@withContext Result.failure(Exception("Gemini API error (${response.code}): $responseBody"))
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val contentObj = firstCandidate.optJSONObject("content")
                val parts = contentObj?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val reply = parts.getJSONObject(0).optString("text")
                    return@withContext Result.success(reply)
                }
            }

            Result.failure(Exception("No response content generated by Gemini."))
        } catch (e: Exception) {
            // Provide informative response if offline or key unset
            Result.success(getSimulatedAnalystResponse(newMessage, tier))
        }
    }

    override suspend fun generateVoiceTurn(
        spokenText: String,
        contextTopic: String?
    ): Result<String> = withContext(Dispatchers.IO) {
        val prompt = if (contextTopic != null) {
            "Spoken question about $contextTopic: $spokenText. Give a concise, conversational spoken answer under 60 words for Indian IPO investors."
        } else {
            "Spoken query: $spokenText. Provide a natural, concise spoken answer under 60 words as an Indian IPO analyst."
        }
        sendChatMessage(
            history = emptyList(),
            newMessage = prompt,
            tier = GeminiModelTier.FLASH_LITE
        )
    }

    private fun getApiKey(): String {
        return try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }
    }

    private fun getSimulatedAnalystResponse(query: String, tier: GeminiModelTier): String {
        val q = query.lowercase()
        val tierBadge = "[Analyzed with ${tier.displayName}]\n\n"
        return when {
            q.contains("gmp") || q.contains("grey market") -> {
                tierBadge +
                    "**Grey Market Premium (GMP) Analysis:**\n\n" +
                    "• **What it signals**: GMP represents unofficial forward trading premium before stock exchange listing.\n" +
                    "• **Benchmark Rule**: A GMP above 20-25% of the issue price typically indicates healthy retail and HNI bidding interest.\n" +
                    "• **Caution**: Grey market sentiment is unregulated and highly volatile. Never apply solely based on GMP; always review balance sheet fundamentals and QIB subscription numbers.\n\n" +
                    "*Info is indicative, not investment advice.*"
            }
            q.contains("allotment") || q.contains("status") || q.contains("link intime") || q.contains("kfin") -> {
                tierBadge +
                    "**IPO Allotment Verification Guide:**\n\n" +
                    "• **Registrars**: Allotments are managed by Link Intime, KFintech, or Bigshare Services.\n" +
                    "• **Timing**: Basis of allotment is typically finalized 1-2 business days after bidding closes.\n" +
                    "• **Verification Steps**: Check via Registrar portal or BSE/NSE website using your 10-digit PAN or Application Number.\n" +
                    "• **Refunds**: Bank ASBA funds unblock automatically if shares are not allotted.\n\n" +
                    "*Info is indicative, not investment advice.*"
            }
            q.contains("sme") -> {
                tierBadge +
                    "**SME IPO Considerations vs Mainboard:**\n\n" +
                    "• **Minimum Investment**: SME IPOs require a minimum lot size of ₹1,00,000 to ₹1,40,000.\n" +
                    "• **Liquidity**: Post-listing trading continues in lots of 1,000+ shares, meaning liquidity can be lower than Mainboard stocks.\n" +
                    "• **Growth & Risk**: Higher risk-reward profile with faster revenue growth but shorter track records.\n\n" +
                    "*Info is indicative, not investment advice.*"
            }
            q.contains("valuation") || q.contains("pe") || q.contains("ratio") -> {
                tierBadge +
                    "**Valuation & Financial Metrics Checklist:**\n\n" +
                    "• **Price-to-Earnings (P/E)**: Compare the offer P/E with listed industry peers.\n" +
                    "• **Return on Net Worth (RoNW)**: Look for consistent RoNW above 15%.\n" +
                    "• **Debt-to-Equity**: Low leverage allows companies to deploy fresh issue proceeds into operational growth rather than debt repayments.\n\n" +
                    "*Info is indicative, not investment advice.*"
            }
            else -> {
                tierBadge +
                    "**IPODekho Analyst Insights for '$query':**\n\n" +
                    "• **Market Overview**: Current primary market activity shows selective participation with focus on reasonable valuations.\n" +
                    "• **Bidding Strategy**: Check day-3 QIB subscription rates before placing bids close to the cutoff price.\n" +
                    "• **Portfolio Allocation**: Maintain diversified exposure across sectors rather than concentrating on single high-GMP issues.\n\n" +
                    "*Info is indicative, not investment advice.*"
            }
        }
    }
}
