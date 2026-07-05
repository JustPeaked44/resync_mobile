package com.example.data.local

import com.example.data.remote.dto.*

data class DetailedHistoryScan(
    val id: String,
    val title: String,
    val score: Int,
    val url: String,
    val date: String,
    val inconsistencies: List<InconsistencyItem>,
    val references: List<CitationItem> = emptyList(),
    val duplicateSections: List<DuplicateItem> = emptyList(),
    val manuscriptText: String? = null
)

object MockScanDatabase {

    val novelManuscriptText = """
[Section 1.1 Scope]
The scope of this strategic project focuses on the behavior of our core users during testing sessions.

[Section 1.2 Introduction]
This study explores user engagement patterns. Specifically, the study includes 200 participants selected from our developer community.

[Section 2 Literature Review]
Prior research on digital collaboration emphasizes retention rate as the primary metric. Many scholars argue that long-term retention rate determines platform viability.

[Section 3.1 Setup]
Our standard regression setup includes configuring three servers to run simulated user actions sequentially.

[Section 3.2 Analysis]
We configure three servers to run simulated user actions sequentially, which forms our standard regression setup.

[Section 3.4 Methodology]
To measure consistency, our empirical test utilizes N=150 as the baseline sample size.

[Section 4 Discussion]
The results indicate that session length of participants increased significantly, suggesting high platform alignment.

[Section 5.1 Analysis]
We analyzed the behavior of all subjects over the trial period.
""".trimIndent()

    val essayManuscriptText = """
[Introduction]
In contemporary society, technology is harmful to writing. It distracts authors with notifications and reduces cognitive capacity for deep, structured prose. We must return to ink and paper.

[Body Paragraph 1]
Historically, the physical act of writing slow and deliberate thoughts has produced the finest literature of our civilisations.

[Conclusion]
Ultimately, technology is highly beneficial for modern literature. It democratises access, simplifies editing, and provides global platforms for self-publishing authors, proving itself an indispensable asset.

[References]
Doe et al. (2019) on human factors.
""".trimIndent()

    val poetryManuscriptText = """
[Preface]
This anthology of poetry is written using traditional structured forms, strictly avoiding the modern chaos of free verse to preserve rhythmic elegance.

[Selected Poem]
The wind whispers through the silent trees,
A quiet shadow on the summer breeze.

[Postface]
In the end, free verse is the only true form of poetic expression, as rules and meters restrict the authentic soul's creative energy.
""".trimIndent()

    private fun createInconsistency(
        type: InconsistencyType,
        severity: Severity,
        sectionA: String,
        sectionB: String,
        description: String,
        recommendedCorrection: String,
        explanation: ExplanationDetail? = null,
        text: String? = null,
        phraseA: String? = null,
        phraseB: String? = null
    ): InconsistencyItem {
        var startA: Int? = null
        var endA: Int? = null
        var startB: Int? = null
        var endB: Int? = null

        if (text != null) {
            if (phraseA != null) {
                val idx = text.indexOf(phraseA)
                if (idx != -1) {
                    startA = idx
                    endA = idx + phraseA.length
                }
            }
            if (phraseB != null) {
                val idx = text.indexOf(phraseB)
                if (idx != -1) {
                    startB = idx
                    endB = idx + phraseB.length
                }
            }
        }

        return InconsistencyItem(
            type = type,
            severity = severity,
            sectionA = sectionA,
            sectionB = sectionB,
            description = description,
            recommendedCorrection = recommendedCorrection,
            explanation = explanation,
            startCharOffsetA = startA,
            endCharOffsetA = endA,
            startCharOffsetB = startB,
            endCharOffsetB = endB
        )
    }

