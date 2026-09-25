package com.example.presentation.dashboard

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PlayfairDisplayFontFamily
import com.example.ui.theme.PlusJakartaSansFontFamily

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToNewScan: () -> Unit,
    onNavigateToDetails: (Int, String, List<String>) -> Unit,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.dashboardUiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.fetchDashboardData()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(scrollState)
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp)
            .testTag("dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // =====================================================================
        // 1. TOP GREETING & CREDITS BADGE
        // =====================================================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "GOOD MORNING",
                    color = Color(0xFF64748B),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp,
                    fontFamily = PlusJakartaSansFontFamily
                )
                Text(
                    text = "Welcome back, Juan.",
                    color = Color(0xFF0F172A),
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PlayfairDisplayFontFamily
                )
            }

            // Credits Badge Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF2563EB))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "★",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "3 Credits",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSansFontFamily
                    )
                }
            }
        }

        // =====================================================================
        // 2. HERO BLUE CARD: "Your manuscript, checked end-to-end."
        // =====================================================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2563EB),
                            Color(0xFF1D4ED8)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            // Ambient decorative background circle
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.08f),
                    radius = 120.dp.toPx(),
                    center = Offset(size.width * 0.9f, 20.dp.toPx())
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top Tag Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.18f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "• AI-powered · ~2 minutes",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = PlusJakartaSansFontFamily
                    )
                }

                // Card Heading
                Text(
                    text = "Your manuscript,\nchecked end-to-end.",
                    color = Color.White,
                    fontSize = 21.sp,
                    lineHeight = 27.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PlayfairDisplayFontFamily
                )

                // Card Subtitle
                Text(
                    text = "Logic gaps · contradictions · redundancies · dead citations — all in a single pass.",
                    color = Color.White.copy(alpha = 0.88f),
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    fontFamily = PlusJakartaSansFontFamily
                )

                // Actions Row: Start a scan button + Preview sample link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Button(
                        onClick = onNavigateToNewScan,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF0F172A)
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "↑",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Start a scan",
                                fontFamily = PlusJakartaSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF0F172A)
                            )
                        }
                    }

                    Text(
                        text = "Preview sample >",
                        color = Color.White,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = PlusJakartaSansFontFamily,
                        modifier = Modifier
                            .clickable {
                                onNavigateToDetails(87, "preview_sample", listOf())
                            }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                // ACCEPTS section
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "ACCEPTS",
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        fontFamily = PlusJakartaSansFontFamily
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AcceptsChip(text = "📄 .docx — Word")
                        AcceptsChip(text = "🔗 Google Docs — share link")
                        AcceptsChip(text = "📄 + Template — optional")
                    }
                }
            }
        }

        // =====================================================================
        // 3. PROCESS CARD: "How It Works"
        // =====================================================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(22.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = "PROCESS",
                    color = Color(0xFF64748B),
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.6.sp,
                    fontFamily = PlusJakartaSansFontFamily
                )
                Text(
                    text = "How It Works",
                    color = Color(0xFF0F172A),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PlusJakartaSansFontFamily
                )
                Text(
                    text = "Four simple steps from manuscript to coherence report.",
                    color = Color(0xFF64748B),
                    fontSize = 12.sp,
                    fontFamily = PlusJakartaSansFontFamily
                )
            }

            // Steps Container
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StepItemCard(
                    stepNum = "STEP 1",
                    title = "Upload Manuscript",
                    description = "Drop your .docx or paste a Google Docs share link.",
                    iconSymbol = "↑"
                )
                StepItemCard(
                    stepNum = "STEP 2",
                    title = "Upload Template",
                    description = "Attach your school's chapter template for better detection.",
                    iconSymbol = "📄"
                )
                StepItemCard(
                    stepNum = "STEP 3",
                    title = "Use Credits",
                    description = "One credit covers a full coherence scan of your manuscript.",
                    iconSymbol = "💳"
                )
                StepItemCard(
                    stepNum = "STEP 4",
                    title = "Export Report",
                    description = "Download your annotated report and fix recommendations.",
                    iconSymbol = "↓"
                )
            }

            // Start your first scan Action Button
            Button(
                onClick = onNavigateToNewScan,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB),
                    contentColor = Color.White
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "↑",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Start your first scan",
                        fontFamily = PlusJakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // =====================================================================
        // 4. OUTPUTS SECTION: "What You Get Back"
        // =====================================================================
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "OUTPUTS",
                    color = Color(0xFF64748B),
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.6.sp,
                    fontFamily = PlusJakartaSansFontFamily
                )
                Text(
                    text = "What You Get Back",
                    color = Color(0xFF0F172A),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PlusJakartaSansFontFamily
                )
            }

            // Output Cards
            OutputCardItem(
                title = "Coherence Score",
                description = "A 0-100 integrity score with three sub-scores: logic, consistency, and citations.",
                iconBg = Color(0xFFEFF6FF),
                iconColor = Color(0xFF2563EB),
                iconText = "⏱"
            )

            OutputCardItem(
                title = "Annotated Manuscript",
                description = "Full text with every flagged passage highlighted inline by issue type.",
                iconBg = Color(0xFFF5F3FF),
                iconColor = Color(0xFF7C3AED),
                iconText = "✏️"
            )

            OutputCardItem(
                title = "Fix Recommendations",
                description = "Concrete, actionable rewrites for each flagged issue — ready to apply.",
                iconBg = Color(0xFFECFDF5),
                iconColor = Color(0xFF059669),
                iconText = "!"
            )

            OutputCardItem(
                title = "Verified Reference List",
                description = "Live / dead status for every cited URL and DOI in your bibliography.",
                iconBg = Color(0xFFFEFCE8),
                iconColor = Color(0xFFD97706),
                iconText = "🔗"
            )
        }

        // =====================================================================
        // 5. PREVIEW SAMPLE & RECENT SCANS
        // =====================================================================
        // Preview Sample Outline Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(14.dp))
                .clickable {
                    onNavigateToDetails(87, "sample_preview", listOf())
                },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = "🔍", fontSize = 13.sp)
                Text(
                    text = "Preview a sample report",
                    fontFamily = PlusJakartaSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF2563EB)
                )
            }
        }

        // Recent Scans Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Scans",
                    color = Color(0xFF0F172A),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PlusJakartaSansFontFamily
                )
                Text(
                    text = "See all",
                    color = Color(0xFF2563EB),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PlusJakartaSansFontFamily,
                    modifier = Modifier.clickable { onNavigateToHistory() }
                )
            }

            // Recent Scan Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                    .clickable {
                        onNavigateToDetails(74, "Capstone_Final_v3.docx", listOf("Methodology Drift", "Sample Size Mismatch"))
                    }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "📄", fontSize = 16.sp)
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "Capstone_Final_v3.docx",
                            fontFamily = PlusJakartaSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Score: 74 · 4 issues · 2 days ago",
                            fontFamily = PlusJakartaSansFontFamily,
                            fontSize = 11.5.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Score Badge Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFEF3C7))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "74",
                        color = Color(0xFF854D0E),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // =====================================================================
        // 6. TRANSPARENCY / SCAN LIMITATIONS CARD
        // =====================================================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(22.dp))
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header with Warning Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = "TRANSPARENCY",
                        color = Color(0xFF64748B),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.6.sp,
                        fontFamily = PlusJakartaSansFontFamily
                    )
                    Text(
                        text = "Scan Limitations",
                        color = Color(0xFF0F172A),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSansFontFamily
                    )
                }

                // Soft Warning Icon
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFEF3C7)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "⚠️", fontSize = 14.sp)
                }
            }

            Text(
                text = "Resync is powerful, but it has boundaries. Here is what it cannot fully handle — so you know what to expect.",
                color = Color(0xFF64748B),
                fontSize = 11.5.sp,
                lineHeight = 16.5.sp,
                fontFamily = PlusJakartaSansFontFamily
            )

            // 2x3 Grid of Limitation Cards
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LimitationGridItem(
                        modifier = Modifier.weight(1f),
                        icon = "🖼️",
                        title = "Images in Manuscript",
                        desc = "Resync cannot read text inside images — image-embedded content may be flagged or skipped."
                    )
                    LimitationGridItem(
                        modifier = Modifier.weight(1f),
                        icon = "✏️",
                        title = "Handwritten Manuscripts",
                        desc = "Only digital, text-based files can be processed — handwriting is not supported."
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LimitationGridItem(
                        modifier = Modifier.weight(1f),
                        icon = "📄",
                        title = "Scanned PDF Files",
                        desc = "Image-only scans (photographed pages) cannot be properly processed."
                    )
                    LimitationGridItem(
                        modifier = Modifier.weight(1f),
                        icon = "📑",
                        title = "Multi-Column Layouts",
                        desc = "Multi-column formatting may not be read in the correct reading order."
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LimitationGridItem(
                        modifier = Modifier.weight(1f),
                        icon = "🔒",
                        title = "Private Files",
                        desc = "Resync cannot access sources behind login walls, paywalls, or private repositories."
                    )
                    LimitationGridItem(
                        modifier = Modifier.weight(1f),
                        icon = "ℹ️",
                        title = "Citation Validation",
                        desc = "Only confirms public accessibility — not full accuracy. Results may vary."
                    )
                }
            }

            // Bottom Banner: "Attach your school template"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF0F7FF))
                    .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(14.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, Color(0xFFBFDBFE), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ℹ️",
                        fontSize = 13.sp
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Attach your school template",
                        fontFamily = PlusJakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF1E3A8A)
                    )
                    Text(
                        text = "Upload your institution's chapter template so Resync maps headings accurately — significantly improves logic gap detection.",
                        fontFamily = PlusJakartaSansFontFamily,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = Color(0xFF1E3A8A)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = onNavigateToNewScan,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2563EB),
                            contentColor = Color.White
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text(
                            text = "Try it",
                            fontFamily = PlusJakartaSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(70.dp)) // Padding for bottom bar
    }
}

