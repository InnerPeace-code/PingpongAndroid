package com.pingpong.app.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pingpong.app.core.common.UiState

@Composable
fun DashboardRoute(
    onNavigateToProfile: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val metricsState by viewModel.metricsState.collectAsStateWithLifecycle()

    DashboardScreen(
        state = metricsState,
        onRefresh = viewModel::refresh
    )
}

@Composable
fun DashboardScreen(
    state: UiState<DashboardMetrics>,
    onRefresh: () -> Unit
) {
    when (state) {
        UiState.Loading -> LoadingDashboard()
        is UiState.Error -> ErrorDashboard(message = state.message, onRefresh = onRefresh)
        is UiState.Success -> MetricsList(metrics = state.data)
        else -> MetricsList(metrics = DashboardMetrics())
    }
}

@Composable
private fun LoadingDashboard() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Loading dashboard metrics...", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun ErrorDashboard(message: String?, onRefresh: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = message ?: "Unable to load dashboard", color = MaterialTheme.colorScheme.error)
        Text(
            text = "Tap refresh to try again.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun MetricsList(metrics: DashboardMetrics) {
    val items = listOf(
        "Pending coach approvals" to metrics.pendingCoachCount,
        "Certified coaches" to metrics.certifiedCoachCount,
        "Total students" to metrics.studentCount
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { (title, count) ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = title, style = MaterialTheme.typography.titleMedium)
                    Text(text = count.toString(), style = MaterialTheme.typography.displaySmall)
                }
            }
        }
    }
}
