package com.redjadet.superdemo.features.dashboard.domain

data class ReadinessMetric(
    val id: String,
    val title: String,
    val status: ReadinessStatus,
    val detail: String,
)

enum class ReadinessStatus {
    Ready,
    Partial,
    Risk,
}

data class EngineeringDemo(
    val id: String,
    val title: String,
    val summary: String,
)

data class DashboardSnapshot(
    val headline: String,
    val metrics: List<ReadinessMetric>,
    val demos: List<EngineeringDemo>,
    val risks: List<String>,
)

interface DashboardRepository {
    suspend fun load(): DashboardSnapshot
}
