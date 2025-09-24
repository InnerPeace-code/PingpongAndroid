package com.pingpong.app.feature.admin

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
fun AdminChangeManageRoute(
    viewModel: AdminChangeManageViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.message) {
        if (state.message != null) {
            delay(2500)
            viewModel.clearMessage()
        }
    }

    AdminChangeManageScreen(
        state = state,
        onRefresh = viewModel::refresh,
        onHandle = viewModel::handle
    )
}

@Composable
private fun AdminChangeManageScreen(
    state: AdminChangeManageUiState,
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
                Text(text = "Coach change requests", style = MaterialTheme.typography.titleLarge)
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
                    Text(text = "Loading requests...")
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
                        AdminChangeCard(request = request, onHandle = { approve -> onHandle(request.id, approve) })
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminChangeCard(
    request: CoachChangeRequest,
    onHandle: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = "Request #${request.id}", style = MaterialTheme.typography.titleMedium)
            request.studentName?.let { Text(text = "Student: $it") }
            request.currentCoachName?.let { Text(text = "Current coach: $it") }
            request.targetCoachName?.let { Text(text = "Target coach: $it") }
            request.reason?.let { Text(text = "Reason: $it") }
            request.status?.let { Text(text = "Status: $it") }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { onHandle(true) }) {
                    Text(text = "Approve")
                }
                OutlinedButton(onClick = { onHandle(false) }) {
                    Text(text = "Reject")
                }
            }
        }
    }
}
