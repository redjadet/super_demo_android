package com.redjadet.superdemo.features.dashboard.data

import com.redjadet.superdemo.features.dashboard.domain.DashboardRepository
import com.redjadet.superdemo.features.dashboard.domain.DashboardSnapshot
import com.redjadet.superdemo.features.dashboard.domain.EngineeringDemo
import com.redjadet.superdemo.features.dashboard.domain.ReadinessMetric
import com.redjadet.superdemo.features.dashboard.domain.ReadinessStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SampleDashboardRepository @Inject constructor() : DashboardRepository {
    override suspend fun load(): DashboardSnapshot = DashboardSnapshot(
        headline = "Android engineering portfolio",
        metrics = listOf(
            ReadinessMetric(
                id = "layers",
                title = "Clean layers",
                status = ReadinessStatus.Ready,
                detail = "Presentation → Domain ← Data with Hilt composition roots.",
            ),
            ReadinessMetric(
                id = "offline",
                title = "Offline Feed cache",
                status = ReadinessStatus.Ready,
                detail = "Room is source of truth; remote sync updates cache transactionally.",
            ),
            ReadinessMetric(
                id = "udf",
                title = "UDF ViewModels",
                status = ReadinessStatus.Ready,
                detail = "StateFlow UI state; Feed posts stay Room-driven via Flow.",
            ),
            ReadinessMetric(
                id = "items",
                title = "Items CRUD",
                status = ReadinessStatus.Ready,
                detail = "Room Flow list + one-time seed flag (emptying table does not re-seed).",
            ),
            ReadinessMetric(
                id = "ci",
                title = "CI / delivery",
                status = ReadinessStatus.Ready,
                detail = "GitHub Actions: unit tests, assembleDebug, lintDebug, assembleDebugAndroidTest.",
            ),
        ),
        demos = listOf(
            EngineeringDemo(
                id = "stale-feed",
                title = "Stale Feed honesty",
                summary = "Fail remote refresh to keep local Room posts + stale banner.",
            ),
            EngineeringDemo(
                id = "deep-links",
                title = "Deep links",
                summary = "superdemo://feed/1, superdemo://items, superdemo://dashboard/risks",
            ),
            EngineeringDemo(
                id = "room-items",
                title = "Room Items CRUD",
                summary = "Seeded once; add / edit / delete with Flow observation.",
            ),
            EngineeringDemo(
                id = "offline-seed",
                title = "Offline first launch",
                summary = "OfflineFeedSeed fills Room so Feed opens with airplane mode on.",
            ),
        ),
        risks = listOf(
            "No production auth — demo only.",
            "JSONPlaceholder is a public sandbox; do not ship secrets against it.",
            "Release minify is intentionally off for reviewer readability.",
            "Wear / TV companions are iOS-only in the sibling repo; not claimed here.",
            "Bookmarks are local-only (no remote outbox sync in this Android sample).",
        ),
    )
}
