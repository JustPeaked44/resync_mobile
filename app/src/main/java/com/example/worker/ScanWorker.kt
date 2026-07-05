package com.example.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.MainActivity
import com.example.data.local.SessionManager
import com.example.data.remote.OneSignalApiService
import com.example.data.remote.OneSignalNotificationRequest
import com.example.data.remote.dto.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.URLEncoder

class ScanWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        val QUANTITATIVE_SECTIONS = listOf("Introduction", "Literature Review", "Methodology", "Statistical Treatment", "Results", "Discussion", "Conclusion")
        val QUALITATIVE_SECTIONS = listOf("Introduction", "Literature Review", "Methodology", "Thematic Analysis", "Findings", "Discussion", "Conclusion")
    }

    override suspend fun doWork(): Result {
        val url = inputData.getString("url") ?: return Result.failure()
        val isDemo = inputData.getBoolean("is_demo", true)
        val pushSubscriptionId = inputData.getString("push_subscription_id")
        
        val sessionManager = SessionManager(context)
        
        val responseToSave: ScanResponse
        
        if (isDemo) {
            delay(6000)
            
            val researchType = inputData.getString("research_type")
            
            // Mocking detected sections for demo
            val detectedSections = listOf("Introduction", "Literature Review", "Methodology", "Discussion")
            
            val expectedSections = if (researchType == "Quantitative") {
                QUANTITATIVE_SECTIONS
            } else if (researchType == "Qualitative") {
                QUALITATIVE_SECTIONS
            } else emptyList()
            
            val missingSectionsList = expectedSections.filter { !detectedSections.contains(it) }
            
            responseToSave = ScanResponse(
                coherenceScore = 88,
                overallAssessment = "Excellent Coherence",
                inconsistencies = listOf(
                    InconsistencyItem(
                        type = InconsistencyType.CONTRADICTION,
                        severity = Severity.HIGH,
                        sectionA = "Section 1.2 Introduction",
                        sectionB = "Section 3.4 Methodology",
                        description = "The target sample size contradicts across sections.",
                        recommendedCorrection = "Reconcile sample sizes to be exactly 150 participants.",
                        explanation = com.example.data.remote.dto.ExplanationDetail(
                            whatWasFound = "Section 1.2 states 'the study includes 200 participants', while Section 3.4 specifies 'N=150'.",
                            whyItMatters = "Conflicting sample sizes undermine the reproducibility of the study and the validity of statistical power calculations.",
                            suggestedFix = "Review the final dataset and update all sections to consistently reflect the actual number of participants."
                        )
                    )
                ),
                references = listOf(
                    CitationItem(
                        citationText = "Smith et al. (2021) on system scaling limits",
                        linkStatus = LinkStatus.VALIDATED,
                        detailedExplanation = "Reference link is live and matches paper DOI."
                    )
                ),
                duplicateSections = emptyList(),
                missingSections = missingSectionsList,
                createdAt = "2026-07-02T13:55:00Z"
            )
        } else {
            val category = inputData.getString("category") ?: "Full Manuscript"
            val theme = inputData.getString("theme")
            val researchType = inputData.getString("research_type")
            
            try {
                if (!url.contains("docs.google.com")) {
                    return Result.success(workDataOf("error" to "Invalid Google Docs URL"))
                }
                
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://api.resync.example.com/") // Placeholder baseUrl for FastAPI backend routing
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()
                val apiService = retrofit.create(com.example.data.remote.ResyncApiService::class.java)
                
                val request = ScanRequest(
                    documentUrl = url,
                    chapterCategory = category,
                    researchTheme = theme,
                    researchType = researchType
                )
                
                val httpResponse = apiService.submitManuscriptForScan(request)
                if (httpResponse.isSuccessful && httpResponse.body() != null) {
                    responseToSave = httpResponse.body()!!
                } else {
                    return Result.success(workDataOf("error" to "Error ${httpResponse.code()}: ${httpResponse.message()}"))
                }
            } catch (e: Exception) {
                return Result.success(workDataOf("error" to (e.message ?: "Network Error")))
            }
        }
        
        sessionManager.incrementScansCount()
        
        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        val adapter = moshi.adapter(ScanResponse::class.java)
        val json = adapter.toJson(responseToSave)

        val pushEnabled = sessionManager.pushNotificationsEnabled.first()
        if (pushEnabled) {
            val title = "Scan Complete"
            val score = responseToSave.coherenceScore
            val highSeverityCount = responseToSave.inconsistencies.count { it.severity == Severity.HIGH }
            val content = "Overall Coherence Score: $score. High severity issues: $highSeverityCount."
            val encodedUrl = URLEncoder.encode(url, "UTF-8")
            
            showLocalNotification(title, content, score, encodedUrl)
            
            if (!pushSubscriptionId.isNullOrEmpty()) {
                sendOneSignalPush(pushSubscriptionId, score.toString(), title, content)
            }
        }

        return Result.success(workDataOf("result_json" to json))
    }

    private fun showLocalNotification(title: String, content: String, score: Int, encodedUrl: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "scan_results_channel"
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Scan Results",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        
        val deepLinkUri = Uri.parse("resync://results/$score/$encodedUrl")
        val deepLinkIntent = Intent(Intent.ACTION_VIEW, deepLinkUri, context, MainActivity::class.java)
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            deepLinkIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
            
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private suspend fun sendOneSignalPush(subscriptionId: String, scanId: String, title: String, content: String) {
        try {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://onesignal.com/")
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                
            val api = retrofit.create(OneSignalApiService::class.java)
            
            val request = OneSignalNotificationRequest(
                app_id = "resync-companion-app-id-2026",
                include_subscription_ids = listOf(subscriptionId),
                headings = mapOf("en" to title),
                contents = mapOf("en" to content),
                data = mapOf("scan_id" to scanId)
            )
            api.sendNotification(request)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
