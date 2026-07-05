package com.example.domain.repository

import com.example.data.remote.dto.ScanRequest
import com.example.data.remote.dto.ScanResponse

interface ScanRepository {
    suspend fun scanManuscript(request: ScanRequest): Result<ScanResponse>
}