// -----------------------------------------------------------------------------
// HELPER COMPOSABLES FOR DASHBOARD
// -----------------------------------------------------------------------------

@Composable
fun AcceptsChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.14f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 11.sp,
            fontFamily = PlusJakartaSansFontFamily,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun StepItemCard(
    stepNum: String,
    title: String,
    description: String,
    iconSymbol: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF8FAFC))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Icon Circle
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color(0xFFEFF6FF))
                .border(1.5.dp, Color(0xFF3B82F6), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = iconSymbol,
                color = Color(0xFF2563EB),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = stepNum,
                color = Color(0xFF2563EB),
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp,
                fontFamily = PlusJakartaSansFontFamily
            )
            Text(
                text = title,
                fontFamily = PlusJakartaSansFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.5.sp,
                color = Color(0xFF0F172A)
            )
            Text(
                text = description,
                fontFamily = PlusJakartaSansFontFamily,
                fontSize = 11.5.sp,
                lineHeight = 15.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}

@Composable
fun OutputCardItem(
    title: String,
    description: String,
    iconBg: Color,
    iconColor: Color,
    iconText: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = iconText,
                color = iconColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                fontFamily = PlusJakartaSansFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.5.sp,
                color = Color(0xFF0F172A)
            )
            Text(
                text = description,
                fontFamily = PlusJakartaSansFontFamily,
                fontSize = 11.5.sp,
                lineHeight = 15.5.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}

@Composable
fun LimitationGridItem(
    icon: String,
    title: String,
    desc: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF8FAFC))
            .border(1.dp, Color(0xFFE2E8F0).copy(alpha = 0.6f), RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = icon, fontSize = 13.sp)
        }

        Text(
            text = title,
            fontFamily = PlusJakartaSansFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color(0xFF0F172A)
        )

        Text(
            text = desc,
            fontFamily = PlusJakartaSansFontFamily,
            fontSize = 10.5.sp,
            lineHeight = 14.5.sp,
            color = Color(0xFF64748B)
        )
    }
}
