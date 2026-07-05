package com.example.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.remote.dto.LinkStatus
import com.example.data.remote.dto.ScanResponse
import com.example.ui.theme.ButtonShape
import com.example.ui.theme.CardShape
import com.example.ui.theme.PlayfairDisplayFontFamily
import com.example.ui.theme.PlusJakartaSansFontFamily
import com.example.ui.theme.JetBrainsMonoFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToNewScan: () -> Unit,
    onNavigateToDetails: (Int, String, List<String>) -> Unit,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.dashboardUiState.collectAsState()

    // Load data when screen enters composition
    LaunchedEffect(Unit) {
        viewModel.fetchDashboardData()
    }

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("dashboard_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Resync Logo",
                            tint = Color(0xFF4F46E5), // Resync Indigo
                            modifier = Modifier.size(26.dp)
                        )
                        Text(
                            text = "Resync",
                            fontFamily = PlayfairDisplayFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color(0xFF0F172A)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier.testTag("dashboard_settings_button")
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is DashboardUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF4F46E5)
                        )
                    }
                }
                is DashboardUiState.Empty -> {
                    EmptyStateView(
                        onNavigateToNewScan = onNavigateToNewScan
                    )
                }
                is DashboardUiState.Success -> {
                    SuccessStateView(
                        lastScan = state.lastScan,
                        history = state.history,
                        onNavigateToDetails = onNavigateToDetails,
                        onNavigateToHistory = onNavigateToHistory
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyStateView(
    onNavigateToNewScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Hero Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFE2E8F0), CardShape)
                .testTag("empty_hero_card"),
            shape = CardShape,
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4F46E5).copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Sparkle Icon",
                        tint = Color(0xFF4F46E5),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = "Scan your first chapter",
                    fontFamily = PlayfairDisplayFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF0F172A),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "You have not yet analyzed a manuscript. Submit a Google Docs document link to receive a detailed coherence check, find logical contradictions, and validate citations automatically.",
                    fontFamily = PlusJakartaSansFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }
        }

        // Disabled Summary Cards Title
        Text(
            text = "Manuscript Metrics",
            fontFamily = PlusJakartaSansFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Color(0xFF94A3B8)
        )

        // Three Disabled Cards Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DisabledSummaryCard(
                title = "Integrity Score",
                value = "— / 100",
                modifier = Modifier.weight(1f).testTag("inactive_integrity_card")
            )
            DisabledSummaryCard(
                title = "Issues Flagged",
                value = "—",
                modifier = Modifier.weight(1f).testTag("inactive_issues_card")
            )
            DisabledSummaryCard(
                title = "Citations Checked",
                value = "—",
                modifier = Modifier.weight(1f).testTag("inactive_citations_card")
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Full-width primary button
        Button(
            onClick = onNavigateToNewScan,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("upload_chapter_button"),
            shape = ButtonShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4F46E5),
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Upload Icon",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Upload a chapter to begin",
                fontFamily = PlusJakartaSansFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun DisabledSummaryCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .border(1.dp, Color(0xFFE2E8F0).copy(alpha = 0.5f), CardShape),
        shape = CardShape,
        color = Color(0xFFF1F5F9).copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title.uppercase(),
                fontFamily = PlusJakartaSansFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = Color(0xFF94A3B8),
                letterSpacing = 0.5.sp
            )
            Text(
                text = value,
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFFCBD5E1)
            )
        }
    }
}

