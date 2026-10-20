package com.redjadet.superdemo.features.dashboard.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.redjadet.superdemo.app.ui.ReviewerContentWidth
import com.redjadet.superdemo.features.dashboard.domain.DemoCapability
import com.redjadet.superdemo.features.dashboard.domain.EngineeringDemo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardRoute(
    onOpenRisks: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snapshot = state.snapshot

    Scaffold(
        topBar = { TopAppBar(title = { Text("Super Demo") }) },
    ) { padding ->
        ReviewerContentWidth(modifier = Modifier.padding(padding)) {
            if (snapshot == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.errorMessage ?: "Loading…",
                            modifier = Modifier.testTag("dashboardStatus"),
                        )
                        if (state.errorMessage != null) {
                            TextButton(
                                onClick = viewModel::refresh,
                                modifier = Modifier.testTag("dashboardRetry"),
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
                return@ReviewerContentWidth
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().testTag("dashboardList"),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(Modifier.padding(20.dp)) {
                            Text("ANDROID ENGINEERING DEMO", style = MaterialTheme.typography.labelMedium)
                            Text(
                                text = snapshot.headline,
                                style = MaterialTheme.typography.headlineLarge,
                                modifier = Modifier.padding(top = 12.dp).testTag("dashboardHeadline"),
                            )
                            Text(
                                text = "A small app built around reliable local data. Explore Feed and Items to see it in action.",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(top = 8.dp),
                            )
                        }
                    }
                }
                item {
                    TextButton(
                        onClick = onOpenRisks,
                        modifier = Modifier.testTag("dashboardRisksLink"),
                    ) {
                        Text("Demo scope")
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }
                item { Text("Explore the demo", style = MaterialTheme.typography.titleLarge) }
                items(snapshot.capabilities, key = DemoCapability::id) { capability ->
                    InfoCard(capability.title, capability.detail)
                }
                item { Text("Try it yourself", style = MaterialTheme.typography.titleLarge) }
                items(snapshot.demos, key = EngineeringDemo::id) { demo ->
                    InfoCard(demo.title, demo.summary)
                }
            }
        }
    }
}

@Composable
private fun InfoCard(title: String, detail: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = detail,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
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
                title = { Text("Demo scope") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        ReviewerContentWidth(modifier = Modifier.padding(padding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().testTag("risksList"),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    Text("Built to explore", style = MaterialTheme.typography.headlineMedium)
                    Text(
                        text = "This demo focuses on local persistence, offline reading and predictable navigation.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
                items(risks) { risk ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(risk, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}
