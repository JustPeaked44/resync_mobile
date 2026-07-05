package com.example.data.remote

import com.example.data.remote.dto.ScanRequest
import com.example.data.remote.dto.ScanResponse
import com.example.data.remote.dto.LoginRequest
import com.example.data.remote.dto.UserSession
import com.example.data.remote.dto.RegisterResponse
import com.example.data.remote.dto.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ResyncApiService {
    @POST("api/scans/run")
    suspend fun submitManuscriptForScan(
        @Body request: ScanRequest
    ): Response<ScanResponse>

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<UserSession>

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>
}
