package com.example.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.presentation.dashboard.DetailedInconsistency
import com.example.presentation.dashboard.DetailedReference
import java.io.OutputStream

object PdfExportUtil {

    fun generateAndSaveReportPdf(
        context: Context,
        documentTitle: String,
        coherenceScore: Int,
        analysisDate: String,
        missingSections: List<String>,
        inconsistencies: List<DetailedInconsistency>,
        references: List<DetailedReference>
    ): Uri? {
        val pdfDocument = PdfDocument()
        
        // Page specification: Letter size 612 x 792 points (8.5" x 11" at 72 points per inch)
        val pageWidth = 612
        val pageHeight = 792
        val margin = 54 // 0.75 in
        
        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var currentPage = pdfDocument.startPage(pageInfo)
        var canvas = currentPage.canvas
        
        // Setup Paint objects
        val textPaint = Paint().apply {
            color = android.graphics.Color.rgb(0x33, 0x41, 0x55) // Slate 700 (#334155)
            textSize = 10f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            isAntiAlias = true
        }
        
        val titlePaint = Paint().apply {
            color = android.graphics.Color.rgb(0x0F, 0x17, 0x2A) // Slate 900 (#0F172A)
            textSize = 22f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            isAntiAlias = true
        }
        
        val sectionHeadingPaint = Paint().apply {
            color = android.graphics.Color.rgb(0x4F, 0x46, 0xE5) // Indigo 600 (#4F46E5)
            textSize = 14f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            isAntiAlias = true
        }

        val subHeadingPaint = Paint().apply {
            color = android.graphics.Color.rgb(0x0F, 0x17, 0x2A) // Slate 900 (#0F172A)
            textSize = 11f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            isAntiAlias = true
        }

        val italicPaint = Paint().apply {
            color = android.graphics.Color.rgb(0x64, 0x74, 0x8B) // Slate 500 (#64748B)
            textSize = 10f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC)
            isAntiAlias = true
        }

        val warningPaint = Paint().apply {
            color = android.graphics.Color.rgb(0xEF, 0x44, 0x44) // Red 500 (#EF4444)
            textSize = 11f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            isAntiAlias = true
        }
        
