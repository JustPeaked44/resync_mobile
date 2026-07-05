package com.example.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.remote.dto.ScanResponse
import com.example.domain.usecase.ScanManuscriptUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface WizardStep {
    object ChooseUploadType : WizardStep
    object SelectChapters : WizardStep
    object UploadChapterContent : WizardStep
    object UploadDocument : WizardStep
}

enum class UploadType {
    PER_CHAPTER,
    WHOLE_MANUSCRIPT
}

class WizardViewModel(
    private val scanManuscriptUseCase: ScanManuscriptUseCase
) : ViewModel() {

    private val _currentStep = MutableStateFlow<WizardStep>(WizardStep.ChooseUploadType)
    val currentStep: StateFlow<WizardStep> = _currentStep.asStateFlow()

    private val _history = MutableStateFlow<List<WizardStep>>(listOf(WizardStep.ChooseUploadType))
    val history: StateFlow<List<WizardStep>> = _history.asStateFlow()

    private val _uploadType = MutableStateFlow<UploadType?>(null)
    val uploadType: StateFlow<UploadType?> = _uploadType.asStateFlow()

    // Screen 2A: Select Chapters state
    private val _selectedChapters = MutableStateFlow<Set<String>>(emptySet())
    val selectedChapters: StateFlow<Set<String>> = _selectedChapters.asStateFlow()

    // Screen 3A: Chapter URLs
    private val _chapterUrls = MutableStateFlow<Map<String, String>>(emptyMap())
    val chapterUrls: StateFlow<Map<String, String>> = _chapterUrls.asStateFlow()

    // Screen 2B: Whole Manuscript state
    private val _wholeManuscriptUrl = MutableStateFlow("")
    val wholeManuscriptUrl: StateFlow<String> = _wholeManuscriptUrl.asStateFlow()

    private val _selectedTab = MutableStateFlow(0) // 0 for Google Docs, 1 for Word document
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _researchType = MutableStateFlow("Quantitative") // Quantitative / Qualitative
    val researchType: StateFlow<String> = _researchType.asStateFlow()

    private val _researchTheme = MutableStateFlow("")
    val researchTheme: StateFlow<String> = _researchTheme.asStateFlow()

    // Scan execution state
    private val _scanUiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val scanUiState: StateFlow<ScanUiState> = _scanUiState.asStateFlow()

    // Error states for validation
    private val _urlError = MutableStateFlow<String?>(null)
    val urlError: StateFlow<String?> = _urlError.asStateFlow()

    private val _chapterErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val chapterErrors: StateFlow<Map<String, String>> = _chapterErrors.asStateFlow()

    fun selectUploadType(type: UploadType) {
        _uploadType.value = type
        val nextStep = when (type) {
            UploadType.PER_CHAPTER -> WizardStep.SelectChapters
            UploadType.WHOLE_MANUSCRIPT -> WizardStep.UploadDocument
        }
        navigateForward(nextStep)
    }

    fun navigateForward(step: WizardStep) {
        val currentList = _history.value.toMutableList()
        currentList.add(step)
        _history.value = currentList
        _currentStep.value = step
    }

    fun navigateBack(): Boolean {
        val currentList = _history.value.toMutableList()
        if (currentList.size > 1) {
            currentList.removeAt(currentList.lastIndex)
            _history.value = currentList
            _currentStep.value = currentList.last()
            return true
        }
        return false // already at root
    }

    fun toggleChapter(chapter: String) {
        val current = _selectedChapters.value.toMutableSet()
        if (current.contains(chapter)) {
            current.remove(chapter)
        } else {
            current.add(chapter)
        }
        _selectedChapters.value = current
    }

    fun updateChapterUrl(chapter: String, url: String) {
        val current = _chapterUrls.value.toMutableMap()
        current[chapter] = url
        _chapterUrls.value = current
        
        val currentErrors = _chapterErrors.value.toMutableMap()
        currentErrors.remove(chapter)
        _chapterErrors.value = currentErrors
    }

    fun updateWholeManuscriptUrl(url: String) {
        _wholeManuscriptUrl.value = url
        _urlError.value = null
    }

    fun updateSelectedTab(tab: Int) {
        _selectedTab.value = tab
    }

    fun updateResearchType(type: String) {
        _researchType.value = type
    }

    fun updateResearchTheme(theme: String) {
        _researchTheme.value = theme
    }

    fun getStepNumber(step: WizardStep): Int {
        return when (step) {
            WizardStep.ChooseUploadType -> 1
            WizardStep.SelectChapters -> 2
            WizardStep.UploadChapterContent -> 3
            WizardStep.UploadDocument -> 2
        }
    }

    fun getTotalSteps(): Int {
        return when (_uploadType.value) {
            UploadType.PER_CHAPTER -> 3
            UploadType.WHOLE_MANUSCRIPT -> 2
            else -> 3 // Default until chosen
        }
    }

    fun loadDemoLink(onSuccess: (ScanResponse) -> Unit) {
        if (_currentStep.value == WizardStep.ChooseUploadType) {
            selectUploadType(UploadType.WHOLE_MANUSCRIPT)
        }

        when (_currentStep.value) {
            WizardStep.UploadDocument -> {
                _wholeManuscriptUrl.value = "https://docs.google.com/document/d/demo-q3-strategy-roadmap"
                _researchTheme.value = "Coherence Analysis and References Demo Theme"
                _researchType.value = "Quantitative"
            }
            WizardStep.SelectChapters -> {
                _selectedChapters.value = setOf("Chapter 1", "Chapter 2")
                navigateForward(WizardStep.UploadChapterContent)
                updateChapterUrl("Chapter 1", "https://docs.google.com/document/d/demo-chapter-1")
                updateChapterUrl("Chapter 2", "https://docs.google.com/document/d/demo-chapter-2")
                _researchTheme.value = "Coherence Analysis and References Demo Theme"
                _researchType.value = "Quantitative"
            }
            WizardStep.UploadChapterContent -> {
                val selected = _selectedChapters.value.ifEmpty { setOf("Chapter 1", "Chapter 2") }
                _selectedChapters.value = selected
                selected.forEachIndexed { index, chapter ->
                    updateChapterUrl(chapter, "https://docs.google.com/document/d/demo-chapter-${index + 1}")
                }
                _researchTheme.value = "Coherence Analysis and References Demo Theme"
                _researchType.value = "Quantitative"
            }
            else -> {}
        }
        analyzeManuscript(onSuccess)
    }

    fun analyzeManuscript(onSuccess: (ScanResponse) -> Unit) {
        _scanUiState.value = ScanUiState.Loading
        _urlError.value = null
        _chapterErrors.value = emptyMap()

        viewModelScope.launch {
            val result = if (_uploadType.value == UploadType.PER_CHAPTER) {
                val listUrls = _selectedChapters.value.map { _chapterUrls.value[it].orEmpty() }
                
                var hasError = false
                val errors = mutableMapOf<String, String>()
                _selectedChapters.value.forEach { chapter ->
                    val url = _chapterUrls.value[chapter].orEmpty()
                    if (url.isBlank()) {
                        errors[chapter] = "URL cannot be empty"
                        hasError = true
                    } else if (!url.contains("docs.google.com") && !url.contains("demo")) {
                        errors[chapter] = "Invalid Google Docs URL"
                        hasError = true
                    }
                }
                if (hasError) {
                    _chapterErrors.value = errors
                    _scanUiState.value = ScanUiState.Idle
                    return@launch
                }

                scanManuscriptUseCase(
                    category = "Chapter Set",
                    theme = _researchTheme.value.ifBlank { null },
                    researchType = _researchType.value,
                    chapterUrls = listUrls
                )
            } else {
                val url = _wholeManuscriptUrl.value
                if (url.isBlank()) {
                    _urlError.value = "URL cannot be empty"
                    _scanUiState.value = ScanUiState.Idle
                    return@launch
                }
                if (!url.contains("docs.google.com") && !url.contains("demo")) {
                    _urlError.value = "Invalid Google Docs URL"
                    _scanUiState.value = ScanUiState.Idle
                    return@launch
                }

                scanManuscriptUseCase(
                    url = url,
                    category = "Full Manuscript",
                    theme = _researchTheme.value.ifBlank { null },
                    researchType = _researchType.value
                )
            }

            result.fold(
                onSuccess = { response ->
                    _scanUiState.value = ScanUiState.Success(response)
                    onSuccess(response)
                },
                onFailure = { throwable ->
                    _scanUiState.value = ScanUiState.Error(throwable.message ?: "An unknown error occurred")
                }
            )
        }
    }

    fun resetState() {
        _currentStep.value = WizardStep.ChooseUploadType
        _history.value = listOf(WizardStep.ChooseUploadType)
        _uploadType.value = null
        _selectedChapters.value = emptySet()
        _chapterUrls.value = emptyMap()
        _wholeManuscriptUrl.value = ""
        _selectedTab.value = 0
        _researchType.value = "Quantitative"
        _researchTheme.value = ""
        _scanUiState.value = ScanUiState.Idle
        _urlError.value = null
        _chapterErrors.value = emptyMap()
    }
}
