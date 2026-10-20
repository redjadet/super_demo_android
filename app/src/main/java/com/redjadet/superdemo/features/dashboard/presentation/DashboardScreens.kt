package com.redjadet.superdemo.features.dashboard.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.redjadet.superdemo.app.ui.ReviewerContentWidth
import com.redjadet.superdemo.features.dashboard.domain.ReadinessMetric
import com.redjadet.superdemo.features.dashboard.domain.ReadinessStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardRoute(
    onOpenRisks: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snapshot = state.snapshot

    Scaffold(
        topBar = { TopAppBar(title = { Text("Dashboard") }) },
    ) { padding ->
        ReviewerContentWidth(modifier = Modifier.padding(padding)) {
        if (snapshot == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = state.errorMessage ?: "Loading…",
                        modifier = Modifier.testTag("dashboardStatus"),
                    )
                    if (state.errorMessage != null) {
                        TextButton(
                            onClick = viewModel::refresh,
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .testTag("dashboardRetry"),
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
            return@ReviewerContentWidth
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("dashboardList"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text(
                    text = snapshot.headline,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.testTag("dashboardHeadline"),
                )
                Text(
                    text = "Kotlin · Jetpack Compose · Material 3 · offline-first Feed",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
            item {
                Text("Release health", style = MaterialTheme.typography.titleLarge)
            }
            items(snapshot.metrics, key = { it.id }) { metric ->
                MetricRow(metric)
            }
            item {
                TextButton(
                    onClick = onOpenRisks,
                    modifier = Modifier.testTag("dashboardRisksLink"),
                ) {
                    Text("Production risks")
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 4.dp),
                    )
                }
            }
            item {
                Text("Engineering demos", style = MaterialTheme.typography.titleLarge)
            }
            items(snapshot.demos, key = { it.id }) { demo ->
                Surface(
                    tonalElevation = 1.dp,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(demo.title, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = demo.summary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun MetricRow(metric: ReadinessMetric) {
    Surface(
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("metric_${metric.id}"),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StatusChip(metric.status)
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(metric.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = metric.detail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun StatusChip(status: ReadinessStatus) {
    val label = when (status) {
        ReadinessStatus.Ready -> "Ready"
        ReadinessStatus.Partial -> "Partial"
        ReadinessStatus.Risk -> "Risk"
    }
    val color = when (status) {
        ReadinessStatus.Ready -> MaterialTheme.colorScheme.primaryContainer
        ReadinessStatus.Partial -> MaterialTheme.colorScheme.secondaryContainer
        ReadinessStatus.Risk -> MaterialTheme.colorScheme.errorContainer
    }
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier
            .background(color, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductionRisksRoute(
    onBack: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val risks = state.snapshot?.risks.orEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Production risks") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("risksList"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text(
                    text = "Honest boundaries for this portfolio sample.",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            items(risks) { risk ->
                Surface(
                    tonalElevation = 1.dp,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = risk,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(14.dp),
                    )
                }
            }
        }
    }
}
