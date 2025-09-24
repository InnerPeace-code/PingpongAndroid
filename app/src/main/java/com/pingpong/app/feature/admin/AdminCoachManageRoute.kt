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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pingpong.app.core.common.UiState
import com.pingpong.app.core.model.admin.AdminCoachSummary
import com.pingpong.app.core.model.student.CoachDetail
import kotlinx.coroutines.delay

@Composable
fun AdminCoachManageRoute(
    viewModel: AdminCoachManageViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.message) {
        if (state.message != null) {
            delay(2500)
            viewModel.clearMessage()
        }
    }

    AdminCoachManageScreen(
        state = state,
        onRefresh = viewModel::refresh,
        onViewDetail = viewModel::viewCoachDetail,
        onToggleStatus = viewModel::toggleStatus,
        onDismissDetail = viewModel::dismissDetail
    )
}

@Composable
private fun AdminCoachManageScreen(
    state: AdminCoachManageUiState,
    onRefresh: () -> Unit,
    onViewDetail: (Long) -> Unit,
    onToggleStatus: (Long, String) -> Unit,
    onDismissDetail: () -> Unit
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
                Text(text = "Certified coaches", style = MaterialTheme.typography.titleLarge)
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
                    Text(text = "Loading coaches...")
                }
            }
            state.coaches.isEmpty() -> {
                Text(text = "No certified coaches")
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.coaches, key = { it.id }) { coach ->
                        AdminCoachCard(
                            coach = coach,
                            onViewDetail = { onViewDetail(coach.id) },
                            onToggleStatus = {
                                val nextStatus = if (coach.active == true || coach.status.equals("ACTIVE", ignoreCase = true)) "INACTIVE" else "ACTIVE"
                                onToggleStatus(coach.id, nextStatus)
                            }
                        )
                    }
                }
            }
        }
    }

    when (val detail = state.detailState) {
        UiState.Loading -> {
            AlertDialog(
                onDismissRequest = onDismissDetail,
                title = { Text("Coach detail") },
                text = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                    }
                },
                confirmButton = {
                    TextButton(onClick = onDismissDetail) {
                        Text("Close")
                    }
                }
            )
        }
        is UiState.Error -> {
            AlertDialog(
                onDismissRequest = onDismissDetail,
                title = { Text("Coach detail") },
                text = { Text(detail.message ?: "Unable to load coach detail") },
                confirmButton = {
                    TextButton(onClick = onDismissDetail) {
                        Text("Close")
                    }
                }
            )
        }
        is UiState.Success -> {
            CoachDetailDialog(detail = detail.data, onDismiss = onDismissDetail)
        }
        else -> Unit
    }
}

@Composable
private fun AdminCoachCard(
    coach: AdminCoachSummary,
    onViewDetail: () -> Unit,
    onToggleStatus: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = coach.name ?: "Coach", style = MaterialTheme.typography.titleMedium)
            coach.schoolName?.let { Text(text = "School: $it") }
            coach.status?.let { Text(text = "Status: $it") }
            coach.phone?.let { Text(text = "Phone: $it") }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onViewDetail) {
                    Text(text = "Details")
                }
                Button(onClick = onToggleStatus) {
                    val label = if (coach.active == true || coach.status.equals("ACTIVE", ignoreCase = true)) "Deactivate" else "Activate"
                    Text(text = label)
                }
            }
        }
    }
}
