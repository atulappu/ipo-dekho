package com.example.ipotracker.presentation.more

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToDisclaimer: () -> Unit,
    onNavigateToDataSources: () -> Unit,
    onNavigateToSubscription: (() -> Unit)? = null,
    onNavigateToAiChat: () -> Unit = {},
    onNavigateToVoiceLive: () -> Unit = {},
    onNavigateToAuth: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("More & Settings", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // App Branding Header Card
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    shadowElevation = 0.5.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            color = PrimaryOrange,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("₹", color = androidx.compose.ui.graphics.Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("IPODekho", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = PrimaryOrange)
                        Text("Track. Compare. Understand IPOs.", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("v1.0.0 • Made for Indian Retail & HNI Investors", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // AI & Cloud Account Section
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    shadowElevation = 0.5.dp
                ) {
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        MoreMenuItem(
                            icon = Icons.Outlined.AutoAwesome,
                            title = "IPODekho AI Analyst",
                            subtitle = "Multi-turn Gemini chatbot (Pro, Flash & Flash-Lite)",
                            onClick = onNavigateToAiChat
                        )
                        HorizontalDivider(color = BorderLight, thickness = 0.8.dp)
                        MoreMenuItem(
                            icon = Icons.Outlined.GraphicEq,
                            title = "Gemini Live Voice Conversations",
                            subtitle = "Real-time speech discussion via Live API",
                            onClick = onNavigateToVoiceLive
                        )
                        HorizontalDivider(color = BorderLight, thickness = 0.8.dp)
                        MoreMenuItem(
                            icon = Icons.Outlined.CloudSync,
                            title = "Firebase Account & Cloud Sync",
                            subtitle = "Google Sign-In & Firestore data backup",
                            onClick = onNavigateToAuth
                        )
                    }
                }
            }

            // General & Preferences Section
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    shadowElevation = 0.5.dp
                ) {
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        if (onNavigateToSubscription != null) {
                            MoreMenuItem(
                                icon = Icons.Outlined.Leaderboard,
                                title = "IPO Subscriptions (BSE / NSE)",
                                subtitle = "Category multiples, day 1-3 progression & tallies",
                                onClick = onNavigateToSubscription
                            )
                            HorizontalDivider(color = BorderLight, thickness = 0.8.dp)
                        }
                        MoreMenuItem(
                            icon = Icons.Outlined.Settings,
                            title = "Settings & Notifications",
                            subtitle = "Dark mode, alerts & preferences",
                            onClick = onNavigateToSettings
                        )
                        HorizontalDivider(color = BorderLight, thickness = 0.8.dp)
                        MoreMenuItem(
                            icon = Icons.Outlined.Gavel,
                            title = "Financial Disclaimer",
                            subtitle = "Educational purposes & GMP disclaimer",
                            onClick = onNavigateToDisclaimer
                        )
                        HorizontalDivider(color = BorderLight, thickness = 0.8.dp)
                        MoreMenuItem(
                            icon = Icons.Outlined.Storage,
                            title = "Official Data Sources",
                            subtitle = "BSE, NSE, SEBI & Registrars",
                            onClick = onNavigateToDataSources
                        )
                    }
                }
            }

            // Support & Sharing
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    shadowElevation = 0.5.dp
                ) {
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        MoreMenuItem(
                            icon = Icons.Outlined.Share,
                            title = "Share Application",
                            subtitle = "Recommend IPODekho to friends",
                            onClick = {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, "Track live IPOs, subscription status, GMP, and allotment results with IPODekho - Track. Compare. Understand IPOs.!")
                                }
                                context.startActivity(Intent.createChooser(intent, "Share App"))
                            }
                        )
                        HorizontalDivider(color = BorderLight, thickness = 0.8.dp)
                        MoreMenuItem(
                            icon = Icons.Outlined.Info,
                            title = "About IPODekho",
                            subtitle = "Mission and architectural info",
                            onClick = onNavigateToDisclaimer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MoreMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = PrimaryOrange, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
    }
}