    private fun createDuplicate(
        sectionA: String,
        sectionB: String,
        similarityScore: Double,
        matchedText: String,
        text: String? = null,
        phraseA: String? = null,
        phraseB: String? = null
    ): DuplicateItem {
        var startA: Int? = null
        var endA: Int? = null
        var startB: Int? = null
        var endB: Int? = null

        if (text != null) {
            if (phraseA != null) {
                val idx = text.indexOf(phraseA)
                if (idx != -1) {
                    startA = idx
                    endA = idx + phraseA.length
                }
            }
            if (phraseB != null) {
                val idx = text.indexOf(phraseB)
                if (idx != -1) {
                    startB = idx
                    endB = idx + phraseB.length
                }
            }
        }

        return DuplicateItem(
            sectionA = sectionA,
            sectionB = sectionB,
            similarityScore = similarityScore,
            matchedText = matchedText,
            startCharOffsetA = startA,
            endCharOffsetA = endA,
            startCharOffsetB = startB,
            endCharOffsetB = endB
        )
    }

    private fun createCitation(
        citationText: String,
        linkStatus: LinkStatus,
        detailedExplanation: String,
        text: String? = null,
        phrase: String? = null
    ): CitationItem {
        var start: Int? = null
        var end: Int? = null

        if (text != null && phrase != null) {
            val idx = text.indexOf(phrase)
            if (idx != -1) {
                start = idx
                end = idx + phrase.length
            }
        }

        return CitationItem(
            citationText = citationText,
            linkStatus = linkStatus,
            detailedExplanation = detailedExplanation,
            startCharOffset = start,
            endCharOffset = end
        )
    }

