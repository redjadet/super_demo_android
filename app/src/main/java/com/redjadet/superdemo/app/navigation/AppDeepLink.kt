package com.redjadet.superdemo.app.navigation

import android.content.Intent
import android.net.Uri

sealed interface AppDeepLink {
    data object Dashboard : AppDeepLink
    data object ProductionRisks : AppDeepLink
    data object Items : AppDeepLink
    data object Feed : AppDeepLink
    data class FeedPost(val id: Int) : AppDeepLink

    companion object {
        fun fromIntent(intent: Intent?): AppDeepLink? {
            val data = intent?.data ?: return null
            return fromUri(data)
        }

        fun fromUri(uri: Uri): AppDeepLink? {
            if (uri.scheme?.lowercase() != "superdemo") return null
            val host = uri.host?.lowercase().orEmpty()
            val segments = buildList {
                if (host.isNotEmpty()) add(host)
                uri.pathSegments.map { it.lowercase() }.forEach(::add)
            }
            return when {
                segments == listOf("dashboard", "risks") -> ProductionRisks
                segments == listOf("dashboard") -> Dashboard
                segments == listOf("items") -> Items
                segments == listOf("feed") -> Feed
                segments.size == 2 && segments[0] == "feed" ->
                    segments[1].toIntOrNull()?.takeIf { it > 0 }?.let(::FeedPost)
                else -> null
            }
        }
    }
}
