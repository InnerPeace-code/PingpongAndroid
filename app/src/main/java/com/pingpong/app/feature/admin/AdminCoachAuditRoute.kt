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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.pingpong.app.core.model.student.CoachApplication
import com.pingpong.app.core.model.student.CoachDetail
import kotlinx.coroutines.delay

@Composable
fun AdminCoachAuditRoute(
    viewModel: AdminCoachAuditViewModel = hiltViewModel()
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
                    Text(text = "Enter certification level")
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
                        levelError = null
                        viewModel.certify(levelDialogCoachId, true, parsed)
                        levelDialogCoachId = null
                    }
                }) {
                    Text("Approve")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    levelDialogCoachId = null
                    levelError = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    AdminCoachAuditScreen(
        state = state,
        onRefresh = viewModel::refresh,
        onViewDetail = viewModel::viewCoachDetail,
        onReject = { coachId -> viewModel.certify(coachId, false, null) },
        onApprove = { coachId ->
            levelDialogCoachId = coachId
            levelInput = "1"
        },
        onDismissDetail = viewModel::dismissDetail
    )
}

@Composable
private fun AdminCoachAuditScreen(
    state: AdminCoachAuditUiState,
    onRefresh: () -> Unit,
    onViewDetail: (Long?) -> Unit,
    onReject: (Long?) -> Unit,
    onApprove: (Long?) -> Unit,
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
                Text(text = "Coach Certifications", style = MaterialTheme.typography.titleLarge)
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
                    Text(text = "Loading coach applications...")
                }
            }
            state.applications.isEmpty() -> {
                Text(text = "No pending coach applications")
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.applications, key = { it.relationId }) { application ->
                        AdminCoachApplicationCard(
                            application = application,
                            onApprove = { onApprove(application.coachId) },
                            onReject = { onReject(application.coachId) },
                            onViewDetail = { onViewDetail(application.coachId) }
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
private fun AdminCoachApplicationCard(
    application: CoachApplication,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onViewDetail: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = application.studentName ?: "Coach", style = MaterialTheme.typography.titleMedium)
            application.appliedAt?.let { Text(text = "Applied at: $it") }
            application.status?.let { Text(text = "Status: $it") }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onApprove) {
                    Text(text = "Approve")
                }
                OutlinedButton(onClick = onReject) {
                    Text(text = "Reject")
                }
                OutlinedButton(onClick = onViewDetail) {
                    Text(text = "Details")
                }
            }
        }
    }
}