        val linePaint = Paint().apply {
            color = android.graphics.Color.rgb(0xE2, 0xE8, 0xF0) // Slate 200 (#E2E8F0)
            strokeWidth = 1f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        var currentY = margin.toFloat()

        fun checkNewPage(neededHeight: Float) {
            if (currentY + neededHeight > pageHeight - margin) {
                // Draw page number at footer before finishing
                val footerPaint = Paint().apply {
                    color = android.graphics.Color.rgb(0x94, 0xA3, 0xB8)
                    textSize = 8f
                    isAntiAlias = true
                }
                canvas.drawText("Page $pageNumber", margin.toFloat(), (pageHeight - 24).toFloat(), footerPaint)
                canvas.drawText("Resync Strategic Coherence Report", (pageWidth - margin - 180).toFloat(), (pageHeight - 24).toFloat(), footerPaint)

                pdfDocument.finishPage(currentPage)
                
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                currentPage = pdfDocument.startPage(pageInfo)
                canvas = currentPage.canvas
                currentY = margin.toFloat()
                
                // Draw thin header line on new pages
                canvas.drawLine(margin.toFloat(), currentY, (pageWidth - margin).toFloat(), currentY, linePaint)
                currentY += 15f
            }
        }

        fun drawWrappedText(text: String, x: Float, paint: Paint, width: Float, lineSpacing: Float = 14f) {
            val words = text.split(" ")
            var line = ""
            for (word in words) {
                val testLine = if (line.isEmpty()) word else "$line $word"
                val measure = paint.measureText(testLine)
                if (measure > width) {
                    checkNewPage(lineSpacing)
                    canvas.drawText(line, x, currentY, paint)
                    currentY += lineSpacing
                    line = word
                } else {
                    line = testLine
                }
            }
            if (line.isNotEmpty()) {
                checkNewPage(lineSpacing)
                canvas.drawText(line, x, currentY, paint)
                currentY += lineSpacing
            }
        }

        // Draw Title Block
        checkNewPage(40f)
        canvas.drawText("RESYNC COHERENCE REPORT", margin.toFloat(), currentY + 15, titlePaint)
        currentY += 45f

        // Metadata block (Title, Date, Score)
        checkNewPage(80f)
        canvas.drawRect(
            margin.toFloat(),
            currentY,
            (pageWidth - margin).toFloat(),
            currentY + 65f,
            Paint().apply {
                color = android.graphics.Color.rgb(0xF8, 0xFA, 0xFC)
                style = Paint.Style.FILL
            }
        )
        canvas.drawRect(
            margin.toFloat(),
            currentY,
            (pageWidth - margin).toFloat(),
            currentY + 65f,
            Paint().apply {
                color = android.graphics.Color.rgb(0xE2, 0xE8, 0xF0)
                style = Paint.Style.STROKE
                strokeWidth = 1f
            }
        )
        
        canvas.drawText("Document Title: $documentTitle", (margin + 12).toFloat(), currentY + 20f, subHeadingPaint)
        canvas.drawText("Analysis Date: $analysisDate", (margin + 12).toFloat(), currentY + 36f, textPaint)
        canvas.drawText("Coherence Score: $coherenceScore%", (margin + 12).toFloat(), currentY + 52f, sectionHeadingPaint)
        currentY += 85f

        // Missing Sections block
        if (missingSections.isNotEmpty()) {
            checkNewPage(40f)
            canvas.drawText("MISSING STRUCTURAL COMPONENTS", margin.toFloat(), currentY, warningPaint)
            currentY += 15f
            
            // Draw highlight box
            val blockHeight = (missingSections.size * 16f) + 24f
            checkNewPage(blockHeight)
            canvas.drawRect(
                margin.toFloat(),
                currentY,
                (pageWidth - margin).toFloat(),
                currentY + blockHeight,
                Paint().apply {
                    color = android.graphics.Color.rgb(0xFE, 0xF2, 0xF2)
                    style = Paint.Style.FILL
                }
            )
            canvas.drawRect(
                margin.toFloat(),
                currentY,
                (pageWidth - margin).toFloat(),
                currentY + blockHeight,
                Paint().apply {
                    color = android.graphics.Color.rgb(0xEF, 0x44, 0x44)
                    style = Paint.Style.STROKE
                    strokeWidth = 1f
                }
            )
            
            var textY = currentY + 18f
            canvas.drawText("WARNING: Expected structural sections are missing from this research type:", (margin + 12).toFloat(), textY, Paint(warningPaint).apply { textSize = 9f })
            textY += 16f
            
            missingSections.forEach { section ->
                canvas.drawText("• $section", (margin + 20).toFloat(), textY, Paint(textPaint).apply { color = android.graphics.Color.rgb(0x99, 0x1B, 0x1B); typeface = Typeface.DEFAULT_BOLD })
                textY += 14f
            }
            
            currentY += blockHeight + 25f
        }

        // Section: Inconsistencies Audit
        checkNewPage(30f)
        canvas.drawText("1. INCONSISTENCIES AUDIT", margin.toFloat(), currentY, sectionHeadingPaint)
        canvas.drawLine(margin.toFloat(), currentY + 6, (pageWidth - margin).toFloat(), currentY + 6, linePaint)
        currentY += 25f

        if (inconsistencies.isEmpty()) {
            drawWrappedText("No severe inconsistencies or alignment contradictions were detected in this manuscript. Excellent logic!", margin.toFloat(), textPaint, (pageWidth - 2 * margin).toFloat())
            currentY += 15f
        } else {
            inconsistencies.forEachIndexed { idx, inc ->
                checkNewPage(40f)
                canvas.drawText("${idx + 1}. [Severity: ${inc.severity}] ${inc.mappingHeader}", margin.toFloat(), currentY, subHeadingPaint)
                currentY += 14f
                
                // Description
                drawWrappedText("Description: ${inc.description}", margin.toFloat(), textPaint, (pageWidth - 2 * margin).toFloat())
                
                // Recommended Correction
                drawWrappedText("Recommendation: ${inc.recommendedCorrection}", margin.toFloat(), italicPaint, (pageWidth - 2 * margin).toFloat())
                
                // Explanation Details if present
                inc.explanation?.let { exp ->
                    drawWrappedText("Found: ${exp.whatWasFound}", (margin + 12).toFloat(), Paint(textPaint).apply { textSize = 9f }, (pageWidth - 2 * margin - 12).toFloat())
                    drawWrappedText("Impact: ${exp.whyItMatters}", (margin + 12).toFloat(), Paint(textPaint).apply { textSize = 9f }, (pageWidth - 2 * margin - 12).toFloat())
                    drawWrappedText("Suggested Fix: ${exp.suggestedFix}", (margin + 12).toFloat(), Paint(italicPaint).apply { textSize = 9f }, (pageWidth - 2 * margin - 12).toFloat())
                }
                
                currentY += 12f
            }
        }
        
        currentY += 15f

        // Section: Reference Validation
        checkNewPage(30f)
        canvas.drawText("2. REFERENCE VALIDATION", margin.toFloat(), currentY, sectionHeadingPaint)
        canvas.drawLine(margin.toFloat(), currentY + 6, (pageWidth - margin).toFloat(), currentY + 6, linePaint)
        currentY += 25f

        if (references.isEmpty()) {
            drawWrappedText("No cited references found or validated.", margin.toFloat(), textPaint, (pageWidth - 2 * margin).toFloat())
            currentY += 15f
        } else {
            references.forEachIndexed { idx, ref ->
                checkNewPage(30f)
                canvas.drawText("${idx + 1}. ${ref.text} [Status: ${ref.status}]", margin.toFloat(), currentY, subHeadingPaint)
                currentY += 14f
                drawWrappedText("URL: ${ref.url}", margin.toFloat(), italicPaint, (pageWidth - 2 * margin).toFloat())
                drawWrappedText("Explanation: ${ref.explanation}", margin.toFloat(), textPaint, (pageWidth - 2 * margin).toFloat())
                currentY += 10f
            }
        }

        // Draw last page footer info
        val footerPaint = Paint().apply {
            color = android.graphics.Color.rgb(0x94, 0xA3, 0xB8)
            textSize = 8f
            isAntiAlias = true
        }
        canvas.drawText("Page $pageNumber", margin.toFloat(), (pageHeight - 24).toFloat(), footerPaint)
        canvas.drawText("Resync Strategic Coherence Report", (pageWidth - margin - 180).toFloat(), (pageHeight - 24).toFloat(), footerPaint)

        pdfDocument.finishPage(currentPage)
        
        val fileName = "Resync_Report_${System.currentTimeMillis()}.pdf"
        val savedUri = savePdfToDownloads(context, pdfDocument, fileName)
        pdfDocument.close()
        return savedUri
    }

    private fun savePdfToDownloads(
        context: Context,
        pdfDocument: PdfDocument,
        fileName: String
    ): Uri? {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
        }
        
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        } else {
            // Pre-Q fallback
            try {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) downloadsDir.mkdirs()
                val file = java.io.File(downloadsDir, fileName)
                Uri.fromFile(file)
            } catch (e: Exception) {
                null
            }
        }

        if (uri != null) {
            try {
                val outputStream: OutputStream? = resolver.openOutputStream(uri)
                if (outputStream != null) {
                    pdfDocument.writeTo(outputStream)
                    outputStream.close()
                    return uri
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }
}
