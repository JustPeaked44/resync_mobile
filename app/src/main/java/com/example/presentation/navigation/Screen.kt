package com.example.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Auth : Screen("auth")
    object DashboardHost : Screen("dashboard_host")
    object Settings : Screen("settings")
    object ScanComparison : Screen("scan_comparison/{scanIds}") {
        fun createRoute(scanIds: String): String {
            return "scan_comparison/$scanIds"
        }
    }
    object ResultDetails : Screen("result_details/{coherenceScore}/{url}/{missingSections}") {
        fun createRoute(coherenceScore: Int, url: String, missingSections: List<String>): String {
            val encodedUrl = java.net.URLEncoder.encode(url, "UTF-8")
            val missingStr = if (missingSections.isEmpty()) "none" else missingSections.joinToString(",")
            val encodedMissing = java.net.URLEncoder.encode(missingStr, "UTF-8")
            return "result_details/$coherenceScore/$encodedUrl/$encodedMissing"
        }
    }
}
