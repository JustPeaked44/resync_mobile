package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class HighlightType(val label: String, val bg: Color, val border: Color) {
    LOGIC_GAP("Logic Gap", Color(0xFFEDE9FE), Color(0xFF7C3AED)),
    CONTRADICTION("Contradiction", Color(0xFFFEE2E2), Color(0xFFDC2626)),
    REDUNDANCY("Redundancy", Color(0xFFFEF9C3), Color(0xFFCA8A04))
}

data class Highlight(
    val phrase: String,
    val type: HighlightType,
    val issueId: Int
)

data class Paragraph(
    val text: String,
    val highlights: List<Highlight> = emptyList()
)

data class ManuscriptSection(
    val chapter: String,
    val paragraphs: List<Paragraph>
)

data class IssueItem(
    val id: Int,
    val type: String,
    val color: Color,
    val from: String,
    val to: String,
    val desc: String,
    val fix: String
)

data class CitationReference(
    val citation: String,
    val status: String // "ok" or "dead"
)

val MANUSCRIPT_SECTIONS = listOf(
    ManuscriptSection(
        chapter = "Abstract",
        paragraphs = listOf(
            Paragraph(
                text = "This study investigates the factors affecting technology adoption among higher education students in Metro Manila institutions. The research aims to evaluate user satisfaction, identify adoption barriers, and propose implementation frameworks for educational technology systems.",
                highlights = listOf(
                    Highlight("evaluate user satisfaction", HighlightType.LOGIC_GAP, 1),
                    Highlight("Metro Manila institutions", HighlightType.CONTRADICTION, 4)
                )
            )
        )
    ),
    ManuscriptSection(
        chapter = "Chapter 1 — Introduction",
        paragraphs = listOf(
            Paragraph(
                text = "The rapid integration of educational technology in Philippine higher education institutions has created both opportunities and challenges. Adoption rates have been inconsistent across institutions, with user satisfaction remaining a critical but underexplored metric.",
                highlights = listOf(
                    Highlight("user satisfaction remaining a critical but underexplored metric", HighlightType.REDUNDANCY, 3)
                )
            ),
            Paragraph(
                text = "This research is conducted within one university in Quezon City, focusing on undergraduate students enrolled in BSIT and BSCpE programs during the academic year 2023–2024.",
                highlights = listOf(
                    Highlight("one university in Quezon City", HighlightType.CONTRADICTION, 4)
                )
            )
        )
    ),
    ManuscriptSection(
        chapter = "Chapter 2 — Review of Literature",
        paragraphs = listOf(
            Paragraph(
                text = "Prior studies indicate that technology adoption rates plateau after 3 months of deployment, as users settle into habitual usage patterns (Cruz et al., 2022). The Technology Acceptance Model posits that perceived usefulness and ease of use are primary determinants of adoption intent.",
                highlights = listOf(
                    Highlight("adoption rates plateau after 3 months", HighlightType.CONTRADICTION, 2)
                )
            )
        )
    ),
    ManuscriptSection(
        chapter = "Chapter 3 — Methodology",
        paragraphs = listOf(
            Paragraph(
                text = "This study employs a descriptive-correlational research design. Data was collected through a structured survey questionnaire administered to 120 respondents. Statistical tools include Pearson correlation and regression analysis.",
                highlights = emptyList()
            ),
            Paragraph(
                text = "The research instruments consist of a validated Technology Acceptance Model questionnaire and a system usability checklist. No additional tools were used for satisfaction measurement.",
                highlights = listOf(
                    Highlight("No additional tools were used for satisfaction measurement", HighlightType.LOGIC_GAP, 1)
                )
            )
        )
    ),
    ManuscriptSection(
        chapter = "Chapter 4 — Findings",
        paragraphs = listOf(
            Paragraph(
                text = "Results show steady growth in technology adoption observed consistently across the 6-month monitoring period, with no plateau detected in the dataset. Adoption rates increased month-over-month throughout the study window.",
                highlights = listOf(
                    Highlight("steady growth in technology adoption observed consistently across the 6-month monitoring period", HighlightType.CONTRADICTION, 2)
                )
            )
        )
    ),
    ManuscriptSection(
        chapter = "Chapter 5 — Conclusion",
        paragraphs = listOf(
            Paragraph(
                text = "This study investigates the factors affecting technology adoption among higher education students in Metro Manila institutions. The research aims to evaluate user satisfaction, identify adoption barriers, and propose implementation frameworks for educational technology systems.",
                highlights = listOf(
                    Highlight("This study investigates the factors affecting technology adoption among higher education students in Metro Manila institutions. The research aims to evaluate user satisfaction, identify adoption barriers, and propose implementation frameworks for educational technology systems.", HighlightType.REDUNDANCY, 3)
                )
            )
        )
    )
)

val SAMPLE_ISSUES = listOf(
    IssueItem(
        id = 1,
        type = "Logic Gap",
        color = Color(0xFF6366F1),
        from = "Objectives",
        to = "Methodology",
        desc = "Objective 3 ('evaluate user satisfaction') has no corresponding data collection instrument in the methodology.",
        fix = "Add a validated satisfaction instrument (e.g., SUS scale) to Section 3.4."
    ),
    IssueItem(
        id = 2,
        type = "Contradiction",
        color = Color(0xFFEF4444),
        from = "RRL",
        to = "Findings",
        desc = "RRL states 'adoption plateaus at 3 months' but Findings report 'steady growth over 6 months.'",
        fix = "Reconcile in Chapter 4 or update the RRL to cite matching studies."
    ),
    IssueItem(
        id = 3,
        type = "Redundancy",
        color = Color(0xFFF97316),
        from = "Abstract",
        to = "Conclusion",
        desc = "Abstract and Conclusion are 85% similar — Conclusion lacks synthesis.",
        fix = "Expand Conclusion with implications and future work beyond restating the Abstract."
    ),
    IssueItem(
        id = 4,
        type = "Contradiction",
        color = Color(0xFFEF4444),
        from = "Title",
        to = "Scope",
        desc = "Title says 'Metro Manila institutions' but scope limits the study to one university in Quezon City.",
        fix = "Either expand sampling or revise the title to reflect the single-institution scope."
    )
)

val SAMPLE_REFS = listOf(
    CitationReference("Cruz et al. (2022). Adaptive learning in PH HEIs.", "ok"),
    CitationReference("Santos & Reyes (2021). Technology adoption.", "ok"),
    CitationReference("Dela Cruz (2020). Student engagement metrics.", "dead"),
    CitationReference("Reyes (2023). AI in academic writing.", "dead")
)
