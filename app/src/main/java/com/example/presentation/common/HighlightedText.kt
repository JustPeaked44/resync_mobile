package com.example.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Highlight
import com.example.ui.theme.PlusJakartaSansFontFamily

data class TextSegment(
    val text: String,
    val highlight: Highlight? = null
)

@Composable
fun HighlightedText(
    text: String,
    highlights: List<Highlight>,
    onPhraseClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Break down text into segments matching highlights
    val segments = mutableListOf<TextSegment>()
    var remaining = text

    val sortedHighlights = highlights.mapNotNull { h ->
        val idx = text.indexOf(h.phrase)
        if (idx != -1) Pair(idx, h) else null
    }.sortedBy { it.first }

    var currentIndex = 0
    for ((idx, h) in sortedHighlights) {
        if (idx > currentIndex) {
            segments.add(TextSegment(text.substring(currentIndex, idx), null))
        }
        segments.add(TextSegment(h.phrase, h))
        currentIndex = idx + h.phrase.length
    }
    if (currentIndex < text.length) {
        segments.add(TextSegment(text.substring(currentIndex), null))
    }

    val annotatedString = buildAnnotatedString {
        for (seg in segments) {
            if (seg.highlight != null) {
                val start = length
                append(seg.text)
                val end = length

                // Style the highlighted span
                addStyle(
                    style = SpanStyle(
                        background = seg.highlight.type.bg,
                        color = Color(0xFF1E293B),
                        fontWeight = FontWeight.Medium
                    ),
                    start = start,
                    end = end
                )
                // Attach tag for click handling
                addStringAnnotation(
                    tag = "ISSUE_ID",
                    annotation = seg.highlight.issueId.toString(),
                    start = start,
                    end = end
                )
            } else {
                append(seg.text)
            }
        }
    }

    ClickableText(
        text = annotatedString,
        modifier = modifier,
        style = TextStyle(
            color = Color(0xFF475569),
            fontSize = 14.sp,
            lineHeight = 22.sp,
            fontFamily = PlusJakartaSansFontFamily
        ),
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "ISSUE_ID", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    annotation.item.toIntOrNull()?.let { issueId ->
                        onPhraseClick(issueId)
                    }
                }
        }
    )
}
