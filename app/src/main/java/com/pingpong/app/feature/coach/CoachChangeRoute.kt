package com.pingpong.app.feature.coach

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pingpong.app.core.model.student.CoachChangeRequest
import kotlinx.coroutines.delay

@Composable
fun CoachChangeRoute(
    viewModel: CoachChangeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.message) {
        if (state.message != null) {
            delay(2500)
            viewModel.clearMessage()
        }
    }

    CoachChangeScreen(
        state = state,
        onRefresh = viewModel::refresh,
        onHandle = viewModel::handleRequest
    )
}

@Composable
private fun CoachChangeScreen(
    state: CoachChangeUiState,
    onRefresh: () -> Unit,
    onHandle: (Long, Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Change Requests", style = MaterialTheme.typography.titleLarge)
                state.message?.let { Text(text = it, color = MaterialTheme.colorScheme.secondary) }
                state.error?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
            }
            OutlinedButton(onClick = onRefresh, enabled = !state.isLoading) {
                Text(text = "Refresh")
            }
        }

        when {
            state.isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Loading change requests...")
                }
            }
            state.requests.isEmpty() -> {
                Text(text = "No change requests")
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.requests, key = { it.id }) { request ->
                        CoachChangeCard(request = request, onHandle = onHandle)
                    }
                }
            }
        }
    }
}

@Composable
private fun CoachChangeCard(
    request: CoachChangeRequest,
    onHandle: (Long, Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "Request #${request.id}", style = MaterialTheme.typography.titleMedium)
            request.studentName?.let { Text(text = "Student: $it") }
            request.currentCoachName?.let { Text(text = "Current coach: $it") }
            request.targetCoachName?.let { Text(text = "Target coach: $it") }
            request.reason?.let { Text(text = "Reason: $it") }
            request.status?.let { Text(text = "Status: ${it.replace('_', ' ')}") }
            request.createdAt?.let { Text(text = "Submitted at: $it", style = MaterialTheme.typography.bodySmall) }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { onHandle(request.id, true) }) {
                    Text(text = "Approve")
                }
                OutlinedButton(onClick = { onHandle(request.id, false) }) {
                    Text(text = "Reject")
                }
            }
        }
    }
}