@Composable
fun SuccessStateView(
    lastScan: ScanResponse,
    history: List<ScanResponse>,
    onNavigateToDetails: (Int, String, List<String>) -> Unit,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Top Results Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFE2E8F0), CardShape)
                .testTag("results_card"),
            shape = CardShape,
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Area with Circular Score & Meta Text
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Circular score indicator
                    val progressColor = when {
                        lastScan.coherenceScore >= 80 -> Color(0xFF10B981) // Success Emerald
                        lastScan.coherenceScore >= 50 -> Color(0xFFF59E0B) // Warning Amber
                        else -> Color(0xFFF43F5E) // Error Rose
                    }

                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .testTag("score_indicator"),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = lastScan.coherenceScore / 100f,
                            color = progressColor,
                            trackColor = Color(0xFFF1F5F9),
                            strokeWidth = 6.dp,
                            modifier = Modifier.fillMaxSize()
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${lastScan.coherenceScore}",
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "/100",
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 10.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    // Score Meta Text
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Manuscript Integrity",
                            fontFamily = PlusJakartaSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF0F172A)
                        )

                        // Status pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(progressColor.copy(alpha = 0.08f))
                                .border(1.dp, progressColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                .testTag("status_pill")
                        ) {
                            Text(
                                text = lastScan.overallAssessment,
                                fontFamily = PlusJakartaSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = progressColor
                            )
                        }

                        // Created At Timestamp (curated and clean)
                        val cleanTime = lastScan.createdAt.replace("T", " ").replace("Z", "")
                        Text(
                            text = "Scanned on $cleanTime",
                            fontFamily = PlusJakartaSansFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Divider(color = Color(0xFFF1F5F9))

                // Three Stats Cards Area
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Card 1: Issues Flagged
                    StatCard(
                        title = "Issues Flagged",
                        value = "${lastScan.inconsistencies.size}",
                        accentColor = Color(0xFFF59E0B), // Amber
                        modifier = Modifier.weight(1f).testTag("stat_card_issues")
                    )

                    // Card 2: Citations Checked
                    StatCard(
                        title = "Citations Checked",
                        value = "${lastScan.references.size}",
                        accentColor = Color(0xFF64748B), // Neutral Slate
                        modifier = Modifier.weight(1f).testTag("stat_card_citations_checked")
                    )

                    // Card 3: Citations Flagged
                    val invalidCitationsCount = lastScan.references.count { it.linkStatus != LinkStatus.VALIDATED }
                    StatCard(
                        title = "Citations Flagged",
                        value = "$invalidCitationsCount",
                        accentColor = Color(0xFF10B981), // Green accent as requested
                        modifier = Modifier.weight(1f).testTag("stat_card_citations_flagged")
                    )
                }

                // View Full Report Navigation
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // Find corresponding URL from sample or use demo URL
                            val url = "https://docs.google.com/document/d/demo_manuscript"
                            onNavigateToDetails(lastScan.coherenceScore, url, lastScan.missingSections)
                        }
                        .padding(vertical = 4.dp)
                        .testTag("view_full_report_button"),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "View Full Report",
                        fontFamily = PlusJakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF4F46E5)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Arrow Forward",
                        tint = Color(0xFF4F46E5),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Document History
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Document History",
                fontFamily = PlayfairDisplayFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF0F172A)
            )

            // Scrollable List of History Items
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_history_list"),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                history.forEachIndexed { index, scan ->
                    val docTitle = "Chapter ${history.size - index}"
                    val docUrl = "https://docs.google.com/document/d/chapter${history.size - index}"

                    val scoreColor = when {
                        scan.coherenceScore >= 80 -> Color(0xFF10B981)
                        scan.coherenceScore >= 50 -> Color(0xFFF59E0B)
                        else -> Color(0xFFF43F5E)
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                            .clickable {
                                onNavigateToDetails(scan.coherenceScore, docUrl, scan.missingSections)
                            }
                            .testTag("history_item_$index"),
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = docTitle,
                                    fontFamily = PlusJakartaSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = scan.createdAt.replace("T", " ").replace("Z", ""),
                                    fontFamily = PlusJakartaSansFontFamily,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(scoreColor.copy(alpha = 0.08f))
                                    .border(1.dp, scoreColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${scan.coherenceScore}",
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = scoreColor
                                )
                            }
                        }
                    }
                }
            }

            // View All navigation button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToHistory() }
                    .padding(vertical = 4.dp)
                    .testTag("view_all_history_button"),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "View All",
                    fontFamily = PlusJakartaSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF4F46E5)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Arrow Forward",
                    tint = Color(0xFF4F46E5),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFF8FAFC)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title.uppercase(),
                fontFamily = PlusJakartaSansFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                color = Color(0xFF64748B),
                letterSpacing = 0.5.sp,
                maxLines = 1
            )
            Text(
                text = value,
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = accentColor
            )
        }
    }
}
