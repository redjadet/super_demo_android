package com.redjadet.superdemo.features.dashboard.domain

data class DemoCapability(
    val id: String,
    val title: String,
    val detail: String,
)

data class EngineeringDemo(
    val id: String,
    val title: String,
    val summary: String,
)

data class DashboardSnapshot(
    val headline: String,
    val capabilities: List<DemoCapability>,
    val demos: List<EngineeringDemo>,
    val risks: List<String>,
)

interface DashboardRepository {
    suspend fun load(): DashboardSnapshot
}
