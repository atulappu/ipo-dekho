package com.example.ipotracker.presentation.aichat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ipotracker.data.firebase.FirebaseAuthService
import com.example.ipotracker.data.firebase.FirestoreService
import com.example.ipotracker.data.remote.gemini.ChatMessage
import com.example.ipotracker.data.remote.gemini.GeminiModelTier
import com.example.ipotracker.data.remote.gemini.GeminiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AiChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val selectedTier: GeminiModelTier = GeminiModelTier.FLASH,
    val isLoading: Boolean = false,
    val error: String? = null
)

class AiChatViewModel(
    private val geminiService: GeminiService,
    private val firebaseAuthService: FirebaseAuthService,
    private val firestoreService: FirestoreService,
    initialQuery: String? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiChatUiState())
    val uiState: StateFlow<AiChatUiState> = _uiState.asStateFlow()

    init {
        // Welcome message establishing role
        val welcomeMessage = ChatMessage(
            role = "model",
            text = "Namaste! I am **IPODekho AI**, your senior Indian IPO & Stock Market research analyst.\n\n" +
                "I can assist you with:\n" +
                "• **GMP Analysis & Estimates**: Expected listing gains and Grey Market trends.\n" +
                "• **Subscription Insights**: QIB, NII, and Retail bidding momentum.\n" +
                "• **Valuation & Fundamentals**: P/E multiples, RoNW, PAT margins from DRHP/RHP.\n" +
                "• **Allotment Verification**: Step-by-step registrar check tips (Link Intime, KFintech).\n\n" +
                "Select your model tier above or ask any IPO question below!",
            modelTier = GeminiModelTier.FLASH
        )
        _uiState.update { it.copy(messages = listOf(welcomeMessage)) }

        if (!initialQuery.isNullOrBlank()) {
            sendMessage(initialQuery)
        }
    }

    fun setModelTier(tier: GeminiModelTier) {
        _uiState.update { it.copy(selectedTier = tier) }
    }

    fun sendMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isBlank() || _uiState.value.isLoading) return

        val userMessage = ChatMessage(
            role = "user",
            text = trimmed,
            modelTier = _uiState.value.selectedTier
        )

        val currentMessages = _uiState.value.messages + userMessage
        _uiState.update {
            it.copy(
                messages = currentMessages,
                isLoading = true,
                error = null
            )
        }

        // Persist user turn to Firestore
        val userId = firebaseAuthService.currentUser.value?.uid ?: "guest"
        viewModelScope.launch {
            firestoreService.persistChatMessage(userId, userMessage)
        }

        viewModelScope.launch {
            val result = geminiService.sendChatMessage(
                history = currentMessages,
                newMessage = trimmed,
                tier = _uiState.value.selectedTier
            )

            result.onSuccess { replyText ->
                val modelMessage = ChatMessage(
                    role = "model",
                    text = replyText,
                    modelTier = _uiState.value.selectedTier
                )
                _uiState.update {
                    it.copy(
                        messages = it.messages + modelMessage,
                        isLoading = false
                    )
                }
                firestoreService.persistChatMessage(userId, modelMessage)
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = err.message ?: "Failed to generate AI response. Please try again."
                    )
                }
            }
        }
    }

    fun clearChat() {
        val resetMessage = ChatMessage(
            role = "model",
            text = "Conversation reset. How can I assist with your Indian IPO analysis today?",
            modelTier = _uiState.value.selectedTier
        )
        _uiState.update { it.copy(messages = listOf(resetMessage), error = null) }
    }

    companion object {
        fun provideFactory(
            geminiService: GeminiService,
            firebaseAuthService: FirebaseAuthService,
            firestoreService: FirestoreService,
            initialQuery: String? = null
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AiChatViewModel(
                    geminiService,
                    firebaseAuthService,
                    firestoreService,
                    initialQuery
                ) as T
            }
        }
    }
}
