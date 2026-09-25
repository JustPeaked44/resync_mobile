package com.example.presentation.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.common.ResyncSymbol
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.PlayfairDisplayFontFamily
import com.example.ui.theme.PlusJakartaSansFontFamily
import kotlinx.coroutines.launch

data class OnboardingSlideData(
    val eyebrow: String,
    val title: String,
    val description: String,
    val buttonText: String,
    val accentColor: Color = Color(0xFF3B82F6)
)

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToSignup: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect {
            onNavigateToSignup()
        }
    }

    BackHandler(enabled = pagerState.currentPage > 0) {
        coroutineScope.launch {
            pagerState.animateScrollToPage(pagerState.currentPage - 1)
        }
    }

    val slides = listOf(
        OnboardingSlideData(
            eyebrow = "WHOLE-MANUSCRIPT REVIEW",
            title = "See whether every chapter tells one clear story.",
            description = "Resync reads your manuscript end-to-end and traces how your objectives, methods, findings, and conclusions connect.",
            buttonText = "Continue",
            accentColor = Color(0xFF3B82F6)
        ),
        OnboardingSlideData(
            eyebrow = "ACTIONABLE ISSUE DETECTION",
            title = "Find the gaps that are easy to miss.",
            description = "Get precise, color-coded flags for logic gaps, contradictions, and repeated ideas, with the chapters involved clearly identified.",
            buttonText = "Continue",
            accentColor = Color(0xFF3B82F6)
        ),
        OnboardingSlideData(
            eyebrow = "A CLEARER PATH TO REVISION",
            title = "Revise with evidence, not guesswork.",
            description = "Review suggested fixes, verify accessible citations, and use your coherence score to focus your next editing session.",
            buttonText = "Start checking for free",
            accentColor = Color(0xFF34D399)
        )
    )

    val currentPage = pagerState.currentPage
    val currentSlide = slides[currentPage]

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag("onboarding_screen")
    ) {
        // TOP HALF: Vibrant Blue Hero Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.18f)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1D4ED8), // Royal Deep Blue
                            Color(0xFF2563EB), // Vibrant Blue
                            Color(0xFF3B82F6)  // Azure
                        )
                    )
                )
        ) {
            // Ambient subtle glowing backdrop circles
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.08f),
                    radius = 160.dp.toPx(),
                    center = Offset(size.width * 0.9f, size.height * 0.15f)
                )
                drawCircle(
                    color = Color(0xFF60A5FA).copy(alpha = 0.25f),
                    radius = 120.dp.toPx(),
                    center = Offset(size.width * 0.1f, size.height * 0.85f)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 22.dp, vertical = 12.dp)
            ) {
                // Top App Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Resync Logo Lockup
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.18f))
                                .border(
                                    1.dp,
                                    Color.White.copy(alpha = 0.28f),
                                    RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            ResyncSymbol(size = 20.dp)
                        }

                        Text(
                            text = "Resync",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PlusJakartaSansFontFamily
                        )
                    }

                    // Skip intro button
                    Text(
                        text = "Skip intro",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = PlusJakartaSansFontFamily,
                        modifier = Modifier
                            .clickable {
                                onNavigateToSignup()
                            }
                            .padding(vertical = 6.dp, horizontal = 4.dp)
                    )
                }

                // Center Illustration Pager
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
                                0 -> Slide1VisualIllustration()
                                1 -> Slide2VisualIllustration()
                                2 -> Slide3VisualIllustration()
                            }
                        }
                    }
                }
            }
        }

        // BOTTOM HALF: White Rounded Bottom Sheet Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.95f)
                .offset(y = (-24).dp)
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(Color.White)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    spotColor = Color(0x1A0F172A)
                )
                .padding(horizontal = 26.dp, vertical = 26.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Content Header and Body
                AnimatedContent(
                    targetState = currentSlide,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "slide_text_content"
                ) { slide ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Tag with horizontal line accent
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(18.dp)
                                    .height(2.5.dp)
                                    .background(slide.accentColor, RoundedCornerShape(2.dp))
                            )
                            Text(
                                text = slide.eyebrow,
                                color = Color(0xFF2563EB),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = PlusJakartaSansFontFamily,
                                letterSpacing = 0.8.sp
                            )
                        }

                        // Bold Serif Title
                        Text(
                            text = slide.title,
                            fontFamily = PlayfairDisplayFontFamily,
                            fontSize = 24.sp,
                            lineHeight = 31.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        // Clean Sans Subtitle
                        Text(
                            text = slide.description,
                            fontFamily = PlusJakartaSansFontFamily,
                            fontSize = 13.5.sp,
                            lineHeight = 20.5.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Controls: Dots Indicator, Action Button, Sign In footer
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Page Indicator Dots
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        repeat(3) { index ->
                            val isSelected = index == currentPage
                            Box(
                                modifier = Modifier
                                    .height(6.dp)
                                    .width(if (isSelected) 24.dp else 6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) Color(0xFF2563EB) else Color(0xFFCBD5E1)
                                    )
                                    .clickable {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(index)
                                        }
                                    }
                            )
                        }
                    }

                    // Main Primary Action Button
                    Button(
                        onClick = {
                            if (currentPage < 2) {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(currentPage + 1)
                                }
                            } else {
                                onNavigateToSignup()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2563EB),
                            contentColor = Color.White
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = currentSlide.buttonText,
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

                    // Bottom Sign In Navigation
                    Row(
                        modifier = Modifier
                            .clickable {
                                onNavigateToLogin()
                            }
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Already have an account? ",
                            fontFamily = PlusJakartaSansFontFamily,
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = "Sign in",
                            fontFamily = PlusJakartaSansFontFamily,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2563EB)
                        )
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// SLIDE 1 ILLUSTRATION: Glowing Manuscript Review Card with Purple Selection & Checkmark Badge
// -----------------------------------------------------------------------------------------
@Composable
fun Slide1VisualIllustration() {
    Box(
        modifier = Modifier.size(240.dp, 160.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Floating "+" indicator on top left
            val plusX = 22.dp.toPx()
            val plusY = 32.dp.toPx()
            drawLine(
                color = Color.White.copy(alpha = 0.45f),
                start = Offset(plusX - 6.dp.toPx(), plusY),
                end = Offset(plusX + 6.dp.toPx(), plusY),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.White.copy(alpha = 0.45f),
                start = Offset(plusX, plusY - 6.dp.toPx()),
                end = Offset(plusX, plusY + 6.dp.toPx()),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Floating "-" indicator on right
            val minusX = w - 18.dp.toPx()
            val minusY = 40.dp.toPx()
            drawLine(
                color = Color.White.copy(alpha = 0.45f),
                start = Offset(minusX - 6.dp.toPx(), minusY),
                end = Offset(minusX + 6.dp.toPx(), minusY),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Outer translucent card frame
            drawRoundRect(
                color = Color.White.copy(alpha = 0.15f),
                topLeft = Offset(24.dp.toPx(), 12.dp.toPx()),
                size = Size(w - 48.dp.toPx(), h - 24.dp.toPx()),
                cornerRadius = CornerRadius(20.dp.toPx())
            )

            // Main White Card
            val cardX = 36.dp.toPx()
            val cardY = 22.dp.toPx()
            val cardW = w - 72.dp.toPx()
            val cardH = h - 44.dp.toPx()

            drawRoundRect(
                color = Color.White,
                topLeft = Offset(cardX, cardY),
                size = Size(cardW, cardH),
                cornerRadius = CornerRadius(16.dp.toPx())
            )

            // Line 1: Dark Blue Title bar
            drawRoundRect(
                color = Color(0xFF1E3A8A),
                topLeft = Offset(cardX + 16.dp.toPx(), cardY + 16.dp.toPx()),
                size = Size(56.dp.toPx(), 6.5.dp.toPx()),
                cornerRadius = CornerRadius(3.dp.toPx())
            )

            // Line 2: Grey sub-line
            drawRoundRect(
                color = Color(0xFFCBD5E1),
                topLeft = Offset(cardX + 16.dp.toPx(), cardY + 29.dp.toPx()),
                size = Size(cardW - 32.dp.toPx(), 4.dp.toPx()),
                cornerRadius = CornerRadius(2.dp.toPx())
            )

            // Line 3: Purple Highlight Band
            val highlightY = cardY + 40.dp.toPx()
            val highlightW = cardW - 32.dp.toPx()
            drawRoundRect(
                color = Color(0xFFF3E8FF),
                topLeft = Offset(cardX + 16.dp.toPx(), highlightY),
                size = Size(highlightW, 16.dp.toPx()),
                cornerRadius = CornerRadius(6.dp.toPx())
            )
            drawRoundRect(
                color = Color(0xFF9333EA),
                topLeft = Offset(cardX + 22.dp.toPx(), highlightY + 6.dp.toPx()),
                size = Size(highlightW * 0.72f, 4.dp.toPx()),
                cornerRadius = CornerRadius(2.dp.toPx())
            )

            // Line 4: Grey bottom line
            drawRoundRect(
                color = Color(0xFFCBD5E1),
                topLeft = Offset(cardX + 16.dp.toPx(), cardY + 64.dp.toPx()),
                size = Size(cardW * 0.58f, 4.dp.toPx()),
                cornerRadius = CornerRadius(2.dp.toPx())
            )

            // Bottom Right Checkmark Badge
            val badgeCenter = Offset(w - 28.dp.toPx(), h - 22.dp.toPx())
            drawCircle(
                color = Color(0xFF60A5FA),
                radius = 21.dp.toPx(),
                center = badgeCenter
            )
            // White Checkmark
            drawLine(
                color = Color.White,
                start = Offset(badgeCenter.x - 7.dp.toPx(), badgeCenter.y),
                end = Offset(badgeCenter.x - 2.dp.toPx(), badgeCenter.y + 5.5.dp.toPx()),
                strokeWidth = 3.2.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.White,
                start = Offset(badgeCenter.x - 2.dp.toPx(), badgeCenter.y + 5.5.dp.toPx()),
                end = Offset(badgeCenter.x + 8.dp.toPx(), badgeCenter.y - 6.5.dp.toPx()),
                strokeWidth = 3.2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

// -----------------------------------------------------------------------------------------
// SLIDE 2 ILLUSTRATION: 3 Stacked Cards with Purple (+), Red (x), Orange (=) Indicator Badges
// -----------------------------------------------------------------------------------------
@Composable
fun Slide2VisualIllustration() {
    Box(
        modifier = Modifier.size(240.dp, 160.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val items = listOf(
                Triple(Color(0xFFA855F7), Color(0xFF9333EA), "+"),
                Triple(Color(0xFFEF4444), Color(0xFFDC2626), "✕"),
                Triple(Color(0xFFF59E0B), Color(0xFFD97706), "=")
            )

            val rowSpacing = 44.dp.toPx()
            val startY = 28.dp.toPx()

            items.forEachIndexed { index, (circleColor, barColor, symbol) ->
                val cy = startY + (index * rowSpacing)

                // Left Circle Icon
                val circleCenter = Offset(42.dp.toPx(), cy)
                drawCircle(
                    color = circleColor,
                    radius = 14.5.dp.toPx(),
                    center = circleCenter
                )

                // White Symbol inside Circle
                when (symbol) {
                    "+" -> {
                        drawLine(
                            color = Color.White,
                            start = Offset(circleCenter.x - 5.dp.toPx(), circleCenter.y),
                            end = Offset(circleCenter.x + 5.dp.toPx(), circleCenter.y),
                            strokeWidth = 2.4.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = Color.White,
                            start = Offset(circleCenter.x, circleCenter.y - 5.dp.toPx()),
                            end = Offset(circleCenter.x, circleCenter.y + 5.dp.toPx()),
                            strokeWidth = 2.4.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                    "✕" -> {
                        drawLine(
                            color = Color.White,
                            start = Offset(circleCenter.x - 4.dp.toPx(), circleCenter.y - 4.dp.toPx()),
                            end = Offset(circleCenter.x + 4.dp.toPx(), circleCenter.y + 4.dp.toPx()),
                            strokeWidth = 2.4.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = Color.White,
                            start = Offset(circleCenter.x + 4.dp.toPx(), circleCenter.y - 4.dp.toPx()),
                            end = Offset(circleCenter.x - 4.dp.toPx(), circleCenter.y + 4.dp.toPx()),
                            strokeWidth = 2.4.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                    "=" -> {
                        drawLine(
                            color = Color.White,
                            start = Offset(circleCenter.x - 5.dp.toPx(), circleCenter.y - 2.5.dp.toPx()),
                            end = Offset(circleCenter.x + 5.dp.toPx(), circleCenter.y - 2.5.dp.toPx()),
                            strokeWidth = 2.2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = Color.White,
                            start = Offset(circleCenter.x - 5.dp.toPx(), circleCenter.y + 2.5.dp.toPx()),
                            end = Offset(circleCenter.x + 5.dp.toPx(), circleCenter.y + 2.5.dp.toPx()),
                            strokeWidth = 2.2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                }

                // White Card adjacent to circle
                val cardX = 66.dp.toPx()
                val cardW = w - 88.dp.toPx()
                val cardH = 30.dp.toPx()
                val cardTop = cy - 15.dp.toPx()

                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(cardX, cardTop),
                    size = Size(cardW, cardH),
                    cornerRadius = CornerRadius(10.dp.toPx())
                )

                // Colored Bar inside Card
                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(cardX + 12.dp.toPx(), cardTop + 8.dp.toPx()),
                    size = Size(36.dp.toPx(), 4.5.dp.toPx()),
                    cornerRadius = CornerRadius(2.25.dp.toPx())
                )

                // Grey Sub-line inside Card
                drawRoundRect(
                    color = Color(0xFFCBD5E1),
                    topLeft = Offset(cardX + 12.dp.toPx(), cardTop + 17.dp.toPx()),
                    size = Size(cardW - 38.dp.toPx(), 3.5.dp.toPx()),
                    cornerRadius = CornerRadius(1.75.dp.toPx())
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// SLIDE 3 ILLUSTRATION: Coherence Score Gauge (86) + Status Pills + Progress Bar
// -----------------------------------------------------------------------------------------
@Composable
fun Slide3VisualIllustration() {
    Box(
        modifier = Modifier.size(240.dp, 160.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Main White Container Card
            val cardX = 20.dp.toPx()
            val cardY = 12.dp.toPx()
            val cardW = w - 40.dp.toPx()
            val cardH = h - 24.dp.toPx()

            drawRoundRect(
                color = Color.White,
                topLeft = Offset(cardX, cardY),
                size = Size(cardW, cardH),
                cornerRadius = CornerRadius(18.dp.toPx())
            )

            // Left Coherence Radial Ring
            val gaugeCenter = Offset(cardX + 44.dp.toPx(), cardY + 44.dp.toPx())
            val gaugeRadius = 26.dp.toPx()

            // Inactive Ring
            drawCircle(
                color = Color(0xFFEFF6FF),
                radius = gaugeRadius,
                center = gaugeCenter,
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )

            // Active Blue Arc
            drawArc(
                color = Color(0xFF2563EB),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = Offset(gaugeCenter.x - gaugeRadius, gaugeCenter.y - gaugeRadius),
                size = Size(gaugeRadius * 2, gaugeRadius * 2),
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )

            // Right Status Indicator Pills (Green, Orange, Purple)
            val pills = listOf(
                Pair(Color(0xFFDCFCE7), Color(0xFF10B981)), // Green
                Pair(Color(0xFFFEF3C7), Color(0xFFF59E0B)), // Orange/Yellow
                Pair(Color(0xFFEDE9FE), Color(0xFF8B5CF6))  // Purple
            )

            val pillStartX = cardX + 88.dp.toPx()
            val pillW = cardW - 100.dp.toPx()

            pills.forEachIndexed { i, (bg, dotColor) ->
                val pillY = cardY + 22.dp.toPx() + (i * 17.dp.toPx())
                // Pill Background
                drawRoundRect(
                    color = bg,
                    topLeft = Offset(pillStartX, pillY),
                    size = Size(pillW, 12.dp.toPx()),
                    cornerRadius = CornerRadius(6.dp.toPx())
                )
                // Dot
                drawCircle(
                    color = dotColor,
                    radius = 3.dp.toPx(),
                    center = Offset(pillStartX + 8.dp.toPx(), pillY + 6.dp.toPx())
                )
                // Inner mini bar
                drawRoundRect(
                    color = dotColor.copy(alpha = 0.5f),
                    topLeft = Offset(pillStartX + 16.dp.toPx(), pillY + 4.5.dp.toPx()),
                    size = Size(pillW - 24.dp.toPx(), 3.dp.toPx()),
                    cornerRadius = CornerRadius(1.5.dp.toPx())
                )
            }

            // Bottom Progress Bar
            val barY = cardY + cardH - 24.dp.toPx()
            val barX = cardX + 16.dp.toPx()
            val barW = cardW - 32.dp.toPx()

            drawRoundRect(
                color = Color(0xFFEFF6FF),
                topLeft = Offset(barX, barY),
                size = Size(barW, 8.dp.toPx()),
                cornerRadius = CornerRadius(4.dp.toPx())
            )
            drawRoundRect(
                color = Color(0xFF60A5FA),
                topLeft = Offset(barX, barY),
                size = Size(barW * 0.76f, 8.dp.toPx()),
                cornerRadius = CornerRadius(4.dp.toPx())
            )
        }

        // Center Gauge Text overlay (86 and COHERENCE)
        Box(
            modifier = Modifier
                .size(70.dp)
                .offset(x = (-42).dp, y = (-10).dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "86",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "COHERENCE",
                    fontFamily = PlusJakartaSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 6.5.sp,
                    color = Color(0xFF64748B),
                    letterSpacing = 0.4.sp
                )
            }
        }
    }
}
