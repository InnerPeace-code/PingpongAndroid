package com.pingpong.app.feature.superadmin

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pingpong.app.core.common.UiState
import com.pingpong.app.core.model.admin.AdminCoachSummary
import com.pingpong.app.core.model.coach.CoachApplication
import kotlinx.coroutines.delay

@Composable
fun SuperAdminCoachesRoute(
    viewModel: SuperAdminCoachesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var levelDialogCoachId by remember { mutableStateOf<Long?>(null) }
    var levelInput by rememberSaveable { mutableStateOf("1") }
    var levelError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state.message) {
        if (state.message != null) {
            delay(2500)
            viewModel.clearMessage()
        }
    }

    if (levelDialogCoachId != null) {
        AlertDialog(
            onDismissRequest = {
                levelDialogCoachId = null
                levelError = null
            },
            title = { Text(text = "Approve coach") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = levelInput,
                        onValueChange = { levelInput = it },
                        label = { Text("Level") },
                        singleLine = true
                    )
                    levelError?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val parsed = levelInput.toIntOrNull()
                    if (parsed == null || parsed <= 0) {
                        levelError = "Enter positive integer"
                    } else {
                        viewModel.certify(levelDialogCoachId!!, true, parsed)
                        levelDialogCoachId = null
                        levelError = null
                    }
                }) {
                    Text(text = "Approve")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    levelDialogCoachId = null
                    levelError = null
                }) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    SuperAdminCoachesScreen(
        state = state,
        onRefresh = viewModel::refresh,
        onApprove = { coachId ->
            levelDialogCoachId = coachId
            levelInput = "1"
        },
        onReject = { coachId -> viewModel.certify(coachId, false, null) },
        onViewDetail = viewModel::viewCoachDetail,
        onDismissDetail = viewModel::dismissDetail
    )
}

@Composable
private fun SuperAdminCoachesScreen(
    state: SuperAdminCoachesUiState,
    onRefresh: () -> Unit,
    onApprove: (Long) -> Unit,
    onReject: (Long) -> Unit,
    onViewDetail: (Long) -> Unit,
    onDismissDetail: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Pending coaches", style = MaterialTheme.typography.titleLarge)
                state.message?.let { Text(text = it, color = MaterialTheme.colorScheme.secondary) }
                state.error?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
            }
            OutlinedButton(onClick = onRefresh, enabled = !state.isLoading) {
                Text(text = "Refresh")
            }
        }

        if (state.isLoading) {
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
        } else {
            PendingCoachList(pending = state.pending, onApprove = onApprove, onReject = onReject, onViewDetail = onViewDetail)
        }

        Text(text = "Certified coaches", style = MaterialTheme.typography.titleLarge)
        CertifiedCoachList(coaches = state.certified, onViewDetail = onViewDetail)
    }

    when (val detail = state.detailState) {
        UiState.Loading -> {
            AlertDialog(
                onDismissRequest = onDismissDetail,
                title = { Text("Coach detail") },
                text = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
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
            com.pingpong.app.feature.admin.CoachDetailDialog(detail = detail.data, onDismiss = onDismissDetail)
        }
        else -> Unit
    }
}

@Composable
private fun PendingCoachList(
    pending: List<CoachApplication>,
    onApprove: (Long) -> Unit,
    onReject: (Long) -> Unit,
    onViewDetail: (Long) -> Unit
) {
    if (pending.isEmpty()) {
        Text(text = "No pending coaches")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(pending, key = { it.relationId }) { coach ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = coach.coachName ?: coach.studentName ?: "Coach", style = MaterialTheme.typography.titleMedium)
                        if (!coach.appliedAt.isNullOrBlank()) {
                            Text(text = "Applied at: ${coach.appliedAt}")
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(onClick = { coach.coachId?.let(onApprove) }) {
                                Text(text = "Approve")
                            }
                            OutlinedButton(onClick = { coach.coachId?.let(onReject) }) {
                                Text(text = "Reject")
                            }
                            OutlinedButton(onClick = { coach.coachId?.let(onViewDetail) }) {
                                Text(text = "Details")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CertifiedCoachList(
    coaches: List<AdminCoachSummary>,
    onViewDetail: (Long) -> Unit
) {
    if (coaches.isEmpty()) {
        Text(text = "No certified coaches")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(coaches, key = { it.id }) { coach ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = coach.name ?: "Coach", style = MaterialTheme.typography.titleMedium)
                        coach.schoolName?.let { Text(text = "School: $it") }
                        coach.status?.let { Text(text = "Status: $it") }
                        OutlinedButton(onClick = { onViewDetail(coach.id) }) {
                            Text(text = "Details")
                        }
                    }
                }
            }
        }
    }
}
