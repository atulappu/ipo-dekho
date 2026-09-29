package com.example.ipotracker.presentation.applicationinfo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ipotracker.domain.repository.IpoRepository
import com.example.ui.theme.*

// Theme colors specifically matched to the dark navy screenshot reference
private val DarkNavyBackground = Color(0xFF080D1A)
private val FormCardBackground = Color(0xFF0D1728)
private val FormCardBorder = Color(0xFF1E2E48)
private val InputFieldBackground = Color(0xFF131D31)
private val InputFieldBorder = Color(0xFF2E3E56)
private val PlaceholderGray = Color(0xFF64748B)
private val RadioUnselectedColor = Color(0xFF64748B)
private val BottomBarBackground = Color(0xFF080D1A)
private val SaveButtonNavy = Color(0xFF111E33)
private val SaveButtonBorder = Color(0xFF2A3D58)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IpoApplicationInfoScreen(
    repository: IpoRepository,
    ipoId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // State Variables
    var applicantName by remember { mutableStateOf("") }
    var panNumber by remember { mutableStateOf("") }
    var applyForIpo by remember { mutableStateOf(true) }

    // 1. Depository Type: CDSL / NSDL (Mutually exclusive, CDSL default)
    var depositoryType by remember { mutableStateOf("CDSL") }

    // 2. Add Bank Details for Form Print: YES / NO (Default: NO)
    var addBankDetails by remember { mutableStateOf(false) }
    var bankName by remember { mutableStateOf("") }
    var bankAccountNumber by remember { mutableStateOf("") }

    // UPI ID
    var upiId by remember { mutableStateOf("") }

    // Form Print Dialog Preview State
    var showFormPrintDialog by remember { mutableStateOf(false) }

    // Handle Form Validation & Submission
    fun handleSaveAndContinue() {
        // 1. Validate Applicant Name
        if (applicantName.trim().isBlank()) {
            Toast.makeText(context, "Please enter Applicant Name.", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Validate PAN Number
        val cleanPan = panNumber.trim().uppercase()
        if (cleanPan.isBlank()) {
            Toast.makeText(context, "Please enter PAN Number.", Toast.LENGTH_SHORT).show()
            return
        }
        val panRegex = Regex("^[A-Z]{5}[0-9]{4}[A-Z]$")
        if (cleanPan.length != 10 || !cleanPan.matches(panRegex)) {
            Toast.makeText(context, "Please enter a valid 10-character PAN (e.g. ABCDE1234F).", Toast.LENGTH_SHORT).show()
            return
        }

        // 3. Validate Depository Type
        if (depositoryType.isBlank()) {
            Toast.makeText(context, "Please select Depository Type (CDSL or NSDL).", Toast.LENGTH_SHORT).show()
            return
        }

        // 4. Conditional Validation if Bank Details = YES
        if (addBankDetails) {
            if (bankName.trim().isBlank()) {
                Toast.makeText(context, "Please enter Bank Name.", Toast.LENGTH_SHORT).show()
                return
            }
            if (bankAccountNumber.trim().isBlank()) {
                Toast.makeText(context, "Please enter Bank Account Number.", Toast.LENGTH_SHORT).show()
                return
            }
            if (!bankAccountNumber.all { it.isDigit() } || bankAccountNumber.length < 9) {
                Toast.makeText(context, "Please enter a valid numeric Bank Account Number (minimum 9 digits).", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // 5. Validate UPI ID where applicable
        if (applyForIpo && upiId.isNotBlank() && !upiId.contains("@")) {
            Toast.makeText(context, "Please enter a valid UPI ID (e.g. username@bank).", Toast.LENGTH_SHORT).show()
            return
        }

        // All validation passed: Show dynamically generated Form Print preview dialog
        showFormPrintDialog = true
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBackground),
        containerColor = DarkNavyBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add Demat Account",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.youtube.com/results?search_query=how+to+apply+ipo+via+upi")
                            )
                            context.startActivity(intent)
                        },
                        modifier = Modifier.testTag("top_video_help_button")
                    ) {
                        Surface(
                            color = Color(0xFFDC2626),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircle,
                                    contentDescription = "Help Video",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkNavyBackground)
            )
        },
        bottomBar = {
            Surface(
                color = BottomBarBackground,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, FormCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Button(
                        onClick = { handleSaveAndContinue() },
                        colors = ButtonDefaults.buttonColors(containerColor = SaveButtonNavy),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, SaveButtonBorder),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("save_and_continue_button")
                    ) {
                        Text(
                            text = "SAVE & CONTINUE",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Dual Action Buttons: GUIDE / HELP (Blue) & VIDEO HELP (Red)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            Toast.makeText(
                                context,
                                "Enter your Applicant Name, PAN, Depository (CDSL/NSDL) and optional Bank details to auto-generate the IPO bidding form.",
                                Toast.LENGTH_LONG
                            ).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryBlue),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .testTag("guide_help_button")
                    ) {
                        Text(
                            text = "GUIDE / HELP",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.youtube.com/results?search_query=how+to+apply+ipo+via+upi")
                            )
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .testTag("video_help_button")
                    ) {
                        Text(
                            text = "VIDEO HELP",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            color = Color.White
                        )
                    }
                }
            }

            // Form Container Card with thin border & dark navy background
            item {
                Surface(
                    color = FormCardBackground,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, FormCardBorder),
                    shadowElevation = 0.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // 1. Applicant Name
                        Column {
                            Text(
                                text = "Applicant Name",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.5.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = applicantName,
                                onValueChange = { applicantName = it },
                                placeholder = {
                                    Text(
                                        text = "e.g. Rajesh Kumar Sharma",
                                        fontSize = 12.5.sp,
                                        color = PlaceholderGray
                                    )
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("applicant_name_field"),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = InputFieldBackground,
                                    unfocusedContainerColor = InputFieldBackground,
                                    focusedBorderColor = PrimaryOrange,
                                    unfocusedBorderColor = InputFieldBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    cursorColor = PrimaryOrange
                                )
                            )
                        }

                        // 2. PAN Number
                        Column {
                            Text(
                                text = "PAN Number",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.5.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = panNumber,
                                onValueChange = { panNumber = it.uppercase().take(10) },
                                placeholder = {
                                    Text(
                                        text = "e.g. ABCDE1234F",
                                        fontSize = 12.5.sp,
                                        color = PlaceholderGray
                                    )
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Characters,
                                    imeAction = ImeAction.Next
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("pan_number_field"),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = InputFieldBackground,
                                    unfocusedContainerColor = InputFieldBackground,
                                    focusedBorderColor = PrimaryOrange,
                                    unfocusedBorderColor = InputFieldBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    cursorColor = PrimaryOrange
                                )
                            )
                        }

                        // 3. Do you want to add Demat details?
                        Column {
                            Text(
                                text = "Do you want to add Demat details?",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.5.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { applyForIpo = true }
                                    .testTag("apply_for_ipo_yes_radio")
                            ) {
                                RadioButton(
                                    selected = applyForIpo,
                                    onClick = { applyForIpo = true },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = PrimaryOrange,
                                        unselectedColor = RadioUnselectedColor
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Yes, I want to apply for the IPO",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    color = Color(0xFFE2E8F0)
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { applyForIpo = false }
                                    .testTag("apply_for_ipo_no_radio")
                            ) {
                                RadioButton(
                                    selected = !applyForIpo,
                                    onClick = { applyForIpo = false },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = PrimaryOrange,
                                        unselectedColor = RadioUnselectedColor
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "No, I just want to check allotment status",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    color = Color(0xFFE2E8F0)
                                )
                            }
                        }

                        // 4. Select Depository Type: [ CDSL ] [ NSDL ]
                        Column {
                            Text(
                                text = "Select Depository Type",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.5.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // CDSL Option Card
                                val isCdslSelected = depositoryType == "CDSL"
                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(
                                        if (isCdslSelected) 1.5.dp else 1.dp,
                                        if (isCdslSelected) PrimaryOrange else Color(0xFFCBD5E1)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clickable { depositoryType = "CDSL" }
                                        .testTag("depository_cdsl_button")
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = isCdslSelected,
                                            onClick = { depositoryType = "CDSL" },
                                            colors = RadioButtonDefaults.colors(
                                                selectedColor = PrimaryOrange,
                                                unselectedColor = Color(0xFF94A3B8)
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "CDSL",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color(0xFF0F172A)
                                        )
                                    }
                                }

                                // NSDL Option Card
                                val isNsdlSelected = depositoryType == "NSDL"
                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(
                                        if (isNsdlSelected) 1.5.dp else 1.dp,
                                        if (isNsdlSelected) PrimaryOrange else Color(0xFFCBD5E1)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clickable { depositoryType = "NSDL" }
                                        .testTag("depository_nsdl_button")
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = isNsdlSelected,
                                            onClick = { depositoryType = "NSDL" },
                                            colors = RadioButtonDefaults.colors(
                                                selectedColor = PrimaryOrange,
                                                unselectedColor = Color(0xFF94A3B8)
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "NSDL",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color(0xFF0F172A)
                                        )
                                    }
                                }
                            }
                        }

                        // 5. Add Bank Details for Form Print? (YES / NO, Default: NO)
                        Column {
                            Text(
                                text = "Add Bank Details for Form Print?",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(28.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Yes Radio
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clickable { addBankDetails = true }
                                        .testTag("bank_details_yes_radio")
                                ) {
                                    RadioButton(
                                        selected = addBankDetails,
                                        onClick = { addBankDetails = true },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = PrimaryOrange,
                                            unselectedColor = RadioUnselectedColor
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "Yes",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                        color = Color(0xFFE2E8F0)
                                    )
                                }

                                // No Radio
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clickable { addBankDetails = false }
                                        .testTag("bank_details_no_radio")
                                ) {
                                    RadioButton(
                                        selected = !addBankDetails,
                                        onClick = { addBankDetails = false },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = PrimaryOrange,
                                            unselectedColor = RadioUnselectedColor
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "No",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                        color = Color(0xFFE2E8F0)
                                    )
                                }
                            }
                        }

                        // Conditional Bank Details Fields (visible ONLY when addBankDetails == true)
                        AnimatedVisibility(
                            visible = addBankDetails,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                // Bank Name (For Form Print)
                                Column {
                                    Text(
                                        text = "Bank Name (For Form Print)",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 12.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedTextField(
                                        value = bankName,
                                        onValueChange = { bankName = it },
                                        placeholder = {
                                            Text(
                                                text = "Enter bank name",
                                                fontSize = 12.5.sp,
                                                color = PlaceholderGray
                                            )
                                        },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(
                                            capitalization = KeyboardCapitalization.Words,
                                            imeAction = ImeAction.Next
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("bank_name_field"),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = InputFieldBackground,
                                            unfocusedContainerColor = InputFieldBackground,
                                            focusedBorderColor = PrimaryOrange,
                                            unfocusedBorderColor = InputFieldBorder,
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            cursorColor = PrimaryOrange
                                        )
                                    )
                                }

                                // Bank Account Number (For Form Print)
                                Column {
                                    Text(
                                        text = "Bank Account Number (For Form Print)",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 12.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedTextField(
                                        value = bankAccountNumber,
                                        onValueChange = { input ->
                                            // Numeric only, reasonable max length up to 18 digits
                                            if (input.all { it.isDigit() }) {
                                                bankAccountNumber = input.take(18)
                                            }
                                        },
                                        placeholder = {
                                            Text(
                                                text = "Enter bank account number",
                                                fontSize = 12.5.sp,
                                                color = PlaceholderGray
                                            )
                                        },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Number,
                                            imeAction = ImeAction.Next
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("bank_account_number_field"),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = InputFieldBackground,
                                            unfocusedContainerColor = InputFieldBackground,
                                            focusedBorderColor = PrimaryOrange,
                                            unfocusedBorderColor = InputFieldBorder,
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            cursorColor = PrimaryOrange
                                        )
                                    )
                                }
                            }
                        }

                        // 6. UPI ID (BHIM, GPay, PhonePe)
                        Column {
                            Text(
                                text = "UPI Id (BHIM, GPay, PhonePe)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.5.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = upiId,
                                onValueChange = { upiId = it.trim() },
                                placeholder = {
                                    Text(
                                        text = "username@okhdfcbank / @ibl",
                                        fontSize = 12.5.sp,
                                        color = PlaceholderGray
                                    )
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Done
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("upi_id_field"),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = InputFieldBackground,
                                    unfocusedContainerColor = InputFieldBackground,
                                    focusedBorderColor = PrimaryOrange,
                                    unfocusedBorderColor = InputFieldBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    cursorColor = PrimaryOrange
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    // Dynamic Form Print Dialog
    if (showFormPrintDialog) {
        val formPrintText = remember(applicantName, panNumber, depositoryType, addBankDetails, bankName, bankAccountNumber, upiId) {
            generateFormPrintText(
                applicantName = applicantName,
                panNumber = panNumber,
                applyForIpo = applyForIpo,
                depositoryType = depositoryType,
                addBankDetails = addBankDetails,
                bankName = bankName,
                bankAccountNumber = bankAccountNumber,
                upiId = upiId
            )
        }

        AlertDialog(
            onDismissRequest = { showFormPrintDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Print,
                        contentDescription = null,
                        tint = PrimaryOrange,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Form Print Preview",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Your Demat profile has been validated. Below is the generated Form Print output reflecting your selections:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Surface(
                        color = TableHeaderBg,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, BorderLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = formPrintText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp
                            ),
                            modifier = Modifier.padding(10.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (addBankDetails) {
                        Text(
                            text = "✓ Bank Name & Account Number included for Form Print.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MarketGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    } else {
                        Text(
                            text = "✓ Bank details excluded from Form Print (User selected No).",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeutralGray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        shareFormPrint(context, formPrintText)
                        Toast.makeText(context, "Demat profile saved and form generated!", Toast.LENGTH_SHORT).show()
                        showFormPrintDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.testTag("form_print_share_button")
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("PRINT / SHARE", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        Toast.makeText(context, "Demat profile saved!", Toast.LENGTH_SHORT).show()
                        showFormPrintDialog = false
                        onNavigateBack()
                    },
                    modifier = Modifier.testTag("form_print_done_button")
                ) {
                    Text("DONE", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NeutralGray)
                }
            },
            shape = RoundedCornerShape(12.dp)
        )
    }
}

/**
 * Builds the official IPO Application Form Print document string
 * Strictly includes Bank Details ONLY when addBankDetails == true
 */
fun generateFormPrintText(
    applicantName: String,
    panNumber: String,
    applyForIpo: Boolean,
    depositoryType: String,
    addBankDetails: Boolean,
    bankName: String,
    bankAccountNumber: String,
    upiId: String
): String {
    val builder = StringBuilder()
    builder.appendLine("=========================================")
    builder.appendLine("        IPO APPLICATION FORM PRINT       ")
    builder.appendLine("=========================================")
    builder.appendLine("Applicant Name : ${applicantName.trim()}")
    builder.appendLine("PAN Number     : ${panNumber.trim().uppercase()}")
    builder.appendLine("Mode           : ${if (applyForIpo) "Apply for IPO" else "Check Allotment"}")
    builder.appendLine("Depository     : $depositoryType")

    if (addBankDetails) {
        builder.appendLine("Bank Name      : ${bankName.trim()}")
        builder.appendLine("Bank A/C No.   : ${bankAccountNumber.trim()}")
    }

    if (upiId.isNotBlank()) {
        builder.appendLine("UPI ID         : ${upiId.trim()}")
    }
    builder.append("=========================================")
    return builder.toString()
}

/**
 * Sends form print to Android print/share intent
 */
private fun shareFormPrint(context: Context, formPrintText: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, formPrintText)
        putExtra(Intent.EXTRA_TITLE, "IPO Application Form Print")
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Print or Share IPO Form")
    context.startActivity(shareIntent)
}
