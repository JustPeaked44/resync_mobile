package com.example.presentation.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.DetailedHistoryScan
import com.example.data.local.MockScanDatabase
import com.example.data.remote.dto.InconsistencyItem
import com.example.data.remote.dto.Severity
import com.example.domain.usecase.CompareScanInput
import com.example.domain.usecase.CompareScansUseCase
import com.example.domain.usecase.ScanComparisonResult
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.PlayfairDisplayFontFamily
import com.example.ui.theme.PlusJakartaSansFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanComparisonScreen(
    scanIds: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Parse the scan IDs
    val selectedScans = remember(scanIds) {
        val ids = scanIds.split(",")
        MockScanDatabase.scans.filter { it.id in ids }
            .sortedBy { it.date } // Chronological order
    }

    if (selectedScans.size < 2) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Comparison", fontFamily = PlayfairDisplayFontFamily) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Please select at least two versions to compare.",
                    fontFamily = PlusJakartaSansFontFamily,
                    fontSize = 16.sp,
                    color = Color.Red
                )
            }
        }
        return
    }

    // Interactive Base & Target selectors
    var baseScanIndex by remember { mutableStateOf(0) }
    var targetScanIndex by remember { mutableStateOf(selectedScans.size - 1) }

    var showBaseDropdown by remember { mutableStateOf(false) }
    var showTargetDropdown by remember { mutableStateOf(false) }

    val baseScan = selectedScans[baseScanIndex]
    val targetScan = selectedScans[targetScanIndex]

    // UseCase execution
    val comparisonResult = remember(baseScan, targetScan) {
        val useCase = CompareScansUseCase()
        useCase(
            baseScan = CompareScanInput(
                id = baseScan.id,
                title = baseScan.title,
                coherenceScore = baseScan.score,
                url = baseScan.url,
                date = baseScan.date,
                inconsistencies = baseScan.inconsistencies
            ),
            targetScan = CompareScanInput(
                id = targetScan.id,
                title = targetScan.title,
                coherenceScore = targetScan.score,
                url = targetScan.url,
                date = targetScan.date,
                inconsistencies = targetScan.inconsistencies
            )
        )
    }

    var selectedTab by remember { mutableStateOf("Resolved") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF8FAFC), // Background Slate (#F8FAFC)
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Version Comparison",
                            fontFamily = PlayfairDisplayFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = selectedScans.firstOrNull()?.title?.substringBefore(" (") ?: "Manuscript Analysis",
                            fontFamily = PlusJakartaSansFontFamily,
                            fontSize = 12.sp,
                            color = Color(0xFF64748B),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("comparison_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF0F172A)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF0F172A)
                ),
                modifier = Modifier.border(1.dp, Color(0xFFE2E8F0))
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Section 1: Chronological Version Timeline
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                    .testTag("comparison_timeline_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Score Evolution Timeline",
                        fontFamily = PlusJakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF4F46E5)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        selectedScans.forEachIndexed { index, scan ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                val nodeColor = when {
                                    scan.score >= 80 -> Color(0xFF10B981) // Emerald
                                    scan.score >= 50 -> Color(0xFFF59E0B) // Amber
                                    else -> Color(0xFFF43F5E) // Rose
                                }

                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(nodeColor.copy(alpha = 0.08f))
                                        .border(2.dp, nodeColor, RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${scan.score}%",
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = nodeColor
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = scan.date,
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 10.sp,
                                    color = Color(0xFF64748B)
                                )

                                Text(
                                    text = if (scan.title.contains("(")) {
                                        scan.title.substringAfter("(").substringBefore(")")
                                    } else {
                                        "V${index + 1}"
                                    },
                                    fontFamily = PlusJakartaSansFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp,
                                    color = Color(0xFF0F172A),
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            if (index < selectedScans.size - 1) {
                                val nextScan = selectedScans[index + 1]
                                val isPositive = nextScan.score >= scan.score
                                val lineColor = if (isPositive) Color(0xFF10B981) else Color(0xFFF43F5E)

                                Canvas(
                                    modifier = Modifier
                                        .weight(0.5f)
                                        .height(2.dp)
                                ) {
                                    drawLine(
                                        color = lineColor,
                                        start = androidx.compose.ui.geometry.Offset(0f, size.height / 2),
                                        end = androidx.compose.ui.geometry.Offset(size.width, size.height / 2),
                                        strokeWidth = 4f,
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section 2: Version comparison selectors
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Compare Specific Scans",
                        fontFamily = PlusJakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF0F172A)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Base Scan Selector
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Base Version",
                                fontFamily = PlusJakartaSansFontFamily,
                                fontSize = 11.sp,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF1F5F9))
                                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                                        .clickable { showBaseDropdown = true }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                        .testTag("base_scan_selector"),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = baseScan.date,
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = Color(0xFF64748B)
                                    )
                                }
                                DropdownMenu(
                                    expanded = showBaseDropdown,
                                    onDismissRequest = { showBaseDropdown = false },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    selectedScans.forEachIndexed { index, scan ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = "${scan.date} (${scan.score}%)",
                                                    fontFamily = JetBrainsMonoFontFamily,
                                                    fontSize = 12.sp
                                                )
                                            },
                                            onClick = {
                                                baseScanIndex = index
                                                showBaseDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Target Scan Selector
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Target Version",
                                fontFamily = PlusJakartaSansFontFamily,
                                fontSize = 11.sp,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF1F5F9))
                                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                                        .clickable { showTargetDropdown = true }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                        .testTag("target_scan_selector"),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = targetScan.date,
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = Color(0xFF64748B)
                                    )
                                }
                                DropdownMenu(
                                    expanded = showTargetDropdown,
                                    onDismissRequest = { showTargetDropdown = false },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    selectedScans.forEachIndexed { index, scan ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = "${scan.date} (${scan.score}%)",
                                                    fontFamily = JetBrainsMonoFontFamily,
                                                    fontSize = 12.sp
                                                )
                                            },
                                            onClick = {
                                                targetScanIndex = index
                                                showTargetDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section 3: Delta Coherence Score Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                    .testTag("comparison_delta_card")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val delta = comparisonResult.scoreDelta
                    val isPositive = delta >= 0
                    val deltaColor = if (isPositive) Color(0xFF10B981) else Color(0xFFF43F5E)
                    val deltaSymbol = if (isPositive) "+" else ""

                    Text(
                        text = "COHERENCE VARIATION",
                        fontFamily = PlusJakartaSansFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        letterSpacing = 1.5.sp
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${comparisonResult.baseScore}%",
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = "Base",
                                fontFamily = PlusJakartaSansFontFamily,
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(deltaColor.copy(alpha = 0.08f))
                                .border(1.dp, deltaColor.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "$deltaSymbol$delta%",
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 32.sp,
                                color = deltaColor
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${comparisonResult.targetScore}%",
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = Color(0xFF4F46E5)
                            )
                            Text(
                                text = "Target",
                                fontFamily = PlusJakartaSansFontFamily,
                                fontSize = 11.sp,
                                color = Color(0xFF4F46E5)
                            )
                        }
                    }

                    val improvementSummary = if (isPositive) {
                        "Your manuscript has improved by $delta points! This demonstrates highly strategic coherence development since ${baseScan.date}."
                    } else {
                        "Your manuscript score decreased by ${-delta} points. Consider reviewing newly introduced logical gaps or redundancies below."
                    }

                    Text(
                        text = improvementSummary,
                        fontFamily = PlusJakartaSansFontFamily,
                        fontSize = 13.sp,
                        color = Color(0xFF334155),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }

            // Section 4: Inconsistencies Tabs (Resolved, Unresolved, New)
            val tabList = listOf("Resolved", "Unresolved", "New")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE2E8F0))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                tabList.forEach { tab ->
                    val isSelected = selectedTab == tab
                    val tabColor = when (tab) {
                        "Resolved" -> Color(0xFF10B981) // Green
                        "New" -> Color(0xFFF43F5E) // Red
                        else -> Color(0xFFF59E0B) // Amber
                    }
                    val count = when (tab) {
                        "Resolved" -> comparisonResult.resolvedInconsistencies.size
                        "Unresolved" -> comparisonResult.unresolvedInconsistencies.size
                        else -> comparisonResult.newInconsistencies.size
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color.White else Color.Transparent)
                            .clickable { selectedTab = tab }
                            .testTag("compare_tab_$tab"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "$tab ($count)",
                                fontFamily = PlusJakartaSansFontFamily,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = if (isSelected) tabColor else Color(0xFF64748B)
                            )
                        }
                    }
                }
            }

            // Tabs content
            val inconsistenciesToShow = when (selectedTab) {
                "Resolved" -> comparisonResult.resolvedInconsistencies
                "Unresolved" -> comparisonResult.unresolvedInconsistencies
                else -> comparisonResult.newInconsistencies
            }

            if (inconsistenciesToShow.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = when (selectedTab) {
                                "Resolved" -> Icons.Default.Info
                                "New" -> Icons.Default.CheckCircle
                                else -> Icons.Default.CheckCircle
                            },
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = when (selectedTab) {
                                "Resolved" -> "No issues resolved in this revision step."
                                "New" -> "Perfect! No new inconsistencies were introduced."
                                else -> "Amazing! No unresolved issues remain."
                            },
                            fontFamily = PlusJakartaSansFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    inconsistenciesToShow.forEach { item ->
                        val borderAccent = when (selectedTab) {
                            "Resolved" -> Color(0xFF10B981)
                            "New" -> Color(0xFFF43F5E)
                            else -> Color(0xFFF59E0B)
                        }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(borderAccent.copy(alpha = 0.08f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = item.type.name.replace("_", " "),
                                            fontFamily = JetBrainsMonoFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            color = borderAccent
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFFF1F5F9))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = item.severity.name,
                                            fontFamily = PlusJakartaSansFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            color = when (item.severity) {
                                                Severity.HIGH -> Color(0xFFF43F5E)
                                                Severity.MEDIUM -> Color(0xFFF59E0B)
                                                Severity.LOW -> Color(0xFF64748B)
                                            }
                                        )
                                    }
                                }

                                Text(
                                    text = item.description,
                                    fontFamily = PlusJakartaSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF0F172A)
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF8FAFC))
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = Color(0xFF64748B),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "Locations in Manuscript",
                                            fontFamily = PlusJakartaSansFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = Color(0xFF475569)
                                        )
                                        Text(
                                            text = "${item.sectionA} • ${item.sectionB}",
                                            fontFamily = PlusJakartaSansFontFamily,
                                            fontSize = 12.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = "RECOMMENDED ACTION",
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = Color(0xFF64748B)
                                    )
                                    Text(
                                        text = item.recommendedCorrection,
                                        fontFamily = PlusJakartaSansFontFamily,
                                        fontSize = 13.sp,
                                        color = Color(0xFF334155),
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
