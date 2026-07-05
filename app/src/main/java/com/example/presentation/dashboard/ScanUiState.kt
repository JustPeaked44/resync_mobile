package com.example.presentation.dashboard

import com.example.data.remote.dto.ScanResponse

sealed interface ScanUiState {
    data object Idle : ScanUiState
    data object Loading : ScanUiState
    data class Success(val data: ScanResponse) : ScanUiState
    data class Error(val message: String) : ScanUiState
}
