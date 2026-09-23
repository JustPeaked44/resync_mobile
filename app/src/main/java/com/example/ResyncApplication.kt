package com.example

import android.app.Application
import android.util.Log
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel

class ResyncApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize OneSignal only if a valid UUID App ID is configured
        val oneSignalAppId = "" // Set to real OneSignal UUID in production
        
        if (oneSignalAppId.isNotBlank()) {
            try {
                OneSignal.Debug.logLevel = LogLevel.VERBOSE
                OneSignal.initWithContext(this, oneSignalAppId)
                Log.d("ResyncApplication", "OneSignal initialized successfully with App ID: $oneSignalAppId")
            } catch (e: Throwable) {
                Log.e("ResyncApplication", "Failed to initialize OneSignal: ${e.message}", e)
            }
        }
    }
}
