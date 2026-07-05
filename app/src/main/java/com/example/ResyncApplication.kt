package com.example

import android.app.Application
import android.util.Log
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel

class ResyncApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Enable verbose OneSignal logging to aid in diagnostics
        OneSignal.Debug.logLevel = LogLevel.VERBOSE
        
        // Initialize OneSignal with a secure or configured app ID
        val oneSignalAppId = "resync-companion-app-id-2026" // In a production app, loaded securely
        
        try {
            OneSignal.initWithContext(this, oneSignalAppId)
            Log.d("ResyncApplication", "OneSignal initialized successfully with App ID: $oneSignalAppId")
        } catch (e: Exception) {
            Log.e("ResyncApplication", "Failed to initialize OneSignal: ${e.message}", e)
        }
    }
}
