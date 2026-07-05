package com.example.domain.usecase

import com.example.data.remote.dto.InconsistencyItem

data class ScanComparisonResult(
    val baseScanTitle: String,
    val targetScanTitle: String,
    val baseScore: Int,
    val targetScore: Int,
    val scoreDelta: Int,
    val resolvedInconsistencies: List<InconsistencyItem>,
    val newInconsistencies: List<InconsistencyItem>,
    val unresolvedInconsistencies: List<InconsistencyItem>
)

data class CompareScanInput(
    val id: String,
    val title: String,
    val coherenceScore: Int,
    val url: String,
    val date: String,
    val inconsistencies: List<InconsistencyItem>
)

class CompareScansUseCase {
    operator fun invoke(
        baseScan: CompareScanInput,
        targetScan: CompareScanInput
    ): ScanComparisonResult {
        val baseInconsistencies = baseScan.inconsistencies
        val targetInconsistencies = targetScan.inconsistencies

        val resolved = mutableListOf<InconsistencyItem>()
        val newIssues = mutableListOf<InconsistencyItem>()
        val unresolved = mutableListOf<InconsistencyItem>()

        // For each item in the base scan, check if it persists in the target scan
        for (baseItem in baseInconsistencies) {
            val stillExists = targetInconsistencies.any { targetItem ->
                areInconsistenciesSimilar(baseItem, targetItem)
            }
            if (stillExists) {
                unresolved.add(baseItem)
            } else {
                resolved.add(baseItem)
            }
        }

        // For each item in the target scan, check if it did NOT exist in the base scan
        for (targetItem in targetInconsistencies) {
            val existedInBase = baseInconsistencies.any { baseItem ->
                areInconsistenciesSimilar(baseItem, targetItem)
            }
            if (!existedInBase) {
                newIssues.add(targetItem)
            }
        }

        return ScanComparisonResult(
            baseScanTitle = baseScan.title,
            targetScanTitle = targetScan.title,
            baseScore = baseScan.coherenceScore,
            targetScore = targetScan.coherenceScore,
            scoreDelta = targetScan.coherenceScore - baseScan.coherenceScore,
            resolvedInconsistencies = resolved,
            newInconsistencies = newIssues,
            unresolvedInconsistencies = unresolved
        )
    }

    private fun areInconsistenciesSimilar(a: InconsistencyItem, b: InconsistencyItem): Boolean {
        return a.description.equals(b.description, ignoreCase = true) ||
                (a.type == b.type && a.sectionA.equals(b.sectionA, ignoreCase = true) && a.sectionB.equals(b.sectionB, ignoreCase = true))
    }
}
