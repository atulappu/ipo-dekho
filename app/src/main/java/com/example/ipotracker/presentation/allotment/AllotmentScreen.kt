package com.example.ipotracker.presentation.allotment

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ipotracker.presentation.detail.DetailKeyValueRow
import com.example.ipotracker.utils.DateUtils
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllotmentScreen(
    viewModel: AllotmentViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var expandedIpoMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("IPO Allotment Status", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = SecondaryBlueLight,
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, SecondaryBlueContainer)
                        ) {
                            Text(
                                text = "CHECKER",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                fontWeight = FontWeight.Bold,
                                color = SecondaryBlue,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(20.dp))
                    }
                },
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
            // Select IPO
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderLight),
                    shadowElevation = 0.5.dp
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "SELECT IPO",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Box {
                            OutlinedButton(
                                onClick = { expandedIpoMenu = true },
                                modifier = Modifier.fillMaxWidth().height(42.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, BorderLight),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = uiState.selectedIpo?.name ?: "Select an IPO",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.5.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = SecondaryBlue)
                                }
                            }

                            DropdownMenu(
                                expanded = expandedIpoMenu,
                                onDismissRequest = { expandedIpoMenu = false },
                                modifier = Modifier.fillMaxWidth(0.9f)
                            ) {
                                uiState.ipos.forEach { ipo ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(ipo.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                                Text(
                                                    "Allotment: ${DateUtils.formatDisplayDate(ipo.allotmentDate)} • Registrar: ${ipo.registrar}",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        },
                                        onClick = {
                                            viewModel.selectIpo(ipo)
                                            expandedIpoMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        if (uiState.selectedIpo != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            DetailKeyValueRow("Assigned Registrar", uiState.registrarName, isAlt = false)
                            DetailKeyValueRow("Allotment Date", DateUtils.formatDisplayDate(uiState.selectedIpo!!.allotmentDate), isAlt = true)
                            DetailKeyValueRow("Status", if (uiState.isAllotmentOut) "Allotment Declared" else "Pending / In Progress", isAlt = false)
                        }
                    }
                }
            }

            // Application Details Input Form
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderLight),
                    shadowElevation = 0.5.dp
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "APPLICANT CREDENTIALS",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = uiState.panNumber,
                            onValueChange = { viewModel.updatePan(it.uppercase()) },
                            label = { Text("PAN Number (e.g. ABCDE1234F)", fontSize = 11.5.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryOrange,
                                unfocusedBorderColor = BorderLight
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = uiState.applicationNumber,
                            onValueChange = { viewModel.updateApplicationNumber(it) },
                            label = { Text("Application Number / DP ID (Optional)", fontSize = 11.5.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryOrange,
                                unfocusedBorderColor = BorderLight
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                val url = uiState.registrarUrl
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryBlue),
                            shape = RoundedCornerShape(8.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("CHECK ON OFFICIAL REGISTRAR PORTAL", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = Color.White)
                        }
                    }
                }
            }

            // Major Registrar Quick Portals
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderLight),
                    shadowElevation = 0.5.dp
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "DIRECT REGISTRAR & EXCHANGE PORTALS",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        RegistrarQuickLink("Link Intime India Pvt Ltd", "https://linkintime.co.in/initial_offer/public-issues.html", context)
                        HorizontalDivider(color = BorderLight, thickness = 0.8.dp)
                        RegistrarQuickLink("KFin Technologies Ltd (Karvy)", "https://ris.kfintech.com/ipostatus/", context)
                        HorizontalDivider(color = BorderLight, thickness = 0.8.dp)
                        RegistrarQuickLink("Bigshare Services Pvt Ltd", "https://www.bigshareonline.com/ipo_Allotment.html", context)
                        HorizontalDivider(color = BorderLight, thickness = 0.8.dp)
                        RegistrarQuickLink("BSE India Allotment Portal", "https://www.bseindia.com/investors/appli_check.aspx", context)
                        HorizontalDivider(color = BorderLight, thickness = 0.8.dp)
                        RegistrarQuickLink("NSE India Verification Portal", "https://www.nseindia.com/products/content/equities/ipos/ipo_login.htm", context)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun RegistrarQuickLink(name: String, url: String, context: android.content.Context) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            }
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = url, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = SecondaryBlue, maxLines = 1)
        }
        Icon(Icons.Default.OpenInNew, contentDescription = null, tint = SecondaryBlue, modifier = Modifier.size(15.dp))
    }
}
