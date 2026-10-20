package com.redjadet.superdemo.features.dashboard.data

import com.redjadet.superdemo.features.dashboard.domain.DashboardRepository
import com.redjadet.superdemo.features.dashboard.domain.DashboardSnapshot
import com.redjadet.superdemo.features.dashboard.domain.DemoCapability
import com.redjadet.superdemo.features.dashboard.domain.EngineeringDemo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SampleDashboardRepository @Inject constructor() : DashboardRepository {
    override suspend fun load(): DashboardSnapshot = DashboardSnapshot(
        headline = "Offline by design",
        capabilities = listOf(
            DemoCapability(
                id = "offline",
                title = "A Feed that stays available",
                detail = "Read saved posts and keep bookmarks, even when a refresh fails.",
            ),
            DemoCapability(
                id = "items",
                title = "Your changes, kept locally",
                detail = "Create, edit and delete Items. Changes stay on this device between launches.",
            ),
            DemoCapability(
                id = "navigation",
                title = "Direct paths into the app",
                detail = "Open a post or tab from a deep link, with a clear state for missing content.",
            ),
        ),
        demos = listOf(
            EngineeringDemo(
                id = "offline-feed",
                title = "01  ·  Read offline",
                summary = "Open Feed, turn off connectivity, then refresh. Saved posts remain available.",
            ),
            EngineeringDemo(
                id = "room-items",
                title = "02  ·  Make it yours",
                summary = "Add an Item, open it to edit notes, then relaunch to check persistence.",
            ),
            EngineeringDemo(
                id = "bookmarks",
                title = "03  ·  Keep a bookmark",
                summary = "Bookmark a post and refresh the Feed. Your local bookmark is preserved.",
            ),
        ),
        risks = listOf(
            "Sample data: the Feed starts with local examples and refreshes from JSONPlaceholder, a public test API.",
            "Local storage: Items and bookmarks stay on this device. Account sign-in and cross-device sync are outside this demo’s scope.",
            "Foreground refresh: Feed sync runs when opened or refreshed. Background sync and cache-expiry rules are not implemented.",
            "Schema changes: this sample uses destructive Room migration, which can reset local data after a schema upgrade.",
            "Release configuration: code shrinking is disabled. Store distribution and release hardening need a separate pass.",
        ),
    )
}
