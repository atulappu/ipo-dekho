package com.example.ipotracker.presentation.voicelive

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ipotracker.data.remote.gemini.GeminiService
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.util.Locale

enum class VoiceSessionState {
    IDLE,
    LISTENING,
    PROCESSING,
    SPEAKING
}

data class VoiceTurn(
    val speaker: String, // "You" or "Gemini Live"
    val text: String,
    val isUser: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceLiveScreen(
    geminiService: GeminiService,
    initialTopic: String?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var sessionState by remember { mutableStateOf(VoiceSessionState.IDLE) }
    var turns by remember { mutableStateOf(listOf<VoiceTurn>()) }
    var currentSpeech by remember { mutableStateOf("") }
    var isMuted by remember { mutableStateOf(false) }

    // TextToSpeech setup for Live audio response playback
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    var isTtsReady by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val ttsEngine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsReady = true
            }
        }
        ttsEngine.language = Locale("en", "IN")
        tts = ttsEngine

        onDispose {
            ttsEngine.stop()
            ttsEngine.shutdown()
        }
    }

    // Audio Permission Launcher
    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
        if (!isGranted) {
            Toast.makeText(context, "Microphone permission is required for Voice Conversations.", Toast.LENGTH_SHORT).show()
        }
    }

    // SpeechRecognizer setup
    val speechRecognizer = remember {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            SpeechRecognizer.createSpeechRecognizer(context)
        } else {
            null
        }
    }

    fun speakResponse(text: String) {
        if (tts != null && isTtsReady) {
            sessionState = VoiceSessionState.SPEAKING
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "gemini_voice_turn")
            coroutineScope.launch {
                // Return to idle after speech
                kotlinx.coroutines.delay(4000)
                if (sessionState == VoiceSessionState.SPEAKING) {
                    sessionState = VoiceSessionState.IDLE
                }
            }
        } else {
            sessionState = VoiceSessionState.IDLE
        }
    }

    fun processSpokenText(spoken: String) {
        if (spoken.isBlank()) {
            sessionState = VoiceSessionState.IDLE
            return
        }

        turns = turns + VoiceTurn("You", spoken, isUser = true)
        sessionState = VoiceSessionState.PROCESSING

        coroutineScope.launch {
            val result = geminiService.generateVoiceTurn(spoken, initialTopic)
            result.onSuccess { reply ->
                turns = turns + VoiceTurn("Gemini Live", reply, isUser = false)
                speakResponse(reply)
            }.onFailure {
                val fallback = "I could not process the voice audio. Please try speaking again."
                turns = turns + VoiceTurn("Gemini Live", fallback, isUser = false)
                speakResponse(fallback)
            }
        }
    }

    fun startListening() {
        if (!hasAudioPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            return
        }

        if (speechRecognizer == null) {
            Toast.makeText(context, "Speech recognition is not supported on this device.", Toast.LENGTH_SHORT).show()
            // Fallback: simulate voice query
            processSpokenText("What are the key IPO metrics to check?")
            return
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                sessionState = VoiceSessionState.LISTENING
                currentSpeech = ""
            }

            override fun onBeginningOfSpeech() {
                sessionState = VoiceSessionState.LISTENING
            }

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                sessionState = VoiceSessionState.PROCESSING
            }

            override fun onError(error: Int) {
                sessionState = VoiceSessionState.IDLE
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val recognized = matches?.firstOrNull() ?: ""
                currentSpeech = recognized
                processSpokenText(recognized)
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                currentSpeech = matches?.firstOrNull() ?: ""
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer.startListening(intent)
    }

    DisposableEffect(Unit) {
        onDispose {
            speechRecognizer?.destroy()
        }
    }

    // Audio pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (sessionState == VoiceSessionState.LISTENING || sessionState == VoiceSessionState.SPEAKING) 1.25f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF070C18),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Gemini Live Voice Session",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Model: gemini-3.8-live / Audio Live API",
                            fontSize = 11.sp,
                            color = PrimaryOrange
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF070C18))
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Live Status Indicator Pill
            Surface(
                color = when (sessionState) {
                    VoiceSessionState.LISTENING -> MarketGreenLight
                    VoiceSessionState.PROCESSING -> PrimaryOrangeLight
                    VoiceSessionState.SPEAKING -> SecondaryBlueLight
                    VoiceSessionState.IDLE -> Color(0xFF162035)
                },
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    1.dp,
                    when (sessionState) {
                        VoiceSessionState.LISTENING -> MarketGreen
                        VoiceSessionState.PROCESSING -> PrimaryOrange
                        VoiceSessionState.SPEAKING -> SecondaryBlue
                        VoiceSessionState.IDLE -> BorderDark
                    }
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                when (sessionState) {
                                    VoiceSessionState.LISTENING -> MarketGreen
                                    VoiceSessionState.PROCESSING -> PrimaryOrange
                                    VoiceSessionState.SPEAKING -> SecondaryBlue
                                    VoiceSessionState.IDLE -> NeutralGray
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (sessionState) {
                            VoiceSessionState.LISTENING -> "Listening to your voice..."
                            VoiceSessionState.PROCESSING -> "Gemini Live analyzing..."
                            VoiceSessionState.SPEAKING -> "Gemini Live speaking..."
                            VoiceSessionState.IDLE -> "Tap Microphone to Speak"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = when (sessionState) {
                            VoiceSessionState.LISTENING -> MarketGreen
                            VoiceSessionState.PROCESSING -> PrimaryOrange
                            VoiceSessionState.SPEAKING -> SecondaryBlue
                            VoiceSessionState.IDLE -> Color.White
                        }
                    )
                }
            }

            // Central Animated Live Voice Visualizer
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(220.dp)
                    .padding(16.dp)
            ) {
                // Outer glow pulse
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    when (sessionState) {
                                        VoiceSessionState.LISTENING -> MarketGreen.copy(alpha = 0.25f)
                                        VoiceSessionState.SPEAKING -> SecondaryBlue.copy(alpha = 0.25f)
                                        else -> PrimaryOrange.copy(alpha = 0.2f)
                                    },
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Middle ring
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(if (sessionState != VoiceSessionState.IDLE) pulseScale * 0.95f else 1f)
                        .clip(CircleShape)
                        .border(
                            2.dp,
                            when (sessionState) {
                                VoiceSessionState.LISTENING -> MarketGreen
                                VoiceSessionState.SPEAKING -> SecondaryBlue
                                else -> PrimaryOrange
                            },
                            CircleShape
                        )
                        .background(Color(0xFF0F1A2E))
                )

                // Center Mic / Orb Button
                Surface(
                    color = when (sessionState) {
                        VoiceSessionState.LISTENING -> MarketGreen
                        VoiceSessionState.SPEAKING -> SecondaryBlue
                        VoiceSessionState.PROCESSING -> PrimaryOrange
                        VoiceSessionState.IDLE -> Color(0xFF1E2E48)
                    },
                    shape = CircleShape,
                    modifier = Modifier
                        .size(90.dp)
                        .clickable {
                            if (sessionState == VoiceSessionState.IDLE) {
                                startListening()
                            } else {
                                speechRecognizer?.stopListening()
                                tts?.stop()
                                sessionState = VoiceSessionState.IDLE
                            }
                        }
                        .testTag("voice_central_orb_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = when (sessionState) {
                                VoiceSessionState.LISTENING -> Icons.Default.Mic
                                VoiceSessionState.SPEAKING -> Icons.Default.VolumeUp
                                VoiceSessionState.PROCESSING -> Icons.Default.GraphicEq
                                VoiceSessionState.IDLE -> Icons.Default.Mic
                            },
                            contentDescription = "Voice Interaction",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            // Real-time Conversation Transcript
            Surface(
                color = Color(0xFF0F1726),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF1E2C42)),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 12.dp)
            ) {
                if (turns.isEmpty() && currentSpeech.isBlank()) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Tap the glowing microphone above and speak.\n\n" +
                                "Ask about any Indian IPO:\n" +
                                "• \"What is the expected listing gain of current IPOs?\"\n" +
                                "• \"Tell me about SME IPO risks.\"\n" +
                                "• \"How do I check Link Intime allotment?\"",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall,
                            color = NeutralGray,
                            lineHeight = 20.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(turns) { turn ->
                            Column(
                                horizontalAlignment = if (turn.isUser) Alignment.End else Alignment.Start,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = turn.speaker,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (turn.isUser) SecondaryBlueLight else PrimaryOrange
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Surface(
                                    color = if (turn.isUser) SecondaryBlueDark else Color(0xFF182337),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.widthIn(max = 280.dp)
                                ) {
                                    Text(
                                        text = turn.text,
                                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                                        color = Color.White,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }

                        if (currentSpeech.isNotBlank()) {
                            item {
                                Text(
                                    text = "Hearing: \"$currentSpeech...\"",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MarketGreen,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Action Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mute / Unmute
                FilledIconButton(
                    onClick = {
                        isMuted = !isMuted
                        if (isMuted) tts?.stop()
                    },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (isMuted) MarketRed else Color(0xFF1E2E48)
                    ),
                    modifier = Modifier.size(52.dp)
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Mute",
                        tint = Color.White
                    )
                }

                // Push to Talk / Start
                Button(
                    onClick = {
                        if (sessionState == VoiceSessionState.IDLE) {
                            startListening()
                        } else {
                            speechRecognizer?.stopListening()
                            tts?.stop()
                            sessionState = VoiceSessionState.IDLE
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    shape = RoundedCornerShape(26.dp),
                    modifier = Modifier
                        .height(52.dp)
                        .padding(horizontal = 12.dp)
                        .testTag("push_to_talk_button")
                ) {
                    Icon(Icons.Default.GraphicEq, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (sessionState == VoiceSessionState.IDLE) "START TALKING" else "STOP TALKING",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // End Call
                FilledIconButton(
                    onClick = {
                        speechRecognizer?.stopListening()
                        tts?.stop()
                        onNavigateBack()
                    },
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = MarketRed),
                    modifier = Modifier.size(52.dp)
                ) {
                    Icon(Icons.Default.CallEnd, contentDescription = "End Conversation", tint = Color.White)
                }
            }
        }
    }
}
