package com.example.presentation.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PlayfairDisplayFontFamily
import com.example.ui.theme.PlusJakartaSansFontFamily
import com.example.ui.theme.JetBrainsMonoFontFamily
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onNavigateToAuth: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 3 })

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect {
            onNavigateToAuth()
        }
    }

    BackHandler(enabled = pagerState.currentPage > 0) {
        coroutineScope.launch {
            pagerState.animateScrollToPage(pagerState.currentPage - 1)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF8FAFC) // Background Slate
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Top Bar: Skip Button (except on last slide)
            if (pagerState.currentPage < 2) {
                TextButton(
                    onClick = {
                        viewModel.completeOnboarding()
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .testTag("onboarding_skip_button")
                ) {
                    Text(
                        text = "Skip",
                        fontFamily = PlusJakartaSansFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // Pager Content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 100.dp)
            ) { page ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Visual Asset
                    when (page) {
                        0 -> Slide1Visual()
                        1 -> Slide2Visual()
                        2 -> Slide3Visual()
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Title
                    val titleText = when (page) {
                        0 -> "Align Your Research"
                        1 -> "Understand Every Correction"
                        else -> "Defend with Confidence"
                    }
                    Text(
                        text = titleText,
                        fontFamily = PlayfairDisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = Color(0xFF0F172A),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("onboarding_title_page_$page")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Subtitle
                    val subtitleText = when (page) {
                        0 -> "Resync systematically scans your manuscript to detect and resolve objective-to-methodology drift."
                        1 -> "Receive specific, XAI-driven revision remedies for term conflicts, contradictions, and gaps."
                        else -> "Achieve academic structural compliance and track your Coherence Index over time."
                    }
                    Text(
                        text = subtitleText,
                        fontFamily = PlusJakartaSansFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .testTag("onboarding_subtitle_page_$page")
                    )
                }
            }

            // Bottom Controls Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(24.dp)
            ) {
                // Back Button (Bottom Left / CenterStart)
                if (pagerState.currentPage > 0) {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .testTag("onboarding_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF4F46E5) // Indigo
                        )
                    }
                }

                // Page Indicator (Centered)
                Row(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .testTag("onboarding_page_indicator"),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0..2) {
                        val isSelected = pagerState.currentPage == i
                        Box(
                            modifier = Modifier
                                .size(if (isSelected) 10.dp else 8.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color(0xFF4F46E5) else Color.Transparent)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) Color(0xFF4F46E5) else Color(0xFFCBD5E1),
                                    shape = CircleShape
                                )
                        )
                    }
                }

                // Action Button (Bottom Right)
                Button(
                    onClick = {
                        if (pagerState.currentPage < 2) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            viewModel.completeOnboarding()
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .height(48.dp)
                        .testTag("onboarding_action_button"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4F46E5), // Primary Indigo
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 24.dp)
                ) {
                    Text(
                        text = if (pagerState.currentPage < 2) "Next" else "Get Started",
                        fontFamily = PlusJakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun Slide1Visual() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(280.dp, 160.dp)) {
            val width = size.width
            val height = size.height
            
            val cardWidth = 70.dp.toPx()
            val cardHeight = 90.dp.toPx()
            
            val cardPositions = listOf(
                Pair(0.15f * width, 0.45f * height),
                Pair(0.5f * width, 0.35f * height),
                Pair(0.85f * width, 0.55f * height)
            )
            
            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(cardPositions[0].first, cardPositions[0].second)
                quadraticTo(
                    (cardPositions[0].first + cardPositions[1].first) / 2f,
                    (cardPositions[0].second + cardPositions[1].second) / 2f - 40f,
                    cardPositions[1].first, cardPositions[1].second
                )
                quadraticTo(
                    (cardPositions[1].first + cardPositions[2].first) / 2f,
                    (cardPositions[1].second + cardPositions[2].second) / 2f + 40f,
                    cardPositions[2].first, cardPositions[2].second
                )
            }
            
            drawPath(
                path = path,
                color = Color(0xFF4F46E5), // Indigo
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )
            
            cardPositions.forEach { pos ->
                val x = pos.first - cardWidth / 2f
                val y = pos.second - cardHeight / 2f
                
                drawRoundRect(
                    color = Color.White,
                    topLeft = androidx.compose.ui.geometry.Offset(x, y),
                    size = androidx.compose.ui.geometry.Size(cardWidth, cardHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx())
                )
                
                drawRoundRect(
                    color = Color(0xFFE2E8F0),
                    topLeft = androidx.compose.ui.geometry.Offset(x, y),
                    size = androidx.compose.ui.geometry.Size(cardWidth, cardHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx()),
                    style = Stroke(width = 1.5.dp.toPx())
                )
                
                val lineStartX = x + 10.dp.toPx()
                val lineEndX = x + cardWidth - 10.dp.toPx()
                val lineSpacing = 12.dp.toPx()
                
                for (i in 0..2) {
                    val lineY = y + 15.dp.toPx() + i * lineSpacing
                    val curEnd = if (i == 2) x + cardWidth * 0.6f else lineEndX
                    drawLine(
                        color = Color(0xFF94A3B8),
                        start = androidx.compose.ui.geometry.Offset(lineStartX, lineY),
                        end = androidx.compose.ui.geometry.Offset(curEnd, lineY),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
                
                drawCircle(
                    color = Color(0xFF4F46E5),
                    radius = 6.dp.toPx(),
                    center = androidx.compose.ui.geometry.Offset(pos.first, pos.second)
                )
                drawCircle(
                    color = Color.White,
                    radius = 3.dp.toPx(),
                    center = androidx.compose.ui.geometry.Offset(pos.first, pos.second)
                )
            }
        }
    }
}

@Composable
fun Slide2Visual() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFFECACA), RoundedCornerShape(12.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFFEE2E2)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "!",
                        color = Color(0xFFEF4444),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        fontFamily = JetBrainsMonoFontFamily
                    )
                }
                Text(
                    text = "CONFLICT DETECTED",
                    color = Color(0xFFEF4444),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    fontFamily = JetBrainsMonoFontFamily
                )
            }
            Text(
                text = "Chapter 1 specifies a $100k budget, while Chapter 3 states $250k.",
                color = Color(0xFF334155),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = PlusJakartaSansFontFamily
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "Correction",
            tint = Color(0xFF4F46E5),
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFEEF2FF))
                .border(1.dp, Color(0xFF4F46E5), RoundedCornerShape(12.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Success",
                    tint = Color(0xFF4F46E5),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "REMEDY",
                    color = Color(0xFF4F46E5),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    fontFamily = JetBrainsMonoFontFamily
                )
            }
            Text(
                text = "Align all financial statements to the approved $250k board draft.",
                color = Color(0xFF1E1B4B),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PlusJakartaSansFontFamily
            )
        }
    }
}

@Composable
fun Slide3Visual() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(RoundedCornerShape(80.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(80.dp)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(120.dp)) {
                drawArc(
                    color = Color(0xFFF1F5F9),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )
                
                drawArc(
                    color = Color(0xFF10B981),
                    startAngle = -90f,
                    sweepAngle = 342f,
                    useCenter = false,
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "95%",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = Color(0xFF10B981)
                )
                Text(
                    text = "Coherence Index",
                    fontFamily = PlusJakartaSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}
