package com.example.data.repository

import com.example.data.remote.ResyncApiService
import com.example.data.remote.dto.ScanRequest
import com.example.data.remote.dto.ScanResponse
import com.example.domain.repository.ScanRepository

class ScanRepositoryImpl(
    private val apiService: ResyncApiService
) : ScanRepository {

    override suspend fun scanManuscript(request: ScanRequest): Result<ScanResponse> {
        return try {
            val response = apiService.submitManuscriptForScan(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