    val scans = listOf(
        // Novel Draft - Chapter 1 Versions
        DetailedHistoryScan(
            id = "novel_v3",
            title = "Novel Draft - Chapter 1 (Final Review)",
            score = 92,
            url = "https://docs.google.com/document/d/1chapter1_draft",
            date = "2026-07-02",
            manuscriptText = novelManuscriptText,
            inconsistencies = listOf(
                createInconsistency(
                    type = InconsistencyType.REDUNDANCY,
                    severity = Severity.LOW,
                    sectionA = "Section 3.1 Setup",
                    sectionB = "Section 3.2 Analysis",
                    description = "Duplicated methodology description of standard regression setup.",
                    recommendedCorrection = "Remove the redundant description in Section 3.2 and refer back to Section 3.1.",
                    text = novelManuscriptText,
                    phraseA = "Our standard regression setup includes configuring three servers to run simulated user actions sequentially.",
                    phraseB = "We configure three servers to run simulated user actions sequentially, which forms our standard regression setup."
                )
            ),
            references = listOf(
                createCitation(
                    citationText = "Smith et al. (2021) on system scaling limits",
                    linkStatus = LinkStatus.VALIDATED,
                    detailedExplanation = "Reference link is live and matches paper DOI.",
                    text = novelManuscriptText,
                    phrase = "Prior research on digital collaboration"
                )
            ),
            duplicateSections = listOf(
                createDuplicate(
                    sectionA = "Section 3.1 Setup",
                    sectionB = "Section 3.2 Analysis",
                    similarityScore = 0.88,
                    matchedText = "Our standard regression setup includes configuring three servers to run simulated user actions sequentially.",
                    text = novelManuscriptText,
                    phraseA = "Our standard regression setup includes configuring three servers to run simulated user actions sequentially.",
                    phraseB = "We configure three servers to run simulated user actions sequentially, which forms our standard regression setup."
                )
            )
        ),
        DetailedHistoryScan(
            id = "novel_v2",
            title = "Novel Draft - Chapter 1 (Revision 1)",
            score = 78,
            url = "https://docs.google.com/document/d/1chapter1_draft",
            date = "2026-06-28",
            manuscriptText = novelManuscriptText,
            inconsistencies = listOf(
                createInconsistency(
                    type = InconsistencyType.LOGIC_GAP,
                    severity = Severity.MEDIUM,
                    sectionA = "Section 2 Literature Review",
                    sectionB = "Section 4 Discussion",
                    description = "There is a logical disconnect in user metrics discussion.",
                    recommendedCorrection = "Include a summary of prior literature findings in discussion.",
                    explanation = ExplanationDetail(
                        whatWasFound = "The Literature Review focuses heavily on 'retention rate' as the primary metric, but the Discussion section concludes by evaluating 'session length' without tying it back to retention.",
                        whyItMatters = "When the discussion fails to address the metrics established in the literature review, the paper narrative appears fragmented.",
                        suggestedFix = "Add a paragraph in the Discussion connecting 'session length' to the 'retention rate' theories discussed in Section 2."
                    ),
                    text = novelManuscriptText,
                    phraseA = "emphasizes retention rate as the primary metric",
                    phraseB = "session length of participants increased significantly"
                ),
                createInconsistency(
                    type = InconsistencyType.REDUNDANCY,
                    severity = Severity.LOW,
                    sectionA = "Section 3.1 Setup",
                    sectionB = "Section 3.2 Analysis",
                    description = "Duplicated methodology description of standard regression setup.",
                    recommendedCorrection = "Remove the redundant description in Section 3.2 and refer back to Section 3.1.",
                    text = novelManuscriptText,
                    phraseA = "Our standard regression setup includes configuring three servers to run simulated user actions sequentially.",
                    phraseB = "We configure three servers to run simulated user actions sequentially, which forms our standard regression setup."
                )
            ),
            references = listOf(
                createCitation(
                    citationText = "Smith et al. (2021) on system scaling limits",
                    linkStatus = LinkStatus.VALIDATED,
                    detailedExplanation = "Reference link is live and matches paper DOI.",
                    text = novelManuscriptText,
                    phrase = "Prior research on digital collaboration"
                )
            ),
            duplicateSections = listOf(
                createDuplicate(
                    sectionA = "Section 3.1 Setup",
                    sectionB = "Section 3.2 Analysis",
                    similarityScore = 0.88,
                    matchedText = "Our standard regression setup includes configuring three servers to run simulated user actions sequentially.",
                    text = novelManuscriptText,
                    phraseA = "Our standard regression setup includes configuring three servers to run simulated user actions sequentially.",
                    phraseB = "We configure three servers to run simulated user actions sequentially, which forms our standard regression setup."
                )
            )
        ),
        DetailedHistoryScan(
            id = "novel_v1",
            title = "Novel Draft - Chapter 1 (Initial Scan)",
            score = 54,
            url = "https://docs.google.com/document/d/1chapter1_draft",
            date = "2026-06-20",
            manuscriptText = novelManuscriptText,
            inconsistencies = listOf(
                createInconsistency(
                    type = InconsistencyType.CONTRADICTION,
                    severity = Severity.HIGH,
                    sectionA = "Section 1.2 Introduction",
                    sectionB = "Section 3.4 Methodology",
                    description = "The target sample size contradicts across sections.",
                    recommendedCorrection = "Reconcile sample sizes to be exactly 150 participants.",
                    explanation = ExplanationDetail(
                        whatWasFound = "Section 1.2 states 'the study includes 200 participants', while Section 3.4 specifies 'N=150'.",
                        whyItMatters = "Conflicting sample sizes undermine the reproducibility of the study and the validity of statistical power calculations.",
                        suggestedFix = "Review the final dataset and update all sections to consistently reflect the actual number of participants."
                    ),
                    text = novelManuscriptText,
                    phraseA = "the study includes 200 participants",
                    phraseB = "N=150"
                ),
                createInconsistency(
                    type = InconsistencyType.LOGIC_GAP,
                    severity = Severity.MEDIUM,
                    sectionA = "Section 2 Literature Review",
                    sectionB = "Section 4 Discussion",
                    description = "There is a logical disconnect in user metrics discussion.",
                    recommendedCorrection = "Include a summary of prior literature findings in discussion.",
                    explanation = ExplanationDetail(
                        whatWasFound = "The Literature Review focuses heavily on 'retention rate' as the primary metric, but the Discussion section concludes by evaluating 'session length' without tying it back to retention.",
                        whyItMatters = "When the discussion fails to address the metrics established in the literature review, the paper narrative appears fragmented.",
                        suggestedFix = "Add a paragraph in the Discussion connecting 'session length' to the 'retention rate' theories discussed in Section 2."
                    ),
                    text = novelManuscriptText,
                    phraseA = "emphasizes retention rate as the primary metric",
                    phraseB = "session length of participants increased significantly"
                ),
                createInconsistency(
                    type = InconsistencyType.TERMINOLOGY_CLASH,
                    severity = Severity.LOW,
                    sectionA = "Section 1.1 Scope",
                    sectionB = "Section 5.1 Analysis",
                    description = "Conflict in terminology of 'users' vs 'subjects'.",
                    recommendedCorrection = "Use 'users' consistently throughout the document.",
                    text = novelManuscriptText,
                    phraseA = "core users",
                    phraseB = "all subjects"
                )
            )
        ),

        // Creative Writing Essay Versions
        DetailedHistoryScan(
            id = "essay_v2",
            title = "Creative Writing Essay (Revised Draft)",
            score = 85,
            url = "https://docs.google.com/document/d/2essay_coherence",
            date = "2026-06-29",
            manuscriptText = essayManuscriptText,
            inconsistencies = emptyList(),
            references = listOf(
                createCitation(
                    citationText = "Doe et al. (2019) on human factors",
                    linkStatus = LinkStatus.VALIDATED,
                    detailedExplanation = "Reference link is live and matches paper DOI.",
                    text = essayManuscriptText,
                    phrase = "Doe et al. (2019) on human factors."
                )
            )
        ),
        DetailedHistoryScan(
            id = "essay_v1",
            title = "Creative Writing Essay (First Draft)",
            score = 48,
            url = "https://docs.google.com/document/d/2essay_coherence",
            date = "2026-06-25",
            manuscriptText = essayManuscriptText,
            inconsistencies = listOf(
                createInconsistency(
                    type = InconsistencyType.CONTRADICTION,
                    severity = Severity.HIGH,
                    sectionA = "Introduction",
                    sectionB = "Conclusion",
                    description = "The central thesis is contradicted in conclusion.",
                    recommendedCorrection = "Align conclusion with the introductory thesis.",
                    explanation = ExplanationDetail(
                        whatWasFound = "Introduction states technology is harmful to writing, while conclusion argues technology is highly beneficial.",
                        whyItMatters = "Contradicting your main thesis ruins academic validity.",
                        suggestedFix = "Rewrite conclusion to support technology's balanced influence as framed in introduction."
                    ),
                    text = essayManuscriptText,
                    phraseA = "technology is harmful to writing",
                    phraseB = "technology is highly beneficial for modern literature"
                )
            ),
            references = listOf(
                createCitation(
                    citationText = "Doe et al. (2019) on human factors",
                    linkStatus = LinkStatus.UNRESOLVED,
                    detailedExplanation = "Reference link is not reachable, check URL.",
                    text = essayManuscriptText,
                    phrase = "Doe et al. (2019) on human factors."
                )
            )
        ),

        // Manuscript Draft Redux
        DetailedHistoryScan(
            id = "redux_v1",
            title = "Manuscript Draft Redux (Initial Scan)",
            score = 92,
            url = "https://docs.google.com/document/d/3redux_final",
            date = "2026-06-27",
            manuscriptText = novelManuscriptText,
            inconsistencies = emptyList()
        ),

        // Poetry Anthology
        DetailedHistoryScan(
            id = "poetry_v1",
            title = "Poetry Anthology (Initial Scan)",
            score = 45,
            url = "https://docs.google.com/document/d/4poetry_draft",
            date = "2026-06-25",
            manuscriptText = poetryManuscriptText,
            inconsistencies = listOf(
                createInconsistency(
                    type = InconsistencyType.LOGIC_GAP,
                    severity = Severity.MEDIUM,
                    sectionA = "Preface",
                    sectionB = "Postface",
                    description = "Prefatory statement conflicts with thematic choice.",
                    recommendedCorrection = "Align preface themes.",
                    text = poetryManuscriptText,
                    phraseA = "strictly avoiding the modern chaos of free verse",
                    phraseB = "free verse is the only true form of poetic expression"
                )
            )
        )
    )
}
