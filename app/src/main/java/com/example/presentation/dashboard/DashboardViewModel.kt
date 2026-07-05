package com.example.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkInfo
import com.example.data.local.SessionManager
import com.example.data.remote.dto.ScanResponse
import com.example.data.remote.dto.InconsistencyItem
import com.example.data.remote.dto.InconsistencyType
import com.example.data.remote.dto.Severity
import com.example.data.remote.dto.CitationItem
import com.example.data.remote.dto.LinkStatus
import com.example.domain.usecase.ScanManuscriptUseCase
import com.example.worker.ScanWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

sealed interface DashboardUiState {
    object Empty : DashboardUiState
    object Loading : DashboardUiState
    data class Success(
        val lastScan: ScanResponse,
        val history: List<ScanResponse>
    ) : DashboardUiState
}

class DashboardViewModel(
    val scanManuscriptUseCase: ScanManuscriptUseCase,
    private val sessionManager: SessionManager,
    private val workManager: WorkManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    private val _dashboardUiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Empty)
    val dashboardUiState: StateFlow<DashboardUiState> = _dashboardUiState.asStateFlow()

    // Expose DataStore parameters reactively to the Compose UI layer
    val userName = sessionManager.userName.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "Noel Henry"
    )

    val userEmail = sessionManager.userEmail.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "noelhenrymier@gmail.com"
    )

    val userInstitution = sessionManager.userInstitution.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "Stanford University"
    )

    val userRole = sessionManager.userRole.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "Principal Investigator"
    )

    val userBio = sessionManager.userBio.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "Specializing in distributed systems coherence and academic reference validation systems."
    )

    val scansCount = sessionManager.scansCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    fun fetchDashboardData() {
        _dashboardUiState.value = DashboardUiState.Loading
        viewModelScope.launch {
            try {
                // Fetch the current scans count from the session manager
                val count = sessionManager.scansCount.first()
                if (count == 0) {
                    _dashboardUiState.value = DashboardUiState.Empty
                } else {
                    // Simulating retrieving latest scans history list from existing repository/backend
                    val mockHistory = listOf(
                        ScanResponse(
                            coherenceScore = 88,
                            overallAssessment = "Excellent Coherence",
                            inconsistencies = listOf(
                                InconsistencyItem(
                                    type = InconsistencyType.CONTRADICTION,
                                    severity = Severity.HIGH,
                                    sectionA = "Section 1.2 Introduction",
                                    sectionB = "Section 3.4 Methodology",
                                    description = "The target sample size contradicts across sections.",
                                    recommendedCorrection = "Reconcile sample sizes to be exactly 150 participants.",
                                    explanation = com.example.data.remote.dto.ExplanationDetail(
                                        whatWasFound = "Section 1.2 states 'the study includes 200 participants', while Section 3.4 specifies 'N=150'.",
                                        whyItMatters = "Conflicting sample sizes undermine the reproducibility of the study and the validity of statistical power calculations.",
                                        suggestedFix = "Review the final dataset and update all sections to consistently reflect the actual number of participants."
                                    )
                                )
                            ),
                            references = listOf(
                                CitationItem(
                                    citationText = "Smith et al. (2021) on system scaling limits",
                                    linkStatus = LinkStatus.VALIDATED,
                                    detailedExplanation = "Reference link is live and matches paper DOI."
                                )
                            ),
                            duplicateSections = emptyList(),
                            createdAt = "2026-07-02T13:55:00Z"
                        ),
                        ScanResponse(
                            coherenceScore = 54,
                            overallAssessment = "Moderate Coherence",
                            inconsistencies = listOf(
                                InconsistencyItem(
                                    type = InconsistencyType.LOGIC_GAP,
                                    severity = Severity.MEDIUM,
                                    sectionA = "Section 2 Literature Review",
                                    sectionB = "Section 4 Discussion",
                                    description = "There is a logical disconnect in user metrics discussion.",
                                    recommendedCorrection = "Include a summary of prior literature findings in discussion.",
                                    explanation = com.example.data.remote.dto.ExplanationDetail(
                                        whatWasFound = "The Literature Review focuses heavily on 'retention rate' as the primary metric, but the Discussion section concludes by evaluating 'session length' without tying it back to retention.",
                                        whyItMatters = "When the discussion fails to address the metrics established in the literature review, the paper's core narrative appears fragmented and its conclusions less persuasive.",
                                        suggestedFix = "Add a paragraph in the Discussion connecting 'session length' to the 'retention rate' theories discussed in Section 2."
                                    )
                                )
                            ),
                            references = listOf(
                                CitationItem(
                                    citationText = "Doe et al. (2019) on human factors",
                                    linkStatus = LinkStatus.UNRESOLVED,
                                    detailedExplanation = "Reference link is not reachable, check URL."
                                )
                            ),
                            duplicateSections = listOf(
                                com.example.data.remote.dto.DuplicateItem(
                                    sectionA = "Section 2.1",
                                    sectionB = "Section 4.1",
                                    similarityScore = 0.88,
                                    matchedText = "The preliminary results indicate a strong correlation between the variables."
                                )
                            ),
                            createdAt = "2026-06-28T10:30:00Z"
                        )
                    )
                    _dashboardUiState.value = DashboardUiState.Success(
                        lastScan = mockHistory.first(),
                        history = mockHistory
                    )
                }
            } catch (e: Exception) {
                _dashboardUiState.value = DashboardUiState.Empty
            }
        }
    }

    fun submitDocumentLink(url: String, category: String = "Full Manuscript", theme: String? = null, researchType: String? = null) {
        if (url.isBlank()) {
            _uiState.value = ScanUiState.Error("Please enter a valid document link")
            return
        }
        _uiState.value = ScanUiState.Loading
        
        val pushSubscriptionId = com.onesignal.OneSignal.User.pushSubscription.id
        
        val inputDataBuilder = Data.Builder()
            .putString("url", url)
            .putBoolean("is_demo", false)
            .putString("category", category)
            .putString("push_subscription_id", pushSubscriptionId)
            
        if (theme != null) {
            inputDataBuilder.putString("theme", theme)
        }
        if (researchType != null) {
            inputDataBuilder.putString("research_type", researchType)
        }
            
        val workRequest = OneTimeWorkRequestBuilder<ScanWorker>()
            .setInputData(inputDataBuilder.build())
            .build()
            
        workManager.enqueue(workRequest)
        
        workManager.getWorkInfoByIdLiveData(workRequest.id).observeForever { workInfo ->
            if (workInfo != null && workInfo.state.isFinished) {
                if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                    val json = workInfo.outputData.getString("result_json")
                    if (json != null) {
                        try {
                            val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
                            val adapter = moshi.adapter(ScanResponse::class.java)
                            val response = adapter.fromJson(json)
                            if (response != null) {
                                _uiState.value = ScanUiState.Success(response)
                            } else {
                                _uiState.value = ScanUiState.Error("Failed to parse response")
                            }
                        } catch (e: Exception) {
                            _uiState.value = ScanUiState.Error("Error parsing result")
                        }
                    } else {
                        val error = workInfo.outputData.getString("error") ?: "An unknown error occurred"
                        _uiState.value = ScanUiState.Error(error)
                    }
                } else {
                    _uiState.value = ScanUiState.Error("Scan failed")
                }
                fetchDashboardData()
            }
        }
    }

    fun submitDemoScan(url: String, researchType: String = "Quantitative") {
        _uiState.value = ScanUiState.Loading
        
        val pushSubscriptionId = com.onesignal.OneSignal.User.pushSubscription.id
        
        val inputData = Data.Builder()
            .putString("url", url)
            .putBoolean("is_demo", true)
            .putString("push_subscription_id", pushSubscriptionId)
            .putString("research_type", researchType)
            .build()
            
        val workRequest = OneTimeWorkRequestBuilder<ScanWorker>()
            .setInputData(inputData)
            .build()
            
        workManager.enqueue(workRequest)
        
        // Observe WorkManager for result so UI updates if in foreground
        workManager.getWorkInfoByIdLiveData(workRequest.id).observeForever { workInfo ->
            if (workInfo != null && workInfo.state.isFinished) {
                if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                    val json = workInfo.outputData.getString("result_json")
                    if (json != null) {
                        try {
                            val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
                            val adapter = moshi.adapter(ScanResponse::class.java)
                            val response = adapter.fromJson(json)
                            if (response != null) {
                                _uiState.value = ScanUiState.Success(response)
                            } else {
                                _uiState.value = ScanUiState.Error("Failed to parse response")
                            }
                        } catch (e: Exception) {
                            _uiState.value = ScanUiState.Error("Error parsing result")
                        }
                    }
                } else {
                    _uiState.value = ScanUiState.Error("Scan failed")
                }
                fetchDashboardData()
            }
        }
    }

    fun resetUiState() {
        _uiState.value = ScanUiState.Idle
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            sessionManager.clearSession()
            onLoggedOut()
        }
    }
}
