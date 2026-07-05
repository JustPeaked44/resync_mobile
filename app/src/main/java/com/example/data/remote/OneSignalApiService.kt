package com.example.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class OneSignalNotificationRequest(
    val app_id: String,
    val include_subscription_ids: List<String>,
    val headings: Map<String, String>,
    val contents: Map<String, String>,
    val data: Map<String, String>
)

interface OneSignalApiService {
    @Headers(
        "Content-Type: application/json; charset=utf-8"
    )
    @POST("api/v1/notifications")
    suspend fun sendNotification(
        @Body request: OneSignalNotificationRequest
    ): Response<Any>
}
