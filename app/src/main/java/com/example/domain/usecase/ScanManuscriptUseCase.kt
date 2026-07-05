package com.example.domain.usecase

import com.example.data.remote.dto.ScanRequest
import com.example.data.remote.dto.ScanResponse
import com.example.domain.repository.ScanRepository

class ScanManuscriptUseCase(
    private val repository: ScanRepository
) {
    suspend operator fun invoke(
        url: String? = null,
        category: String = "Full Manuscript",
        theme: String? = null,
        researchType: String? = null,
        chapterUrls: List<String>? = null
    ): Result<ScanResponse> {
        val finalUrl = if (chapterUrls != null && chapterUrls.isNotEmpty()) {
            // Validate each chapter URL
            for (chapterUrl in chapterUrls) {
                if (chapterUrl.isBlank()) {
                    return Result.failure(IllegalArgumentException("URL cannot be empty"))
                }
                if (!chapterUrl.contains("docs.google.com") && !chapterUrl.contains("demo")) {
                    return Result.failure(IllegalArgumentException("Invalid Google Docs URL"))
                }
            }
            chapterUrls.joinToString(",")
        } else {
            if (url == null || url.isBlank()) {
                return Result.failure(IllegalArgumentException("URL cannot be empty"))
            }
            if (!url.contains("docs.google.com") && !url.contains("demo")) {
                return Result.failure(IllegalArgumentException("Invalid Google Docs URL"))
            }
            url
        }

        return repository.scanManuscript(
            ScanRequest(
                documentUrl = finalUrl,
                chapterCategory = category,
                researchTheme = theme,
                researchType = researchType
            )
        )
    }
}
