package com.example.presentation.dashboard

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.geometry.Offset
import androidx.compose.material3.LocalTextStyle
import com.example.ui.theme.PlayfairDisplayFontFamily
import com.example.ui.theme.PlusJakartaSansFontFamily
import com.example.ui.theme.JetBrainsMonoFontFamily

@Composable
fun ResultDetailsScreen(
    coherenceScore: Int,
    url: String,
    missingSections: List<String> = emptyList(),
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var selectedTab by remember { mutableStateOf("Overview") }
    var expandedSuggestions by remember { mutableStateOf(setOf<Int>()) }
    var expandedInconsistencies by remember { mutableStateOf(setOf<Int>()) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showPermissionExplanationDialog by remember { mutableStateOf(false) }

    // Navigation and highlighting state maps
    val cardYOffsets = remember { mutableStateMapOf<String, Float>() }
    val previewLazyListState = rememberLazyListState()

    // 0. Locate matched scan from MockScanDatabase
    val matchedScan = remember(url, coherenceScore) {
        com.example.data.local.MockScanDatabase.scans.firstOrNull { it.url == url && it.score == coherenceScore }
            ?: com.example.data.local.MockScanDatabase.scans.firstOrNull { it.url == url }
            ?: com.example.data.local.MockScanDatabase.scans.firstOrNull { it.score == coherenceScore }
    }

    val manuscriptText = remember(matchedScan) {
        matchedScan?.manuscriptText ?: ""
    }

    // Helper data structures for paragraph mapping
    val paragraphInfos = remember(manuscriptText) {
        var currentOffset = 0
        manuscriptText.split("\n").mapIndexed { idx, pText ->
            val start = currentOffset
            val end = start + pText.length
            currentOffset = end + 1 // +1 for the newline character
            ParagraphInfo(idx, pText, start, end)
        }
    }


    // Dynamic Title based on URL to feel rich & integrated
    val documentTitle = remember(url) {
        when {
            url.contains("chapter1") -> "Novel Draft - Chapter 1"
            url.contains("essay") -> "Creative Writing Essay"
            url.contains("redux") -> "Manuscript Draft Redux"
            url.contains("poetry") -> "Poetry Anthology"
            url.contains("demo") -> "Q3 Research Alignment Manuscript"
            url.isNotBlank() && url.startsWith("http") -> "Strategic Project Draft"
            else -> "Strategic Coherence Draft"
        }
    }

    val analysisDate = "July 2, 2026"

    val scoreColor = when {
        coherenceScore >= 80 -> Color(0xFF10B981) // Success Emerald
        coherenceScore >= 50 -> Color(0xFFF59E0B) // Warning Amber
        else -> Color(0xFFEF4444) // Error Rose Red
    }

    val scoreStatusText = when {
        coherenceScore >= 80 -> "Excellent Coherence"
        coherenceScore >= 50 -> "Moderate Coherence"
        else -> "Attention Required"
    }

    // 1. Executive Diagnosis Text based on Core Score Tiers
    val executiveDiagnosis = when {
        coherenceScore >= 80 -> "Your manuscript exhibits exceptional structural integrity, smooth stylistic transitions, and highly consistent narrative threads. Ideas flow seamlessly from paragraph to paragraph, establishing a high level of clarity and reader engagement. Minor polishing of references is recommended, but the strategic logical path remains pristine."
        coherenceScore >= 50 -> "Your manuscript shows clear structure but suffers from minor pacing issues and occasional stylistic discontinuities. While the overall logic is sound, some section transitions require additional refinement for optimal reading flow. Specifically, chronological alignment between chapters and unified definitions need adjustment."
        else -> "Your manuscript has significant structural gaps, fragmented paragraph transitions, or stylistic shifts that disrupt readability. We highly recommend revising sentence flows, reconciling conflicting strategic directions, and aligning thematic components. A comprehensive review of external links is required to fix broken references."
    }

    // 2. Inconsistencies List based on Tiers
    val inconsistenciesList = remember(matchedScan, coherenceScore) {
        if (matchedScan != null) {
            matchedScan.inconsistencies.map { item ->
                DetailedInconsistency(
                    mappingHeader = "${item.sectionA} ➔ ${item.sectionB}",
                    severity = when (item.severity) {
                        com.example.data.remote.dto.Severity.HIGH -> "High"
                        com.example.data.remote.dto.Severity.MEDIUM -> "Medium"
                        com.example.data.remote.dto.Severity.LOW -> "Low"
                    },
                    description = item.description,
                    recommendedCorrection = item.recommendedCorrection,
                    explanation = item.explanation?.let {
                        DetailedExplanation(
                            whatWasFound = it.whatWasFound,
                            whyItMatters = it.whyItMatters,
                            suggestedFix = it.suggestedFix
                        )
                    },
                    startCharOffsetA = item.startCharOffsetA,
                    endCharOffsetA = item.endCharOffsetA,
                    startCharOffsetB = item.startCharOffsetB,
                    endCharOffsetB = item.endCharOffsetB,
                    type = item.type
                )
            }
        } else if (coherenceScore >= 80) {
            listOf(
                DetailedInconsistency(
                    mappingHeader = "Chapter 1 ➔ Chapter 2",
                    severity = "Low",
                    description = "Pronoun reference in Section 1.4 is slightly ambiguous when referring to the core system's operators versus system developers.",
                    recommendedCorrection = "Clarify the pronoun 'they' by replacing it explicitly with 'the system operators' to maintain absolute precision for external auditing teams.",
                    explanation = DetailedExplanation(
                        whatWasFound = "Section 1.4 uses 'they' after mentioning both operators and developers in the previous sentence.",
                        whyItMatters = "Ambiguous pronouns in technical specs can lead to misassigned responsibilities during implementation.",
                        suggestedFix = "Explicitly write 'the system operators' instead of 'they'."
                    )
                )
            )
        } else if (coherenceScore >= 50) {
            listOf(
                DetailedInconsistency(
                    mappingHeader = "Chapter 1 ➔ Chapter 3",
                    severity = "Medium",
                    description = "The target public launch scheduled for Q3 2026 in Chapter 1 contradicts the prerequisite system integration deadline of October 2026 specified in Chapter 3.",
                    recommendedCorrection = "Adjust the system integration milestone to August 2026, or push the public release target to Q4 2026 to preserve a chronologically valid strategy.",
                    explanation = DetailedExplanation(
                        whatWasFound = "Chapter 1 sets public launch to Q3 2026, but Chapter 3 says system integration won't finish until October 2026.",
                        whyItMatters = "You cannot launch a product before its core integration is completed. This invalidates the strategic timeline.",
                        suggestedFix = "Align the timeline: push the launch to Q4 or accelerate the integration to August."
                    )
                ),
                DetailedInconsistency(
                    mappingHeader = "Chapter 2 ➔ Chapter 4",
                    severity = "Medium",
                    description = "Sudden narrative tone transition. The prose shifts from a highly objective, data-driven perspective in corporate planning to an informal, first-person conversational voice.",
                    recommendedCorrection = "Revise Chapter 4 to utilize objective passive voice structures, removing informal personal anecdotes to maintain brand authority.",
                    explanation = DetailedExplanation(
                        whatWasFound = "Chapter 4 includes phrases like 'I really think we should...' and 'our guys saw...', contrasting with Chapter 2's formal tone.",
                        whyItMatters = "Inconsistent tones disrupt the reader's immersion and diminish the perceived professionalism of the document.",
                        suggestedFix = "Rewrite the informal sections of Chapter 4 to match the objective tone of the preceding chapters."
                    )
                )
            )
        } else {
            listOf(
                DetailedInconsistency(
                    mappingHeader = "Chapter 1 ➔ Chapter 4",
                    severity = "High",
                    description = "Direct financial contradiction. Chapter 1 outlines an absolute priority to slash cloud infrastructure expenditures by 45%, while Chapter 4 details an immediate plan to scale physical database hosting footprint by 300%.",
                    recommendedCorrection = "Reconcile these goals. Explore automated multi-region server optimization frameworks that scale dynamically without expanding capital hosting investments.",
                    explanation = DetailedExplanation(
                        whatWasFound = "Chapter 1 demands a 45% infrastructure cost reduction; Chapter 4 budgets for a 300% physical hosting expansion.",
                        whyItMatters = "These opposing financial directives will paralyze budgetary approval processes.",
                        suggestedFix = "Clarify whether the physical expansion is exempt from the cost-cutting directive or revise the hosting strategy."
                    )
                ),
                DetailedInconsistency(
                    mappingHeader = "Chapter 2 ➔ Chapter 3",
                    severity = "High",
                    description = "Methodological contradiction. The introductory pages claim a double-blind randomized clinical trial format, but the data collection details an observational retrospective review.",
                    recommendedCorrection = "Enforce a single unified research methodology. Re-align the description blocks to match the chosen trial format's exact legal compliance constraints.",
                    explanation = DetailedExplanation(
                        whatWasFound = "Chapter 2 describes a 'double-blind RCT' while Chapter 3 details 'retrospective chart reviews'.",
                        whyItMatters = "The study design dictates the level of evidence. Mixing them up invalidates the entire methodology section.",
                        suggestedFix = "Select the actual methodology used and rewrite the conflicting chapter to reflect it."
                    )
                ),
                DetailedInconsistency(
                    mappingHeader = "Chapter 3 ➔ Chapter 4",
                    severity = "Medium",
                    description = "The Core Architectural Framework (CAF) is referenced repeatedly as the main design authority, but its specific parameters and definitions are never introduced.",
                    recommendedCorrection = "Include a dedicated framework overview section in Chapter 3 or add an active, verified citation pointing to the official CAF specifications doc.",
                    explanation = DetailedExplanation(
                        whatWasFound = "The acronym 'CAF' is used multiple times without a prior definition or citation.",
                        whyItMatters = "Undefined critical acronyms confuse readers who aren't familiar with internal organizational terminology.",
                        suggestedFix = "Define 'Core Architectural Framework (CAF)' upon its first use and briefly outline its scope."
                    )
                )
            )
        }
    }

    val suggestionsList = remember(coherenceScore) {
        if (coherenceScore >= 80) {
            listOf(
                DetailedSuggestion(
                    title = "Unified Terminology Definitions",
                    diagnosticRationale = "The term 'Core Engine' is introduced in Chapter 1 but abbreviated as 'CE' in Chapter 2 without a preceding parenthetical definition.",
                    improvementRemedy = "Ensure 'Core Engine (CE)' is defined on its very first usage to establish cohesive comprehension."
                ),
                DetailedSuggestion(
                    title = "Active Link Citations",
                    diagnosticRationale = "The secondary user validation sheets are referenced implicitly, but the text lacks a direct citation index.",
                    improvementRemedy = "Append a citation tag pointing directly to the validation sheets in the appendix."
                )
            )
        } else if (coherenceScore >= 50) {
            listOf(
                DetailedSuggestion(
                    title = "Chronological Sequence Alignment",
                    diagnosticRationale = "A professional strategy roadmap should maintain a strictly linear timeline to establish logical credibility for readers.",
                    improvementRemedy = "Perform a master timeline sync across all planning files to ensure prerequisite dependencies precede final execution."
                ),
                DetailedSuggestion(
                    title = "Stylistic and Tone Consolidation",
                    diagnosticRationale = "A strategy manuscript requires a unified authorial presence to avoid distracting the reader from core arguments.",
                    improvementRemedy = "Enforce a formal, third-person perspective and prune casual prose structures across chapters."
                )
            )
        } else {
            listOf(
                DetailedSuggestion(
                    title = "Strategic Contradiction Reconciliation",
                    diagnosticRationale = "Direct strategic contradictions undermine the validity of the manuscript and create severe operational confusion.",
                    improvementRemedy = "Perform a collaborative review between the financial and technical teams to align goals before editing."
                ),
                DetailedSuggestion(
                    title = "Methodological Uniformity Audit",
                    diagnosticRationale = "Mixing study designs compromises compliance credibility and introduces severe structural invalidity.",
                    improvementRemedy = "Strictly define the study boundaries and prune details that belong to alternate study designs."
                ),
                DetailedSuggestion(
                    title = "Glossary & Framework Onboarding",
                    diagnosticRationale = "Unexplained acronyms and proprietary frameworks alienate external auditors and target readers.",
                    improvementRemedy = "Provide comprehensive glossary entries and external links on first use."
                )
            )
        }
    }

    // 4. References List based on Tiers
    val referencesList = remember(matchedScan, coherenceScore) {
        if (matchedScan != null) {
            matchedScan.references.map { item ->
                DetailedReference(
                    text = item.citationText,
                    status = when (item.linkStatus) {
                        com.example.data.remote.dto.LinkStatus.VALIDATED -> "Validated"
                        com.example.data.remote.dto.LinkStatus.UNRESOLVED -> "Unresolved"
                        com.example.data.remote.dto.LinkStatus.BROKEN -> "Broken"
                    },
                    url = item.citationText,
                    explanation = item.detailedExplanation,
                    startCharOffset = item.startCharOffset,
                    endCharOffset = item.endCharOffset
                )
            }
        } else if (coherenceScore >= 80) {
            listOf(
                DetailedReference(
                    text = "Q2 Financial Strategy Board",
                    status = "Validated",
                    url = "https://docs.google.com/spreadsheets/d/finance_q2",
                    explanation = "Fully synchronized, accessible, and contains correct strategic schemas."
                ),
                DetailedReference(
                    text = "User Validation Survey Sheets",
                    status = "Validated",
                    url = "https://docs.google.com/spreadsheets/d/survey_results",
                    explanation = "Active public link verified against user survey database integrity rules."
                )
            )
        } else if (coherenceScore >= 50) {
            listOf(
                DetailedReference(
                    text = "Steering Committee Milestones",
                    status = "Unresolved",
                    url = "https://docs.google.com/document/d/committee_milestones",
                    explanation = "The document exists, but the timeline was updated last month and needs manual re-verification."
                ),
                DetailedReference(
                    text = "Drafting Guide 2025 v1",
                    status = "Validated",
                    url = "https://docs.google.com/document/d/guide_draft_v1",
                    explanation = "Fully verified and compliant with latest stylistic rules."
                )
            )
        } else {
            listOf(
                DetailedReference(
                    text = "Core Architectural Framework Specs",
                    status = "Broken",
                    url = "https://docs.google.com/document/d/caf_specs_404",
                    explanation = "The linked resource returned a 404 error. The reference document has been deleted or moved."
                ),
                DetailedReference(
                    text = "Clinical Trial Patient Log",
                    status = "Unresolved",
                    url = "https://docs.google.com/spreadsheets/d/trial_logs",
                    explanation = "The link is active but requires restricted credentials. External auditors cannot verify compliance."
                ),
                DetailedReference(
                    text = "Strategic Goals Appendix",
                    status = "Validated",
                    url = "https://docs.google.com/document/d/align_objectives",
                    explanation = "Fully verified and synced with the team shared drive."
                )
            )
        }
    }

    // 5. Duplications List based on Tiers
    val duplicationsList = remember(matchedScan, coherenceScore) {
        if (matchedScan != null) {
            matchedScan.duplicateSections
        } else if (coherenceScore >= 80) {
            emptyList()
        } else if (coherenceScore >= 50) {
            listOf(
                com.example.data.remote.dto.DuplicateItem(
                    sectionA = "Section 2.1 Background",
                    sectionB = "Section 4.1 Discussion",
                    similarityScore = 0.88,
                    matchedText = "The preliminary results indicate a strong correlation between the variables, suggesting that the initial hypothesis holds true under controlled conditions."
                )
            )
        } else {
            listOf(
                com.example.data.remote.dto.DuplicateItem(
                    sectionA = "Abstract",
                    sectionB = "Executive Summary",
                    similarityScore = 0.95,
                    matchedText = "This research explores the fundamental challenges in distributed system scalability, proposing a novel approach to latency mitigation through localized caching strategies."
                ),
                com.example.data.remote.dto.DuplicateItem(
                    sectionA = "Chapter 1",
                    sectionB = "Conclusion",
                    similarityScore = 0.89,
                    matchedText = "Ultimately, the infrastructure costs can be reduced by 45% without compromising the overall system reliability or throughput."
                )
            )
        }
    }

    val highlights = remember(matchedScan, inconsistenciesList, referencesList, duplicationsList) {
        val list = mutableListOf<HighlightSpan>()

        // Map Inconsistencies
        inconsistenciesList.forEachIndexed { index, item ->
            val type = when (item.type) {
                com.example.data.remote.dto.InconsistencyType.CONTRADICTION -> HighlightType.CONTRADICTION
                com.example.data.remote.dto.InconsistencyType.REDUNDANCY -> HighlightType.REDUNDANCY
                com.example.data.remote.dto.InconsistencyType.LOGIC_GAP -> HighlightType.LOGIC_GAP
                com.example.data.remote.dto.InconsistencyType.TERMINOLOGY_CLASH -> HighlightType.TERMINOLOGY_CLASH
            }
            // Map Section A
            if (item.startCharOffsetA != null && item.endCharOffsetA != null) {
                list.add(
                    HighlightSpan(
                        id = "incons_a_$index",
                        type = type,
                        startOffset = item.startCharOffsetA,
                        endOffset = item.endCharOffsetA,
                        label = "Inconsistency (A)",
                        targetIndex = index
                    )
                )
            }
            // Map Section B
            if (item.startCharOffsetB != null && item.endCharOffsetB != null) {
                list.add(
                    HighlightSpan(
                        id = "incons_b_$index",
                        type = type,
                        startOffset = item.startCharOffsetB,
                        endOffset = item.endCharOffsetB,
                        label = "Inconsistency (B)",
                        targetIndex = index
                    )
                )
            }
        }

        // Map Duplications
        duplicationsList.forEachIndexed { index, item ->
            if (item.startCharOffsetA != null && item.endCharOffsetA != null) {
                list.add(
                    HighlightSpan(
                        id = "dup_a_$index",
                        type = HighlightType.DUPLICATION,
                        startOffset = item.startCharOffsetA,
                        endOffset = item.endCharOffsetA,
                        label = "Duplication (A)",
                        targetIndex = index
                    )
                )
            }
            if (item.startCharOffsetB != null && item.endCharOffsetB != null) {
                list.add(
                    HighlightSpan(
                        id = "dup_b_$index",
                        type = HighlightType.DUPLICATION,
                        startOffset = item.startCharOffsetB,
                        endOffset = item.endCharOffsetB,
                        label = "Duplication (B)",
                        targetIndex = index
                    )
                )
            }
        }

        // Map References / Citations
        referencesList.forEachIndexed { index, item ->
            if (item.startCharOffset != null && item.endCharOffset != null) {
                list.add(
                    HighlightSpan(
                        id = "cit_$index",
                        type = HighlightType.CITATION,
                        startOffset = item.startCharOffset,
                        endOffset = item.endCharOffset,
                        label = "Citation",
                        targetIndex = index
                    )
                )
            }
        }

        list.sortedBy { it.startOffset }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = com.example.util.PdfExportUtil.generateAndSaveReportPdf(
                context = context,
                documentTitle = documentTitle,
                coherenceScore = coherenceScore,
                analysisDate = analysisDate,
                missingSections = missingSections,
                inconsistencies = inconsistenciesList,
                references = referencesList
            )
            if (uri != null) {
                coroutineScope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = "Report saved to Downloads folder",
                        actionLabel = "Open",
                        duration = androidx.compose.material3.SnackbarDuration.Long
                    )
                    if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                setDataAndType(uri, "application/pdf")
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "No PDF viewer found", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        } else {
            showPermissionExplanationDialog = true
        }
    }

    fun handlePdfExport() {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            val uri = com.example.util.PdfExportUtil.generateAndSaveReportPdf(
                context = context,
                documentTitle = documentTitle,
                coherenceScore = coherenceScore,
                analysisDate = analysisDate,
                missingSections = missingSections,
                inconsistencies = inconsistenciesList,
                references = referencesList
            )
            if (uri != null) {
                coroutineScope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = "Report saved to Downloads folder",
                        actionLabel = "Open",
                        duration = androidx.compose.material3.SnackbarDuration.Long
                    )
                    if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                setDataAndType(uri, "application/pdf")
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "No PDF viewer found", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(context, "Failed to save PDF", Toast.LENGTH_SHORT).show()
            }
        } else {
            val permissionCheck = androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                val uri = com.example.util.PdfExportUtil.generateAndSaveReportPdf(
                    context = context,
                    documentTitle = documentTitle,
                    coherenceScore = coherenceScore,
                    analysisDate = analysisDate,
                    missingSections = missingSections,
                    inconsistencies = inconsistenciesList,
                    references = referencesList
                )
                if (uri != null) {
                    coroutineScope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = "Report saved to Downloads folder",
                            actionLabel = "Open",
                            duration = androidx.compose.material3.SnackbarDuration.Long
                        )
                        if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(uri, "application/pdf")
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "No PDF viewer found", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } else {
                permissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    if (showPermissionExplanationDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionExplanationDialog = false },
            title = { Text("Storage Permission Required") },
            text = { Text("This app requires storage permission to save the generated PDF report to your Downloads directory on older Android versions.") },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        showPermissionExplanationDialog = false
                        permissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                ) {
                    Text("Grant Permission")
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = { showPermissionExplanationDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF8FAFC), // Background Slate (#F8FAFC)
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                            .testTag("details_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF0F172A)
                        )
                    }

                    Column {
                        Text(
                            text = documentTitle,
                            fontFamily = PlayfairDisplayFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFF0F172A),
                            maxLines = 1,
                            modifier = Modifier.testTag("details_document_title")
                        )
                        Text(
                            text = "Audited on $analysisDate",
                            fontFamily = PlusJakartaSansFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Action Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Download Report PDF Button
                    IconButton(
                        onClick = { handlePdfExport() },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                            .testTag("details_download_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Download Report",
                            tint = Color(0xFF4F46E5)
                        )
                    }

                    // Native Share Button
                    IconButton(
                        onClick = {
                            val shareText = """
                                Resync Strategic Coherence Report
                                =================================
                                Document Title: $documentTitle
                                Source URL: $url
                                Coherence Score: $coherenceScore%
                                Status Assessment: $scoreStatusText
                                
                                Overview Summary:
                                $executiveDiagnosis
                                
                                Audit completed on: $analysisDate
                                Powered by Resync AI Studio Companion Portal
                            """.trimIndent()

                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Share Coherence Report")
                            context.startActivity(shareIntent)
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                            .testTag("details_share_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Report",
                            tint = Color(0xFF4F46E5)
                        )
                    }
                }
            }

            // Custom Segmented Tab Layout (Scrollable for compact screens)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE2E8F0))
                    .horizontalScroll(androidx.compose.foundation.rememberScrollState())
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("Overview", "Preview", "Inconsistencies", "Suggestions", "References", "Duplication").forEach { tab ->
                    val isSelected = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .height(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color.White else Color.Transparent)
                            .border(
                                width = if (isSelected) 1.dp else 0.dp,
                                color = if (isSelected) Color(0xFFCBD5E1) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedTab = tab }
                            .padding(horizontal = 16.dp)
                            .testTag("tab_chip_$tab"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab,
                            fontFamily = PlusJakartaSansFontFamily,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = if (isSelected) Color(0xFF4F46E5) else Color(0xFF64748B)
                        )
                    }
                }
            }

            // Tab Content Rendering
            when (selectedTab) {
                "Overview" -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Core Score Focus Box
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                                .padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ScoreRing(
                                score = coherenceScore,
                                scoreColor = scoreColor,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = scoreStatusText,
                                    fontFamily = PlusJakartaSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = scoreColor,
                                    modifier = Modifier.testTag("details_score_status_text")
                                )
                                Text(
                                    text = "Document integrity check successfully synced",
                                    fontFamily = PlusJakartaSansFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        // Executive Diagnosis Section
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Overview",
                                    tint = Color(0xFF4F46E5),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Executive Diagnosis",
                                    fontFamily = PlusJakartaSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF0F172A)
                                )
                            }

                            // Italic serif type with a left-accent Indigo border
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF5F3FF)) // Soft indigo background
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Left-accent border (4.dp thick bar)
                                Spacer(
                                    modifier = Modifier
                                        .width(4.dp)
                                        .height(110.dp)
                                        .background(Color(0xFF4F46E5))
                                )

                                Text(
                                    text = executiveDiagnosis,
                                    fontFamily = PlayfairDisplayFontFamily,
                                    fontStyle = FontStyle.Italic,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    lineHeight = 22.sp,
                                    color = Color(0xFF334155),
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                            }
                        }

                        // Source Audit Metadata Summary
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Source Audit Properties",
                                fontFamily = PlusJakartaSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF0F172A)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Synchronized Link",
                                    fontFamily = PlusJakartaSansFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.sp,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = if (url.length > 28) url.take(25) + "..." else url,
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = Color(0xFF4F46E5)
                                )
                            }
                        }
                    }
                }

                "Preview" -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Info and Legend Header Card
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "ORIGINAL MANUSCRIPT PREVIEW",
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF4F46E5)
                            )
                            Text(
                                text = "Scroll through the extracted text of your manuscript below. Areas of concern have been color-coded based on the issue type. Tap any highlighted text to jump directly to its detailed explanation card.",
                                fontFamily = PlusJakartaSansFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                color = Color(0xFF64748B)
                            )

                            // Highlights Legend
                            Text(
                                text = "HIGHLIGHT LEGEND:",
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(androidx.compose.foundation.rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val legends = listOf(
                                    Triple("Contradiction", Color(0xFFFEE2E2), Color(0xFF991B1B)),
                                    Triple("Redundancy/Duplication", Color(0xFFFFEDD5), Color(0xFFC2410C)),
                                    Triple("Logic Gap", Color(0xFFF3E8FF), Color(0xFF6B21A8)),
                                    Triple("Terminology Clash", Color(0xFFDBEAFE), Color(0xFF1E40AF)),
                                    Triple("Citation Issue", Color(0xFFFEF9C3), Color(0xFF854D0E))
                                )
                                legends.forEach { (label, bg, text) ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(bg)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = label,
                                            fontFamily = PlusJakartaSansFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = text
                                        )
                                    }
                                }
                            }
                        }

                        // Manuscript Scrollable Pane
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(500.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (missingSections.isNotEmpty()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFFEF2F2))
                                        .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(8.dp))
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Warning",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "Missing Expected Sections: ${missingSections.joinToString(", ")}",
                                        fontFamily = PlusJakartaSansFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF991B1B)
                                    )
                                }
                            }

                            if (manuscriptText.isBlank()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No manuscript text available for preview.",
                                        fontFamily = PlusJakartaSansFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 13.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            } else {
                                LazyColumn(
                                    state = previewLazyListState,
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    itemsIndexed(paragraphInfos) { _, paragraph ->
                                        val annotatedString = remember(paragraph, highlights) {
                                            buildParagraphAnnotatedString(
                                                pText = paragraph.text,
                                                pStart = paragraph.startOffset,
                                                pEnd = paragraph.endOffset,
                                                highlights = highlights
                                            )
                                        }

                                        ClickableText(
                                            text = annotatedString,
                                            style = LocalTextStyle.current.copy(
                                                fontFamily = PlusJakartaSansFontFamily,
                                                fontSize = 14.sp,
                                                lineHeight = 22.sp,
                                                color = Color(0xFF1E293B)
                                            ),
                                            onClick = { offset ->
                                                annotatedString.getStringAnnotations("highlight_click", offset, offset)
                                                    .firstOrNull()?.let { annotation ->
                                                        val clickedId = annotation.item
                                                        // Find the matching HighlightSpan
                                                        highlights.firstOrNull { it.id == clickedId }?.let { span ->
                                                            val tabToSelect = when (span.type) {
                                                                HighlightType.CONTRADICTION,
                                                                HighlightType.REDUNDANCY,
                                                                HighlightType.LOGIC_GAP,
                                                                HighlightType.TERMINOLOGY_CLASH -> "Inconsistencies"
                                                                HighlightType.DUPLICATION -> "Duplication"
                                                                HighlightType.CITATION -> "References"
                                                            }
                                                            selectedTab = tabToSelect
                                                            coroutineScope.launch {
                                                                delay(150) // Wait for tab change layout pass
                                                                val targetY = cardYOffsets[span.id]
                                                                if (targetY != null) {
                                                                    scrollState.animateScrollTo(targetY.toInt())
                                                                }
                                                            }
                                                        }
                                                    }
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                "Inconsistencies" -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (missingSections.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFFEF2F2))
                                    .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(12.dp))
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Warning",
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(24.dp)
                                )
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = "MISSING STRUCTURAL COMPONENTS",
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFFEF4444)
                                    )
                                    Text(
                                        text = "This manuscript is missing the following expected sections: ${missingSections.joinToString(", ")}.",
                                        fontFamily = PlusJakartaSansFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp,
                                        color = Color(0xFF7F1D1D)
                                    )
                                    Text(
                                        text = "Please add these sections to comply with the selected research type.",
                                        fontFamily = PlusJakartaSansFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp,
                                        color = Color(0xFFB91C1C)
                                    )
                                }
                            }
                        }

                        inconsistenciesList.forEachIndexed { index, inconsistency ->
                            val severityColor = when (inconsistency.severity) {
                                "High" -> Color(0xFFEF4444)
                                "Medium" -> Color(0xFFF59E0B)
                                else -> Color(0xFF3B82F6) // Low/Info
                            }

                            val isExpanded = expandedInconsistencies.contains(index)

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                    .onGloballyPositioned { coords ->
                                        val parentCoords = coords.parentLayoutCoordinates
                                        if (parentCoords != null) {
                                            val localPos = parentCoords.localPositionOf(coords, Offset.Zero)
                                            cardYOffsets["incons_a_$index"] = localPos.y
                                            cardYOffsets["incons_b_$index"] = localPos.y
                                        }
                                    }
                                    .clickable {
                                        expandedInconsistencies = if (isExpanded) {
                                            expandedInconsistencies - index
                                        } else {
                                            expandedInconsistencies + index
                                        }
                                    }
                                    .padding(18.dp)
                                    .testTag("inconsistency_card_$index"),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Header: Mapping and Severity Pill
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = inconsistency.mappingHeader,
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF4F46E5)
                                    )

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Severity Pill
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(severityColor.copy(alpha = 0.08f))
                                                .border(1.dp, severityColor.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = inconsistency.severity,
                                                fontFamily = JetBrainsMonoFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp,
                                                color = severityColor
                                            )
                                        }
                                        Icon(
                                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                            contentDescription = if (isExpanded) "Collapse details" else "Expand details",
                                            tint = Color(0xFF94A3B8),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                // Issue description
                                Text(
                                    text = inconsistency.description,
                                    fontFamily = PlusJakartaSansFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    color = Color(0xFF334155)
                                )

                                // Expandable Explanation Section
                                androidx.compose.animation.AnimatedVisibility(visible = isExpanded) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        inconsistency.explanation?.let { explanation ->
                                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Text(
                                                    text = "WHAT WAS FOUND",
                                                    fontFamily = JetBrainsMonoFontFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    color = Color(0xFF64748B)
                                                )
                                                Text(
                                                    text = explanation.whatWasFound,
                                                    fontFamily = PlusJakartaSansFontFamily,
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 13.sp,
                                                    lineHeight = 18.sp,
                                                    color = Color(0xFF475569)
                                                )
                                            }

                                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Text(
                                                    text = "WHY IT MATTERS",
                                                    fontFamily = JetBrainsMonoFontFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    color = Color(0xFF64748B)
                                                )
                                                Text(
                                                    text = explanation.whyItMatters,
                                                    fontFamily = PlusJakartaSansFontFamily,
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 13.sp,
                                                    lineHeight = 18.sp,
                                                    color = Color(0xFF475569)
                                                )
                                            }
                                            
                                            // Highlighted Indigo-bordered Recommended Correction Box (moved inside explanation if expanded)
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color(0xFFEEF2FF)) // Indigo tint
                                                    .border(1.dp, Color(0xFF4F46E5), RoundedCornerShape(8.dp)) // Indigo border
                                                    .padding(12.dp),
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Text(
                                                    text = "RECOMMENDED CORRECTION",
                                                    fontFamily = JetBrainsMonoFontFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    color = Color(0xFF4F46E5),
                                                    letterSpacing = 0.5.sp
                                                )
                                                Text(
                                                    text = explanation.suggestedFix,
                                                    fontFamily = PlusJakartaSansFontFamily,
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 13.sp,
                                                    lineHeight = 18.sp,
                                                    color = Color(0xFF1E1B4B)
                                                )
                                            }
                                        }
                                    }
                                }
                                
                                // Show simplified correction if not expanded and explanation exists, or just the original if no explanation
                                androidx.compose.animation.AnimatedVisibility(visible = !isExpanded) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFEEF2FF)) // Indigo tint
                                            .border(1.dp, Color(0xFF4F46E5), RoundedCornerShape(8.dp)) // Indigo border
                                            .padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "RECOMMENDED CORRECTION",
                                            fontFamily = JetBrainsMonoFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = Color(0xFF4F46E5),
                                            letterSpacing = 0.5.sp
                                        )
                                        Text(
                                            text = inconsistency.recommendedCorrection,
                                            fontFamily = PlusJakartaSansFontFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 13.sp,
                                            lineHeight = 18.sp,
                                            color = Color(0xFF1E1B4B)
                                        )
                                    }
                                }

                                if (inconsistency.startCharOffsetA != null) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        TextButton(
                                            onClick = {
                                                coroutineScope.launch {
                                                    selectedTab = "Preview"
                                                    delay(150)
                                                    val targetOffset = inconsistency.startCharOffsetA ?: 0
                                                    val targetParagraphIndex = paragraphInfos.indexOfFirst { p ->
                                                        targetOffset >= p.startOffset && targetOffset <= p.endOffset
                                                    }
                                                    if (targetParagraphIndex != -1) {
                                                        previewLazyListState.animateScrollToItem(targetParagraphIndex)
                                                    }
                                                }
                                            },
                                            modifier = Modifier.testTag("incons_view_preview_$index")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Info,
                                                contentDescription = "View in Preview",
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Show in Document",
                                                fontFamily = PlusJakartaSansFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = Color(0xFF4F46E5)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "Suggestions" -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        suggestionsList.forEachIndexed { index, suggestion ->
                            val isExpanded = expandedSuggestions.contains(index)

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                    .clickable {
                                        expandedSuggestions = if (isExpanded) {
                                            expandedSuggestions - index
                                        } else {
                                            expandedSuggestions + index
                                        }
                                    }
                                    .padding(16.dp)
                                    .testTag("suggestion_accordion_$index"),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        // Dynamic Step counter/index bubble
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFFEEF2FF)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "%02d".format(index + 1),
                                                fontFamily = JetBrainsMonoFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = Color(0xFF4F46E5)
                                            )
                                        }

                                        Text(
                                            text = suggestion.title,
                                            fontFamily = PlusJakartaSansFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color(0xFF0F172A)
                                        )
                                    }

                                    Icon(
                                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                                        tint = Color(0xFF64748B)
                                    )
                                }

                                AnimatedVisibility(visible = isExpanded) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                                    ) {
                                        // Diagnostic Rationale Block
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFF8FAFC))
                                                .padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = "DIAGNOSTIC RATIONALE",
                                                fontFamily = JetBrainsMonoFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = Color(0xFF64748B)
                                            )
                                            Text(
                                                text = suggestion.diagnosticRationale,
                                                fontFamily = PlusJakartaSansFontFamily,
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 13.sp,
                                                lineHeight = 18.sp,
                                                color = Color(0xFF334155)
                                            )
                                        }

                                        // Improvement Remedy Block
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFECFDF5)) // Success Green Hint
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Success Check",
                                                tint = Color(0xFF10B981),
                                                modifier = Modifier.size(18.dp)
                                            )

                                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Text(
                                                    text = "IMPROVEMENT REMEDY",
                                                    fontFamily = JetBrainsMonoFontFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    color = Color(0xFF10B981)
                                                )
                                                Text(
                                                    text = suggestion.improvementRemedy,
                                                    fontFamily = PlusJakartaSansFontFamily,
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 13.sp,
                                                    lineHeight = 18.sp,
                                                    color = Color(0xFF065F46)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "References" -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        referencesList.forEachIndexed { index, citation ->
                            val statusColor = when (citation.status) {
                                "Validated" -> Color(0xFF10B981)
                                "Unresolved" -> Color(0xFFF59E0B)
                                else -> Color(0xFFEF4444) // Broken
                            }

                            val statusIcon = when (citation.status) {
                                "Validated" -> Icons.Default.Check
                                "Unresolved" -> Icons.Default.Info
                                else -> Icons.Default.Warning
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                    .onGloballyPositioned { coords ->
                                        val parentCoords = coords.parentLayoutCoordinates
                                        if (parentCoords != null) {
                                            val localPos = parentCoords.localPositionOf(coords, Offset.Zero)
                                            cardYOffsets["cit_$index"] = localPos.y
                                        }
                                    }
                                    .padding(16.dp)
                                    .testTag("reference_card_$index"),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = citation.text,
                                            fontFamily = PlusJakartaSansFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color(0xFF0F172A)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = citation.url,
                                            fontFamily = JetBrainsMonoFontFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 11.sp,
                                            color = Color(0xFF4F46E5)
                                        )
                                    }

                                    // Status pill
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(statusColor.copy(alpha = 0.08f))
                                            .border(1.dp, statusColor.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = statusIcon,
                                            contentDescription = citation.status,
                                            tint = statusColor,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = citation.status,
                                            fontFamily = JetBrainsMonoFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            color = statusColor
                                        )
                                    }
                                }

                                // Separator
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(Color(0xFFF1F5F9))
                                )

                                // Detailed Explanation
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = "AUDIT VERIFICATION DETAILS",
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = Color(0xFF64748B)
                                    )
                                    Text(
                                        text = citation.explanation,
                                        fontFamily = PlusJakartaSansFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp,
                                        color = Color(0xFF475569)
                                    )
                                }

                                if (citation.startCharOffset != null) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        TextButton(
                                            onClick = {
                                                coroutineScope.launch {
                                                    selectedTab = "Preview"
                                                    delay(150)
                                                    val targetOffset = citation.startCharOffset ?: 0
                                                    val targetParagraphIndex = paragraphInfos.indexOfFirst { p ->
                                                        targetOffset >= p.startOffset && targetOffset <= p.endOffset
                                                    }
                                                    if (targetParagraphIndex != -1) {
                                                        previewLazyListState.animateScrollToItem(targetParagraphIndex)
                                                    }
                                                }
                                            },
                                            modifier = Modifier.testTag("cit_view_preview_$index")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Info,
                                                contentDescription = "View in Preview",
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Show in Document",
                                                fontFamily = PlusJakartaSansFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = Color(0xFF4F46E5)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                "Duplication" -> {
                    DuplicationTab(
                        duplicationsList = duplicationsList,
                        onViewInPreview = { offset ->
                            coroutineScope.launch {
                                selectedTab = "Preview"
                                delay(150)
                                val targetParagraphIndex = paragraphInfos.indexOfFirst { p ->
                                    offset >= p.startOffset && offset <= p.endOffset
                                }
                                if (targetParagraphIndex != -1) {
                                    previewLazyListState.animateScrollToItem(targetParagraphIndex)
                                }
                            }
                        },
                        onPositionMeasured = { index, y ->
                            cardYOffsets["dup_a_$index"] = y
                            cardYOffsets["dup_b_$index"] = y
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Back Navigation Footer Button
            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("details_footer_button"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4F46E5), // Primary Indigo
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Return to Companion Portal",
                    fontFamily = PlusJakartaSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
fun ScoreRing(
    score: Int,
    scoreColor: Color,
    modifier: Modifier = Modifier
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animatePercent by animateFloatAsState(
        targetValue = if (animationPlayed) score / 100f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing),
        label = "score_ring_animation"
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.size(130.dp)) {
            // Background Slate track
            drawArc(
                color = Color(0xFFE2E8F0),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            )
            // Animated Color Path
            drawArc(
                color = scoreColor,
                startAngle = -90f,
                sweepAngle = animatePercent * 360f,
                useCenter = false,
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "$score%",
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = scoreColor,
                modifier = Modifier.testTag("score_ring_percentage_text")
            )
            Text(
                text = "COHERENCE",
                fontFamily = PlusJakartaSansFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = Color(0xFF64748B),
                letterSpacing = 0.5.sp
            )
        }
    }
}

// Data models used internally to cleanly present detailed report layouts
data class DetailedExplanation(
    val whatWasFound: String,
    val whyItMatters: String,
    val suggestedFix: String
)

data class DetailedInconsistency(
    val mappingHeader: String,
    val severity: String,
    val description: String,
    val recommendedCorrection: String,
    val explanation: DetailedExplanation? = null,
    val startCharOffsetA: Int? = null,
    val endCharOffsetA: Int? = null,
    val startCharOffsetB: Int? = null,
    val endCharOffsetB: Int? = null,
    val type: com.example.data.remote.dto.InconsistencyType = com.example.data.remote.dto.InconsistencyType.CONTRADICTION
)

data class DetailedSuggestion(
    val title: String,
    val diagnosticRationale: String,
    val improvementRemedy: String
)

data class DetailedReference(
    val text: String,
    val status: String,
    val url: String,
    val explanation: String,
    val startCharOffset: Int? = null,
    val endCharOffset: Int? = null
)

@Composable
fun DuplicationTab(
    duplicationsList: List<com.example.data.remote.dto.DuplicateItem>,
    onViewInPreview: (Int) -> Unit,
    onPositionMeasured: (Int, Float) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        if (duplicationsList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "No Duplications",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "No duplicated sections found.",
                        fontFamily = PlusJakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Your manuscript exhibits excellent originality.",
                        fontFamily = PlusJakartaSansFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        } else {
            duplicationsList.forEachIndexed { index, duplicate ->
                val severity = if (duplicate.similarityScore >= 0.92) "High" else "Medium"
                val severityColor = if (severity == "High") Color(0xFFEF4444) else Color(0xFFF59E0B)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                        .onGloballyPositioned { coords ->
                            val parentCoords = coords.parentLayoutCoordinates
                            if (parentCoords != null) {
                                val localPos = parentCoords.localPositionOf(coords, Offset.Zero)
                                onPositionMeasured(index, localPos.y)
                            }
                        }
                        .padding(16.dp)
                        .testTag("duplicate_card_$index"),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${duplicate.sectionA} vs ${duplicate.sectionB}",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF4F46E5)
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${(duplicate.similarityScore * 100).toInt()}% Match",
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = severityColor
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(severityColor.copy(alpha = 0.08f))
                                    .border(1.dp, severityColor.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = severity,
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = severityColor
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF8FAFC))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "MATCHED EXCERPT",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = duplicate.matchedText,
                            fontFamily = PlusJakartaSansFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = Color(0xFF334155),
                            fontStyle = FontStyle.Italic
                        )
                    }

                    if (duplicate.startCharOffsetA != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { onViewInPreview(duplicate.startCharOffsetA ?: 0) },
                                modifier = Modifier.testTag("dup_view_preview_$index")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "View in Preview",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Show in Document",
                                    fontFamily = PlusJakartaSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF4F46E5)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

data class ParagraphInfo(
    val index: Int,
    val text: String,
    val startOffset: Int,
    val endOffset: Int
)

data class HighlightSpan(
    val id: String, // unique ID of the issue
    val type: HighlightType,
    val startOffset: Int,
    val endOffset: Int,
    val label: String,
    val targetIndex: Int // index of the item in the respective tab
)

enum class HighlightType {
    CONTRADICTION,
    REDUNDANCY,
    LOGIC_GAP,
    TERMINOLOGY_CLASH,
    DUPLICATION,
    CITATION
}

fun buildParagraphAnnotatedString(
    pText: String,
    pStart: Int,
    pEnd: Int,
    highlights: List<HighlightSpan>
): AnnotatedString {
    return buildAnnotatedString {
        append(pText)

        // Find highlights that overlap with this paragraph
        highlights.forEach { span ->
            val relStart = maxOf(0, span.startOffset - pStart)
            val relEnd = minOf(pText.length, span.endOffset - pStart)

            if (relStart < relEnd) {
                // Style background based on type
                val bgColor = when (span.type) {
                    HighlightType.CONTRADICTION -> Color(0xFFFEE2E2) // Light Red
                    HighlightType.DUPLICATION -> Color(0xFFFFEDD5)   // Light Orange
                    HighlightType.REDUNDANCY -> Color(0xFFFFEDD5)    // Light Orange
                    HighlightType.CITATION -> Color(0xFFFEF9C3)      // Light Yellow
                    HighlightType.LOGIC_GAP -> Color(0xFFF3E8FF)     // Light Purple
                    HighlightType.TERMINOLOGY_CLASH -> Color(0xFFDBEAFE) // Light Blue
                }
                val textColor = when (span.type) {
                    HighlightType.CONTRADICTION -> Color(0xFF991B1B) // Dark Red
                    HighlightType.DUPLICATION -> Color(0xFFC2410C)   // Dark Orange
                    HighlightType.REDUNDANCY -> Color(0xFFC2410C)    // Dark Orange
                    HighlightType.CITATION -> Color(0xFF854D0E)      // Dark Yellow
                    HighlightType.LOGIC_GAP -> Color(0xFF6B21A8)     // Dark Purple
                    HighlightType.TERMINOLOGY_CLASH -> Color(0xFF1E40AF) // Dark Blue
                }

                addStyle(
                    style = SpanStyle(
                        background = bgColor,
                        color = textColor,
                        fontWeight = FontWeight.Bold
                    ),
                    start = relStart,
                    end = relEnd
                )

                // Add a custom tag for click interaction
                addStringAnnotation(
                    tag = "highlight_click",
                    annotation = span.id,
                    start = relStart,
                    end = relEnd
                )
            }
        }
    }
}
