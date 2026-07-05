package com.example.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardShape
import com.example.ui.theme.PlayfairDisplayFontFamily
import com.example.ui.theme.PlusJakartaSansFontFamily
import com.example.ui.theme.JetBrainsMonoFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: DashboardViewModel,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val name by viewModel.userName.collectAsState()
    val email by viewModel.userEmail.collectAsState()
    val institution by viewModel.userInstitution.collectAsState()
    val role by viewModel.userRole.collectAsState()
    val bio by viewModel.userBio.collectAsState()

    val resolvedName = name ?: "Noel Henry"
    val resolvedEmail = email ?: "noelhenrymier@gmail.com"
    val resolvedInstitution = institution ?: "Stanford University"
    val resolvedRole = role ?: "Principal Investigator"
    val resolvedBio = bio ?: "Specializing in distributed systems coherence and academic reference validation systems."

    // Compute Initials for Avatar
    val initials = resolvedName.split(" ")
        .filter { it.isNotBlank() }
        .map { it.first().uppercaseChar() }
        .joinToString("")
        .take(2)

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("profile_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        fontFamily = PlayfairDisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color(0xFF0F172A)
                    )
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier.testTag("profile_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color(0xFF64748B)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF8FAFC)
                )
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Section: Avatar & Meta Information
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                // Large Centered Circular Avatar
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4F46E5).copy(alpha = 0.08f))
                        .border(2.dp, Color(0xFF4F46E5).copy(alpha = 0.2f), CircleShape)
                        .testTag("profile_avatar"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials.ifEmpty { "NH" },
                        fontFamily = PlayfairDisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp,
                        color = Color(0xFF4F46E5),
                        textAlign = TextAlign.Center
                    )
                }

                // Name and Academic Role
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = resolvedName,
                        fontFamily = PlayfairDisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color(0xFF0F172A),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("profile_full_name")
                    )
                    Text(
                        text = resolvedRole,
                        fontFamily = PlusJakartaSansFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("profile_academic_role_subtitle")
                    )
                }
            }

            // Credential Information Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE2E8F0), CardShape)
                    .testTag("profile_credentials_card"),
                shape = CardShape,
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "User Credentials",
                        fontFamily = PlusJakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
                    )

                    // 1. Full Name Outlined Field
                    ProfileOutlinedField(
                        label = "Full Name",
                        value = resolvedName,
                        modifier = Modifier.testTag("profile_field_name")
                    )

                    // 2. Email Address Outlined Field
                    ProfileOutlinedField(
                        label = "Email Address",
                        value = resolvedEmail,
                        modifier = Modifier.testTag("profile_field_email")
                    )

                    // 3. Academic Institution Outlined Field
                    ProfileOutlinedField(
                        label = "Academic Institution",
                        value = resolvedInstitution,
                        modifier = Modifier.testTag("profile_field_institution")
                    )

                    // 4. Academic Role Outlined Field
                    ProfileOutlinedField(
                        label = "Academic Role",
                        value = resolvedRole,
                        modifier = Modifier.testTag("profile_field_role")
                    )

                    // 5. Brief Research Bio Outlined Field
                    ProfileOutlinedField(
                        label = "Brief Research Bio",
                        value = resolvedBio,
                        singleLine = false,
                        modifier = Modifier.testTag("profile_field_bio")
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileOutlinedField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            fontFamily = PlusJakartaSansFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color(0xFF64748B)
        )
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            singleLine = singleLine,
            maxLines = if (singleLine) 1 else 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF8FAFC),
                unfocusedContainerColor = Color(0xFFF8FAFC),
                disabledContainerColor = Color(0xFFF8FAFC),
                focusedBorderColor = Color(0xFFE2E8F0),
                unfocusedBorderColor = Color(0xFFE2E8F0),
                disabledBorderColor = Color(0xFFE2E8F0),
                focusedTextColor = Color(0xFF0F172A),
                unfocusedTextColor = Color(0xFF0F172A),
                disabledTextColor = Color(0xFF0F172A)
            )
        )
    }
}
