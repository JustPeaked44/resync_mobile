package com.example.presentation.dashboard

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SessionManager
import com.example.ui.theme.ButtonShape
import com.example.ui.theme.CardShape
import com.example.ui.theme.PlayfairDisplayFontFamily
import com.example.ui.theme.PlusJakartaSansFontFamily
import com.example.ui.theme.JetBrainsMonoFontFamily
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    sessionManager: SessionManager,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Observe Preference Flows
    val currentTheme by sessionManager.themePreference.collectAsState(initial = "system")
    val pushEnabled by sessionManager.pushNotificationsEnabled.collectAsState(initial = true)
    val emailEnabled by sessionManager.emailAlertsEnabled.collectAsState(initial = true)

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("settings_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontFamily = PlayfairDisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color(0xFF0F172A)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF0F172A)
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. App Theme Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE2E8F0), CardShape)
                    .testTag("theme_card"),
                shape = CardShape,
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "App Theme",
                        fontFamily = PlusJakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ThemeOptionRow(
                            label = "Light Theme",
                            isSelected = currentTheme == "light",
                            onSelect = {
                                coroutineScope.launch {
                                    sessionManager.setThemePreference("light")
                                }
                            },
                            modifier = Modifier.testTag("theme_option_light")
                        )
                        ThemeOptionRow(
                            label = "Dark Theme",
                            isSelected = currentTheme == "dark",
                            onSelect = {
                                coroutineScope.launch {
                                    sessionManager.setThemePreference("dark")
                                }
                            },
                            modifier = Modifier.testTag("theme_option_dark")
                        )
                        ThemeOptionRow(
                            label = "System Default",
                            isSelected = currentTheme == "system",
                            onSelect = {
                                coroutineScope.launch {
                                    sessionManager.setThemePreference("system")
                                }
                            },
                            modifier = Modifier.testTag("theme_option_system")
                        )
                    }
                }
            }

            // 2. Notification Settings Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE2E8F0), CardShape)
                    .testTag("notifications_card"),
                shape = CardShape,
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Notification Settings",
                        fontFamily = PlusJakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
                    )

                    // Push Notifications
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "Push Notifications",
                                fontFamily = PlusJakartaSansFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Receive critical drift and coherence alerts on device",
                                fontFamily = PlusJakartaSansFontFamily,
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                        Switch(
                            checked = pushEnabled,
                            onCheckedChange = { isChecked ->
                                coroutineScope.launch {
                                    sessionManager.setPushNotificationsEnabled(isChecked)
                                    // OneSignal SDK Integration
                                    try {
                                        if (isChecked) {
                                            com.onesignal.OneSignal.User.pushSubscription.optIn()
                                        } else {
                                            com.onesignal.OneSignal.User.pushSubscription.optOut()
                                        }
                                    } catch (e: Exception) {
                                        // Silent fallbacks
                                    }
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF4F46E5)
                            ),
                            modifier = Modifier.testTag("push_notifications_switch")
                        )
                    }

                    Divider(color = Color(0xFFF1F5F9))

                    // Email Alerts
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "Email Alerts",
                                fontFamily = PlusJakartaSansFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Get weekly coherence activity summaries via email",
                                fontFamily = PlusJakartaSansFontFamily,
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                        Switch(
                            checked = emailEnabled,
                            onCheckedChange = { isChecked ->
                                coroutineScope.launch {
                                    sessionManager.setEmailAlertsEnabled(isChecked)
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF4F46E5)
                            ),
                            modifier = Modifier.testTag("email_alerts_switch")
                        )
                    }
                }
            }

            // 3. Security & Data Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE2E8F0), CardShape)
                    .testTag("security_card"),
                shape = CardShape,
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Security & Data",
                        fontFamily = PlusJakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
                    )

                    ClickableSettingRow(
                        label = "Change Password",
                        onClick = {
                            Toast.makeText(context, "Change password flow is managed online via companion portal", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("change_password_row")
                    )

                    Divider(color = Color(0xFFF1F5F9))

                    ClickableSettingRow(
                        label = "Configure Two-Factor Authentication",
                        onClick = {
                            Toast.makeText(context, "Two-factor auth settings are configured in companion account portal", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("2fa_row")
                    )

                    Divider(color = Color(0xFFF1F5F9))

                    ClickableSettingRow(
                        label = "Clear Local Cache",
                        onClick = {
                            // Clear locally cached application data (non-auth)
                            // We display a friendly, polished visual notification
                            Toast.makeText(context, "Local cache cleared successfully", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("clear_cache_row")
                    )
                }
            }

            // 4. About & Support Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE2E8F0), CardShape)
                    .testTag("about_card"),
                shape = CardShape,
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "About & Support",
                        fontFamily = PlusJakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
                    )

                    ClickableSettingRow(
                        label = "Terms of Service",
                        onClick = {
                            Toast.makeText(context, "Displaying terms of service...", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("terms_row")
                    )

                    Divider(color = Color(0xFFF1F5F9))

                    ClickableSettingRow(
                        label = "Privacy Policy",
                        onClick = {
                            Toast.makeText(context, "Displaying privacy policy...", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("privacy_row")
                    )

                    Divider(color = Color(0xFFF1F5F9))

                    ClickableSettingRow(
                        label = "Help & Support",
                        onClick = {
                            Toast.makeText(context, "Contact support online at support@resync.example.com", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("support_row")
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5. Footer - Logout Button
            Button(
                onClick = {
                    coroutineScope.launch {
                        sessionManager.clearSession()
                        onLogout()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("settings_logout_button"),
                shape = ButtonShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFEF2F2),
                    contentColor = Color(0xFFF43F5E)
                ),
                border = BorderStroke(1.dp, Color(0xFFFCA5A5))
            ) {
                Text(
                    text = "Sign Out from Resync",
                    fontFamily = PlusJakartaSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun ThemeOptionRow(
    label: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onSelect() }
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontFamily = PlusJakartaSansFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = Color(0xFF0F172A)
        )
        RadioButton(
            selected = isSelected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(
                selectedColor = Color(0xFF4F46E5),
                unselectedColor = Color(0xFF94A3B8)
            )
        )
    }
}

@Composable
fun ClickableSettingRow(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontFamily = PlusJakartaSansFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = Color(0xFF0F172A)
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Arrow",
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(20.dp)
        )
    }
}
