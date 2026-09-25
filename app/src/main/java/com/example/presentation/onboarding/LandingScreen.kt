package com.example.presentation.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.PlayfairDisplayFontFamily
import com.example.ui.theme.PlusJakartaSansFontFamily
import com.example.presentation.common.ResyncSymbol

data class OnboardingPageData(
    val eyebrow: String,
    val title: String,
    val description: String,
    val accent: Color
)

@Composable
fun LandingScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToSignup: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    val pages = listOf(
        OnboardingPageData(
            eyebrow = "Whole-manuscript review",
            title = "See whether every chapter tells one clear story.",
            description = "Resync reads your manuscript end-to-end and traces how your objectives, methods, findings, and conclusions connect.",
            accent = Color(0xFF93C5FD)
        ),
        OnboardingPageData(
            eyebrow = "Actionable issue detection",
            title = "Find the gaps that are easy to miss.",
            description = "Get precise, color-coded flags for logic gaps, contradictions, and repeated ideas, with the chapters involved clearly identified.",
            accent = Color(0xFFC4B5FD)
        ),
        OnboardingPageData(
            eyebrow = "A clearer path to revision",
            title = "Revise with evidence, not guesswork.",
            description = "Review suggested fixes, verify accessible citations, and use your coherence score to focus your next editing session.",
            accent = Color(0xFFA7F3D0)
        )
    )

    val currentPage = pagerState.currentPage
    val currentData = pages[currentPage]

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFF))
    ) {
        // Upper Hero with Gradient Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.05f)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1E40AF),
                            Color(0xFF2563EB),
                            Color(0xFF6366F1)
                        )
                    )
                )
        ) {
            // Decorative background circles
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.07f),
                    radius = 135.dp.toPx(),
                    center = Offset(size.width + 30.dp.toPx(), -20.dp.toPx())
                )
                drawCircle(
                    color = Color(0xFF60A5FA).copy(alpha = 0.18f),
                    radius = 95.dp.toPx(),
                    center = Offset(-20.dp.toPx(), size.height + 40.dp.toPx())
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // Top App Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    Color.White.copy(alpha = 0.16f),
                                    RoundedCornerShape(10.dp)
                                )
                                .border(
                                    1.dp,
                                    Color.White.copy(alpha = 0.2f),
                                    RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            ResyncSymbol(size = 20.dp)
                        }
                        Text(
                            text = "Resync",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = PlusJakartaSansFontFamily
                        )
                    }

                    if (currentPage < 2) {
                        Text(
                            text = "Skip intro",
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PlusJakartaSansFontFamily,
                            modifier = Modifier
                                .clickable {
                                    coroutineScope.launch { pagerState.animateScrollToPage(2) }
                                }
                                .padding(vertical = 6.dp, horizontal = 4.dp)
                        )
                    }
                }

                // Swappable Hero Visual Diagram
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            when (page) {
                                0 -> VisualPage1()
                                1 -> VisualPage2()
                                2 -> VisualPage3()
                            }
                        }
                    }
                }
            }
        }

        // Bottom Sheet Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.0f)
                .offset(y = (-20).dp)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(Color(0xFFF8FAFF))
                .padding(start = 24.dp, end = 24.dp, top = 26.dp, bottom = 20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Copy with animated transitions
                AnimatedContent(
                    targetState = currentData,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "landing_copy"
                ) { data ->
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(18.dp)
                                    .height(2.5.dp)
                                    .background(data.accent, RoundedCornerShape(2.dp))
                            )
                            Text(
                                text = data.eyebrow.uppercase(),
                                color = Color(0xFF2563EB),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = PlusJakartaSansFontFamily,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Text(
                            text = data.title,
                            fontFamily = PlayfairDisplayFontFamily,
                            fontSize = 24.sp,
                            lineHeight = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        Text(
                            text = data.description,
                            fontFamily = PlusJakartaSansFontFamily,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Controls & Buttons
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Page Indicator Dots
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        repeat(3) { index ->
                            val isSelected = index == currentPage
                            Box(
                                modifier = Modifier
                                    .height(7.dp)
                                    .width(if (isSelected) 24.dp else 7.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Color(0xFF2563EB) else Color(0xFFCBD5E1))
                                    .clickable {
                                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                                    }
                            )
                        }
                    }

                    // Main Action Button
                    Button(
                        onClick = {
                            if (currentPage < 2) {
                                coroutineScope.launch { pagerState.animateScrollToPage(currentPage + 1) }
                            } else {
                                onNavigateToSignup()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (currentPage < 2) "Continue" else "Start checking for free",
                                fontFamily = PlusJakartaSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Sign in link
                    Row(
                        modifier = Modifier.clickable { onNavigateToLogin() },
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Already have an account? ",
                            fontFamily = PlusJakartaSansFontFamily,
                            fontSize = 12.5.sp,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = "Sign in",
                            fontFamily = PlusJakartaSansFontFamily,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2563EB)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VisualPage1() {
    Canvas(modifier = Modifier.size(240.dp, 160.dp)) {
        val w = size.width
        val h = size.height

        // Outer sheet
        drawRoundRect(
            color = Color.White.copy(alpha = 0.15f),
            topLeft = Offset(20.dp.toPx(), 10.dp.toPx()),
            size = Size(w - 40.dp.toPx(), h - 20.dp.toPx()),
            cornerRadius = CornerRadius(16.dp.toPx())
        )

        // Main white card
        val cardX = 35.dp.toPx()
        val cardY = 25.dp.toPx()
        val cardW = w - 70.dp.toPx()
        val cardH = h - 50.dp.toPx()

        drawRoundRect(
            color = Color.White,
            topLeft = Offset(cardX, cardY),
            size = Size(cardW, cardH),
            cornerRadius = CornerRadius(12.dp.toPx())
        )

        // Lines inside card
        drawRoundRect(
            color = Color(0xFF1E3A8A).copy(alpha = 0.85f),
            topLeft = Offset(cardX + 14.dp.toPx(), cardY + 16.dp.toPx()),
            size = Size(60.dp.toPx(), 7.dp.toPx()),
            cornerRadius = CornerRadius(3.5.dp.toPx())
        )

        drawRoundRect(
            color = Color(0xFFCBD5E1),
            topLeft = Offset(cardX + 14.dp.toPx(), cardY + 30.dp.toPx()),
            size = Size(cardW - 28.dp.toPx(), 4.dp.toPx()),
            cornerRadius = CornerRadius(2.dp.toPx())
        )

        // Highlight box (purple)
        drawRoundRect(
            color = Color(0xFFEDE9FE),
            topLeft = Offset(cardX + 14.dp.toPx(), cardY + 42.dp.toPx()),
            size = Size(cardW - 28.dp.toPx(), 16.dp.toPx()),
            cornerRadius = CornerRadius(5.dp.toPx())
        )
        drawRoundRect(
            color = Color(0xFF7C3AED).copy(alpha = 0.6f),
            topLeft = Offset(cardX + 18.dp.toPx(), cardY + 48.dp.toPx()),
            size = Size(cardW - 48.dp.toPx(), 4.dp.toPx()),
            cornerRadius = CornerRadius(2.dp.toPx())
        )

        // Success badge checkmark circle
        val circleCenter = Offset(w - 25.dp.toPx(), h - 20.dp.toPx())
        drawCircle(
            color = Color(0xFF60A5FA),
            radius = 24.dp.toPx(),
            center = circleCenter
        )
        // Checkmark
        drawLine(
            color = Color.White,
            start = Offset(circleCenter.x - 8.dp.toPx(), circleCenter.y),
            end = Offset(circleCenter.x - 2.dp.toPx(), circleCenter.y + 6.dp.toPx()),
            strokeWidth = 3.5.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.White,
            start = Offset(circleCenter.x - 2.dp.toPx(), circleCenter.y + 6.dp.toPx()),
            end = Offset(circleCenter.x + 9.dp.toPx(), circleCenter.y - 7.dp.toPx()),
            strokeWidth = 3.5.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun VisualPage2() {
    Canvas(modifier = Modifier.size(240.dp, 160.dp)) {
        val w = size.width
        val h = size.height

        // Dashed timeline
        drawLine(
            color = Color.White.copy(alpha = 0.35f),
            start = Offset(35.dp.toPx(), 20.dp.toPx()),
            end = Offset(35.dp.toPx(), h - 20.dp.toPx()),
            strokeWidth = 2.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        )

        // 3 Nodes (Purple, Red, Amber)
        val nodes = listOf(
            Triple(Color(0xFF8B5CF6), Color(0xFF7C3AED), 35.dp.toPx()),
            Triple(Color(0xFFEF4444), Color(0xFFDC2626), 80.dp.toPx()),
            Triple(Color(0xFFF59E0B), Color(0xFFD97706), 125.dp.toPx())
        )

        nodes.forEach { (nodeColor, textColor, yPos) ->
            // Node circle
            drawCircle(
                color = nodeColor,
                radius = 14.dp.toPx(),
                center = Offset(35.dp.toPx(), yPos)
            )
            drawCircle(
                color = Color.White,
                radius = 5.dp.toPx(),
                center = Offset(35.dp.toPx(), yPos)
            )

            // Card to the right
            val cardX = 60.dp.toPx()
            val cardW = w - 75.dp.toPx()
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(cardX, yPos - 14.dp.toPx()),
                size = Size(cardW, 28.dp.toPx()),
                cornerRadius = CornerRadius(8.dp.toPx())
            )
            drawRoundRect(
                color = textColor,
                topLeft = Offset(cardX + 10.dp.toPx(), yPos - 6.dp.toPx()),
                size = Size(40.dp.toPx(), 4.dp.toPx()),
                cornerRadius = CornerRadius(2.dp.toPx())
            )
            drawRoundRect(
                color = Color(0xFFCBD5E1),
                topLeft = Offset(cardX + 10.dp.toPx(), yPos + 2.dp.toPx()),
                size = Size(cardW - 30.dp.toPx(), 3.dp.toPx()),
                cornerRadius = CornerRadius(1.5.dp.toPx())
            )
        }
    }
}

@Composable
fun VisualPage3() {
    Canvas(modifier = Modifier.size(240.dp, 160.dp)) {
        val w = size.width
        val h = size.height

        // White card background
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(15.dp.toPx(), 10.dp.toPx()),
            size = Size(w - 30.dp.toPx(), h - 20.dp.toPx()),
            cornerRadius = CornerRadius(16.dp.toPx())
        )

        // Left Score Gauge
        val gaugeCenter = Offset(60.dp.toPx(), 65.dp.toPx())
        drawCircle(
            color = Color(0xFFDBEAFE),
            radius = 30.dp.toPx(),
            center = gaugeCenter,
            style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round)
        )
        drawArc(
            color = Color(0xFF2563EB),
            startAngle = 135f,
            sweepAngle = 220f,
            useCenter = false,
            topLeft = Offset(gaugeCenter.x - 30.dp.toPx(), gaugeCenter.y - 30.dp.toPx()),
            size = Size(60.dp.toPx(), 60.dp.toPx()),
            style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round)
        )

        // Right Pills (Green, Amber, Purple)
        val pills = listOf(
            Pair(Color(0xFFDCFCE7), Color(0xFF10B981)),
            Pair(Color(0xFFFEF3C7), Color(0xFFF59E0B)),
            Pair(Color(0xFFEDE9FE), Color(0xFF8B5CF6))
        )
        pills.forEachIndexed { i, (bg, dot) ->
            val py = 35.dp.toPx() + (i * 22.dp.toPx())
            val px = 110.dp.toPx()
            drawRoundRect(
                color = bg,
                topLeft = Offset(px, py),
                size = Size(90.dp.toPx(), 16.dp.toPx()),
                cornerRadius = CornerRadius(8.dp.toPx())
            )
            drawCircle(
                color = dot,
                radius = 3.5.dp.toPx(),
                center = Offset(px + 10.dp.toPx(), py + 8.dp.toPx())
            )
            drawRoundRect(
                color = dot.copy(alpha = 0.6f),
                topLeft = Offset(px + 20.dp.toPx(), py + 6.dp.toPx()),
                size = Size(40.dp.toPx(), 4.dp.toPx()),
                cornerRadius = CornerRadius(2.dp.toPx())
            )
        }

        // Bottom horizontal progress bar
        val barY = h - 32.dp.toPx()
        drawRoundRect(
            color = Color(0xFFEFF6FF),
            topLeft = Offset(30.dp.toPx(), barY),
            size = Size(w - 60.dp.toPx(), 9.dp.toPx()),
            cornerRadius = CornerRadius(4.5.dp.toPx())
        )
        drawRoundRect(
            color = Color(0xFF60A5FA),
            topLeft = Offset(30.dp.toPx(), barY),
            size = Size((w - 60.dp.toPx()) * 0.78f, 9.dp.toPx()),
            cornerRadius = CornerRadius(4.5.dp.toPx())
        )
    }
}
