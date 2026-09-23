package com.example.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PlayfairDisplayFontFamily
import com.example.ui.theme.PlusJakartaSansFontFamily

@Composable
fun LoginScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSignup: () -> Unit,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    val canLogin = email.isNotBlank() && password.isNotEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Top Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0))
                    .clickable { onNavigateBack() }
                    .align(Alignment.CenterStart),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF475569),
                    modifier = Modifier.size(18.dp)
                )
            }

            Row(
                modifier = Modifier.align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color(0xFF2563EB), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("R", color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
                }
                Text(
                    text = "Resync",
                    fontFamily = PlusJakartaSansFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    color = Color(0xFF1E293B)
                )
            }
        }

        // Form Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Welcome back",
                fontFamily = PlayfairDisplayFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = Color(0xFF0F172A)
            )
            Text(
                text = "Sign in to access your scans and results.",
                fontFamily = PlusJakartaSansFontFamily,
                fontSize = 14.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // Social Logins
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Google button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                        .clickable { onLoginSuccess() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("G", color = Color(0xFF4285F4), fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Text("Google", fontFamily = PlusJakartaSansFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF334155))
                    }
                }

                // Facebook button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                        .clickable { onLoginSuccess() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("f", color = Color(0xFF1877F2), fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Text("Facebook", fontFamily = PlusJakartaSansFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF334155))
                    }
                }
            }

            // Divider
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f).height(1.dp).background(Color(0xFFE2E8F0)))
                Text(
                    text = "or continue with email",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp,
                    fontFamily = PlusJakartaSansFontFamily,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Box(modifier = Modifier.weight(1f).height(1.dp).background(Color(0xFFE2E8F0)))
            }

            // Email field
            Text(
                text = "EMAIL",
                fontFamily = PlusJakartaSansFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 0.5.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("you@university.edu.ph", color = Color(0xFF94A3B8), fontSize = 14.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF2563EB),
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Password field
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PASSWORD",
                    fontFamily = PlusJakartaSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = "Forgot password?",
                    fontFamily = PlusJakartaSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color(0xFF2563EB),
                    modifier = Modifier.clickable { }
                )
            }
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("••••••••", color = Color(0xFF94A3B8), fontSize = 14.sp) },
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    Text(
                        text = if (showPassword) "Hide" else "Show",
                        color = Color(0xFF64748B),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSansFontFamily,
                        modifier = Modifier
                            .clickable { showPassword = !showPassword }
                            .padding(end = 12.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF2563EB),
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                )
            )

            Spacer(modifier = Modifier.height(26.dp))

            // Sign In Button
            Button(
                onClick = { onLoginSuccess() },
                enabled = canLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB),
                    disabledContainerColor = Color(0xFF2563EB).copy(alpha = 0.35f)
                )
            ) {
                Text(
                    text = "Sign In",
                    fontFamily = PlusJakartaSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth().clickable { onNavigateToSignup() },
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Don't have an account? ",
                    fontFamily = PlusJakartaSansFontFamily,
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = "Sign up",
                    fontFamily = PlusJakartaSansFontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2563EB)
                )
            }
        }
    }
}
