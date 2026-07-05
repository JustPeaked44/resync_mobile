package com.example.presentation.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search

import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PlayfairDisplayFontFamily
import com.example.ui.theme.PlusJakartaSansFontFamily
import com.example.ui.theme.JetBrainsMonoFontFamily

@Composable
fun NewScanScreen(
    viewModel: DashboardViewModel,
    onOpenDetails: (Int, String, List<String>) -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    wizardViewModel: WizardViewModel = remember { WizardViewModel(viewModel.scanManuscriptUseCase) }
) {
    val currentStep by wizardViewModel.currentStep.collectAsState()
    val uploadType by wizardViewModel.uploadType.collectAsState()
    val selectedChapters by wizardViewModel.selectedChapters.collectAsState()
    val chapterUrls by wizardViewModel.chapterUrls.collectAsState()
    val wholeManuscriptUrl by wizardViewModel.wholeManuscriptUrl.collectAsState()
    val selectedTab by wizardViewModel.selectedTab.collectAsState()
    val researchType by wizardViewModel.researchType.collectAsState()
    val researchTheme by wizardViewModel.researchTheme.collectAsState()
    val wizardScanUiState by wizardViewModel.scanUiState.collectAsState()
    val urlError by wizardViewModel.urlError.collectAsState()
    val chapterErrors by wizardViewModel.chapterErrors.collectAsState()

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    val stepNumber = wizardViewModel.getStepNumber(currentStep)
    val totalSteps = wizardViewModel.getTotalSteps()

    val isScanning = wizardScanUiState is ScanUiState.Loading

    // Processing steps for cycling HUD
    val steps = listOf(
        "Connecting to document link...",
        "Retrieving layout...",
        "Analyzing terminology...",
        "Detecting contradictions...",
        "Validating reference citations...",
        "Synthesizing final coherence score..."
    )
    var currentStepIndex by remember { mutableStateOf(0) }

    LaunchedEffect(isScanning) {
        if (isScanning) {
            currentStepIndex = 0
            while (true) {
                kotlinx.coroutines.delay(1000)
                currentStepIndex = (currentStepIndex + 1) % steps.size
            }
        }
    }

    // Auto-navigate to details on success
    LaunchedEffect(wizardScanUiState) {
        if (wizardScanUiState is ScanUiState.Success) {
            val successData = (wizardScanUiState as ScanUiState.Success).data
            val finalUrl = if (uploadType == UploadType.PER_CHAPTER) {
                chapterUrls.values.firstOrNull().orEmpty()
            } else {
                wholeManuscriptUrl
            }
            onOpenDetails(successData.coherenceScore, finalUrl, successData.missingSections)
            wizardViewModel.resetState()
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        containerColor = Color(0xFFF8FAFC), // Slate gray background
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Sticky top app bar
                Surface(
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Search, // Scan engine icon
                                contentDescription = "Coherence Compass",
                                tint = Color(0xFF4F46E5), // Indigo
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Coherence Scan Engine",
                                fontFamily = PlayfairDisplayFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(0xFF0F172A)
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TextButton(
                                onClick = {
                                    wizardViewModel.loadDemoLink { scanResponse ->
                                        // Tapping pre-fills and triggers standard scan directly
                                    }
                                },
                                enabled = !isScanning,
                                modifier = Modifier.testTag("try_demo_manuscript_scan_banner")
                            ) {
                                Text(
                                    text = "Load Interactive Demo Link",
                                    fontFamily = PlusJakartaSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF10B981) // Emerald Green
                                )
                            }
                            IconButton(
                                onClick = onNavigateToSettings,
                                modifier = Modifier.testTag("newscan_settings_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }

                // Determinate purple progress bar with indicator
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(bottom = 8.dp)
                ) {
                    LinearProgressIndicator(
                        progress = stepNumber.toFloat() / totalSteps.toFloat(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp),
                        color = Color(0xFF4F46E5),
                        trackColor = Color(0xFFEEF2FF)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Step $stepNumber of $totalSteps",
                        fontFamily = PlusJakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        },
        bottomBar = {
            // Sticky footer that sits above the on-screen keyboard
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding() // Automatically floats above the keyboard
            ) {
                when (currentStep) {
                    is WizardStep.ChooseUploadType -> {
                        // Footer for Screen 1 (optional placeholder, or none as cards navigate immediately)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(
                                onClick = onNavigateToDashboard,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("wizard_back_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFF1F5F9),
                                    contentColor = Color(0xFF64748B)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Return to Dashboard",
                                    fontFamily = PlusJakartaSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    is WizardStep.SelectChapters -> {
                        // Footer for Screen 2A: Select Chapters
                        val count = selectedChapters.size
                        var showInlineWarning by remember { mutableStateOf(false) }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (showInlineWarning && count == 0) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFFEF2F2))
                                        .border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(8.dp))
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Warning",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Select at least one chapter",
                                        fontFamily = PlusJakartaSansFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp,
                                        color = Color(0xFFEF4444)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { wizardViewModel.navigateBack() },
                                    modifier = Modifier
                                        .weight(0.35f)
                                        .height(48.dp)
                                        .testTag("wizard_back_button"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFF1F5F9),
                                        contentColor = Color(0xFF64748B)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Back",
                                        fontFamily = PlusJakartaSansFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }

                                Button(
                                    onClick = {
                                        if (count > 0) {
                                            wizardViewModel.navigateForward(WizardStep.UploadChapterContent)
                                        } else {
                                            showInlineWarning = true
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(0.65f)
                                        .height(48.dp)
                                        .testTag("wizard_continue_button"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF4F46E5),
                                        contentColor = Color.White,
                                        disabledContainerColor = Color(0xFFE2E8F0),
                                        disabledContentColor = Color(0xFF94A3B8)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Continue ($count selected)",
                                        fontFamily = PlusJakartaSansFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    is WizardStep.UploadChapterContent -> {
                        // Footer for Screen 3A: Chapter Content Link Submission
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { wizardViewModel.navigateBack() },
                                modifier = Modifier
                                    .weight(0.35f)
                                    .height(48.dp)
                                    .testTag("wizard_back_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFF1F5F9),
                                    contentColor = Color(0xFF64748B)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Back",
                                    fontFamily = PlusJakartaSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            Button(
                                onClick = {
                                    focusManager.clearFocus()
                                    wizardViewModel.analyzeManuscript {
                                        // on success automatically navigated
                                    }
                                },
                                enabled = !isScanning,
                                modifier = Modifier
                                    .weight(0.65f)
                                    .height(48.dp)
                                    .testTag("analyze_manuscript_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4F46E5),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Analyze manuscript →",
                                    fontFamily = PlusJakartaSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    is WizardStep.UploadDocument -> {
                        // Footer for Screen 2B: Upload Whole Document
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { wizardViewModel.navigateBack() },
                                modifier = Modifier
                                    .weight(0.35f)
                                    .height(48.dp)
                                    .testTag("wizard_back_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFF1F5F9),
                                    contentColor = Color(0xFF64748B)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Back",
                                    fontFamily = PlusJakartaSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            Button(
                                onClick = {
                                    focusManager.clearFocus()
                                    wizardViewModel.analyzeManuscript {
                                        // on success automatically navigated
                                    }
                                },
                                enabled = !isScanning && (selectedTab == 1 || wholeManuscriptUrl.isNotBlank()),
                                modifier = Modifier
                                    .weight(0.65f)
                                    .height(48.dp)
                                    .testTag("analyze_manuscript_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4F46E5),
                                    contentColor = Color.White,
                                    disabledContainerColor = Color(0xFFE2E8F0),
                                    disabledContentColor = Color(0xFF94A3B8)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Analyze manuscript →",
                                    fontFamily = PlusJakartaSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                when (currentStep) {
                    is WizardStep.ChooseUploadType -> {
                        // SCREEN 1 — CHOOSE UPLOAD TYPE (Step 1 of Y)
                        Text(
                            text = "1. CHOOSE UPLOAD TYPE",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF4F46E5),
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = "How would you like to upload your manuscript?",
                            fontFamily = PlayfairDisplayFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color(0xFF0F172A),
                            lineHeight = 28.sp
                        )

                        Text(
                            text = "Select whether you want to scan individual sections or the entire draft.",
                            fontFamily = PlusJakartaSansFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = Color(0xFF64748B)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Per Chapter Card
                        Surface(
                            onClick = { wizardViewModel.selectUploadType(UploadType.PER_CHAPTER) },
                            color = Color.White,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("upload_type_chapter_card")
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFEEF2FF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.List,
                                        contentDescription = "Per Chapter",
                                        tint = Color(0xFF4F46E5),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Per chapter",
                                        fontFamily = PlusJakartaSansFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color(0xFF0F172A)
                                    )
                                    Text(
                                        text = "Scan individual sections of your draft separately",
                                        fontFamily = PlusJakartaSansFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 13.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }

                        // Whole Manuscript Card
                        Surface(
                            onClick = { wizardViewModel.selectUploadType(UploadType.WHOLE_MANUSCRIPT) },
                            color = Color.White,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("upload_type_manuscript_card")
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFECFDF5)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.List,
                                        contentDescription = "Whole manuscript",
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Whole manuscript",
                                        fontFamily = PlusJakartaSansFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color(0xFF0F172A)
                                    )
                                    Text(
                                        text = "Analyze your entire draft document at once",
                                        fontFamily = PlusJakartaSansFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 13.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                    }

                    is WizardStep.SelectChapters -> {
                        // SCREEN 2A — SELECT CHAPTERS (Path A, Step 2 of 3)
                        Text(
                            text = "2. SELECT CHAPTERS",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF4F46E5),
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = "Which chapters are you uploading?",
                            fontFamily = PlayfairDisplayFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color(0xFF0F172A),
                            lineHeight = 28.sp
                        )

                        Text(
                            text = "Select one or more chapters to analyze compatibility.",
                            fontFamily = PlusJakartaSansFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = Color(0xFF64748B)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Custom 2-Column Wrap Pill Layout
                        val chapters = listOf("Chapter 1", "Chapter 2", "Chapter 3", "Chapter 4", "Chapter 5", "Chapter 6")
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            for (i in chapters.indices step 2) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    val ch1 = chapters[i]
                                    val isSelected1 = selectedChapters.contains(ch1)
                                    Surface(
                                        onClick = { wizardViewModel.toggleChapter(ch1) },
                                        color = if (isSelected1) Color(0xFFEEF2FF) else Color.White,
                                        shape = RoundedCornerShape(8.dp),
                                        border = androidx.compose.foundation.BorderStroke(
                                            width = if (isSelected1) 2.dp else 1.dp,
                                            color = if (isSelected1) Color(0xFF4F46E5) else Color(0xFFE2E8F0)
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("chapter_pill_${ch1.lowercase().replace(" ", "_")}")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = ch1,
                                                fontFamily = PlusJakartaSansFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = if (isSelected1) Color(0xFF4F46E5) else Color(0xFF0F172A)
                                            )
                                            if (isSelected1) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    tint = Color(0xFF4F46E5),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }

                                    if (i + 1 < chapters.size) {
                                        val ch2 = chapters[i + 1]
                                        val isSelected2 = selectedChapters.contains(ch2)
                                        Surface(
                                            onClick = { wizardViewModel.toggleChapter(ch2) },
                                            color = if (isSelected2) Color(0xFFEEF2FF) else Color.White,
                                            shape = RoundedCornerShape(8.dp),
                                            border = androidx.compose.foundation.BorderStroke(
                                                width = if (isSelected2) 2.dp else 1.dp,
                                                color = if (isSelected2) Color(0xFF4F46E5) else Color(0xFFE2E8F0)
                                            ),
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("chapter_pill_${ch2.lowercase().replace(" ", "_")}")
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(14.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = ch2,
                                                    fontFamily = PlusJakartaSansFontFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp,
                                                    color = if (isSelected2) Color(0xFF4F46E5) else Color(0xFF0F172A)
                                                )
                                                if (isSelected2) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = "Selected",
                                                        tint = Color(0xFF4F46E5),
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }

                    is WizardStep.UploadChapterContent -> {
                        // SCREEN 3A — UPLOAD CHAPTER CONTENT (Path A, Step 3 of 3)
                        Text(
                            text = "3. UPLOAD CHAPTER CONTENT",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF4F46E5),
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = "Provide document links",
                            fontFamily = PlayfairDisplayFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color(0xFF0F172A),
                            lineHeight = 28.sp
                        )

                        Text(
                            text = "Enter Google Docs URLs for each of your selected chapters.",
                            fontFamily = PlusJakartaSansFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = Color(0xFF64748B)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // URL Inputs per selected chapter
                        selectedChapters.sorted().forEach { chapter ->
                            val url = chapterUrls[chapter].orEmpty()
                            val error = chapterErrors[chapter]

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Google Docs URL for $chapter",
                                    fontFamily = PlusJakartaSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF334155)
                                )
                                OutlinedTextField(
                                    value = url,
                                    onValueChange = { wizardViewModel.updateChapterUrl(chapter, it) },
                                    placeholder = {
                                        Text(
                                            text = "https://docs.google.com/document/d/...",
                                            fontFamily = PlusJakartaSansFontFamily,
                                            color = Color(0xFF94A3B8),
                                            fontSize = 14.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    isError = error != null,
                                    enabled = !isScanning,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_chapter_url_${chapter.lowercase().replace(" ", "_")}"),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Uri,
                                        imeAction = ImeAction.Next
                                    ),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        focusedBorderColor = Color(0xFF4F46E5),
                                        unfocusedBorderColor = Color(0xFFE2E8F0),
                                        errorBorderColor = Color(0xFFEF4444)
                                    )
                                )
                                if (error != null) {
                                    Text(
                                        text = error,
                                        fontFamily = PlusJakartaSansFontFamily,
                                        fontSize = 12.sp,
                                        color = Color(0xFFEF4444)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Unified optional research theme field
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Research Theme or Focus (optional)",
                                fontFamily = PlusJakartaSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF334155)
                            )
                            OutlinedTextField(
                                value = researchTheme,
                                onValueChange = { wizardViewModel.updateResearchTheme(it) },
                                placeholder = {
                                    Text(
                                        text = "e.g., Focus on distributed system structures",
                                        fontFamily = PlusJakartaSansFontFamily,
                                        color = Color(0xFF94A3B8),
                                        fontSize = 14.sp
                                    )
                                },
                                enabled = !isScanning,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_research_theme"),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = Color(0xFF4F46E5),
                                    unfocusedBorderColor = Color(0xFFE2E8F0)
                                )
                            )
                        }
                    }

                    is WizardStep.UploadDocument -> {
                        // SCREEN 2B — UPLOAD DOCUMENT (Path B, Step 2 of 2)
                        Text(
                            text = "2. UPLOAD DOCUMENT",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF4F46E5),
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = "UPLOADING",
                            fontFamily = PlusJakartaSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = "Full manuscript draft",
                            fontFamily = PlayfairDisplayFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color(0xFF0F172A),
                            lineHeight = 28.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Custom 50%-width styled tabs
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF1F5F9))
                                .padding(4.dp)
                        ) {
                            // Google Docs Tab
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (selectedTab == 0) Color.White else Color.Transparent)
                                    .clickable { wizardViewModel.updateSelectedTab(0) }
                                    .padding(vertical = 10.dp)
                                    .testTag("tab_google_docs"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "🔗 Google Docs link",
                                    fontFamily = PlusJakartaSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (selectedTab == 0) Color(0xFF4F46E5) else Color(0xFF64748B)
                                )
                            }

                            // Word Document Tab
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (selectedTab == 1) Color.White else Color.Transparent)
                                    .clickable { wizardViewModel.updateSelectedTab(1) }
                                    .padding(vertical = 10.dp)
                                    .testTag("tab_word_document"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "📄 Word document",
                                    fontFamily = PlusJakartaSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (selectedTab == 1) Color(0xFF4F46E5) else Color(0xFF64748B)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (selectedTab == 0) {
                            // Tab 0 Content: Google Docs Link
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Manuscript Google Docs Link",
                                    fontFamily = PlusJakartaSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF334155)
                                )
                                OutlinedTextField(
                                    value = wholeManuscriptUrl,
                                    onValueChange = { wizardViewModel.updateWholeManuscriptUrl(it) },
                                    placeholder = {
                                        Text(
                                            text = "https://docs.google.com/document/d/...",
                                            fontFamily = PlusJakartaSansFontFamily,
                                            color = Color(0xFF94A3B8),
                                            fontSize = 14.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    isError = urlError != null,
                                    enabled = !isScanning,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_manuscript_link"),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Uri,
                                        imeAction = ImeAction.Next
                                    ),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        focusedBorderColor = Color(0xFF4F46E5),
                                        unfocusedBorderColor = Color(0xFFE2E8F0),
                                        errorBorderColor = Color(0xFFEF4444)
                                    )
                                )
                                if (urlError != null) {
                                    Text(
                                        text = urlError!!,
                                        fontFamily = PlusJakartaSansFontFamily,
                                        fontSize = 12.sp,
                                        color = Color(0xFFEF4444)
                                    )
                                }
                                Text(
                                    text = "Make sure your document sharing is set to 'Anyone with the link can view.'",
                                    fontFamily = PlusJakartaSansFontFamily,
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        } else {
                            // Tab 1 Content: Word Document (Placeholder File Selection layout)
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(2.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .padding(24.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.List,
                                        contentDescription = "Upload Document",
                                        tint = Color(0xFF4F46E5),
                                        modifier = Modifier.size(36.dp)
                                    )

                                    Text(
                                        text = "Upload Word Document",
                                        fontFamily = PlusJakartaSansFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color(0xFF0F172A)
                                    )

                                    Text(
                                        text = ".docx format up to 20MB",
                                        fontFamily = PlusJakartaSansFontFamily,
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B)
                                    )

                                    Button(
                                        onClick = {},
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFEEF2FF),
                                            contentColor = Color(0xFF4F46E5)
                                        ),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "Choose File",
                                            fontFamily = PlusJakartaSansFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }

                                    Text(
                                        text = "Direct file uploads are read-only in this demo. Please use the Google Docs Link tab to run active scans.",
                                        fontFamily = PlusJakartaSansFontFamily,
                                        fontSize = 11.sp,
                                        color = Color(0xFFF59E0B),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Segmented control for Research Type
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Research Type",
                                fontFamily = PlusJakartaSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF334155)
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .padding(4.dp)
                            ) {
                                // Quantitative Segment
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (researchType == "Quantitative") Color.White else Color.Transparent)
                                        .clickable { wizardViewModel.updateResearchType("Quantitative") }
                                        .padding(vertical = 10.dp)
                                        .testTag("research_type_quantitative"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Quantitative",
                                        fontFamily = PlusJakartaSansFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (researchType == "Quantitative") Color(0xFF4F46E5) else Color(0xFF64748B)
                                    )
                                }

                                // Qualitative Segment
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (researchType == "Qualitative") Color.White else Color.Transparent)
                                        .clickable { wizardViewModel.updateResearchType("Qualitative") }
                                        .padding(vertical = 10.dp)
                                        .testTag("research_type_qualitative"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Qualitative",
                                        fontFamily = PlusJakartaSansFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (researchType == "Qualitative") Color(0xFF4F46E5) else Color(0xFF64748B)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Research Theme or Focus
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Research Theme or Focus (optional)",
                                fontFamily = PlusJakartaSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF334155)
                            )
                            OutlinedTextField(
                                value = researchTheme,
                                onValueChange = { wizardViewModel.updateResearchTheme(it) },
                                placeholder = {
                                    Text(
                                        text = "e.g., Focus on distributed system structures",
                                        fontFamily = PlusJakartaSansFontFamily,
                                        color = Color(0xFF94A3B8),
                                        fontSize = 14.sp
                                    )
                                },
                                enabled = !isScanning,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_research_theme"),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = Color(0xFF4F46E5),
                                    unfocusedBorderColor = Color(0xFFE2E8F0)
                                )
                            )
                        }
                    }
                }
            }

            // High Fidelity Processing HUD Overlay with sequential active steps
            AnimatedVisibility(
                visible = isScanning,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF0F172A).copy(alpha = 0.85f)) // Translucent dark overlay
                        .clickable(enabled = false) {}, // Intercept touch events
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .padding(32.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                            .padding(28.dp)
                            .testTag("loading_overlay_container"),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF4F46E5),
                            modifier = Modifier.size(52.dp),
                            strokeWidth = 4.dp
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "AI is analyzing your coherence...",
                                fontFamily = PlusJakartaSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(0xFF0F172A)
                            )

                            // Cycling Text Label for Active Steps
                            Text(
                                text = steps[currentStepIndex],
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF4F46E5),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.testTag("loading_step_text")
                            )

                            Text(
                                text = "Analysis started. Please stay on this screen while we validate citations, find contradictions, and synthesize final compatibility scores.",
                                fontFamily = PlusJakartaSansFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp,
                                color = Color(0xFF64748B),
                                lineHeight = 18.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Toast/Overlay for Error states from scanning
            if (wizardScanUiState is ScanUiState.Error) {
                Surface(
                    color = Color(0xFFFEF2F2),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                        .testTag("new_scan_error_container")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error Icon",
                            tint = Color(0xFFEF4444)
                        )
                        Text(
                            text = (wizardScanUiState as ScanUiState.Error).message,
                            fontFamily = PlusJakartaSansFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = Color(0xFFEF4444),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { wizardViewModel.resetState() }) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Dismiss",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
