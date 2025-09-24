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
import kotlinx.coroutines.delay
import com.pingpong.app.core.common.UiState
import com.pingpong.app.core.model.coach.CoachApplication
import com.pingpong.app.core.model.coach.CoachStudentDetail

@Composable
fun CoachApplicationsRoute(
    viewModel: CoachApplicationsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.message) {
        if (state.message != null) {
            kotlinx.coroutines.delay(2500)
            viewModel.clearMessage()
        }
    }

    CoachApplicationsScreen(
        state = state,
        onRefresh = viewModel::refresh,
        onApprove = { viewModel.handleApplication(it, true) },
        onReject = { viewModel.handleApplication(it, false) },
        onViewStudent = viewModel::viewStudentDetail,
        onDismissDetail = viewModel::dismissDetail
    )
}

@Composable
private fun CoachApplicationsScreen(
    state: CoachApplicationsUiState,
    onRefresh: () -> Unit,
    onApprove: (Long) -> Unit,
    onReject: (Long) -> Unit,
    onViewStudent: (Long?) -> Unit,
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "Student Applications", style = MaterialTheme.typography.titleLarge)
                state.message?.let {
                    Text(text = it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                }
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
                    Text(text = "Loading applications...")
                }
            }
            state.error != null -> {
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error
                )
            }
            state.applications.isEmpty() -> {
                Text(text = "No pending applications", style = MaterialTheme.typography.bodyMedium)
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.applications, key = { it.relationId }) { application ->
                        CoachApplicationCard(
                            application = application,
                            onApprove = { onApprove(application.relationId) },
                            onReject = { onReject(application.relationId) },
                            onView = { onViewStudent(application.studentId) }
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
                title = { Text("Student Detail") },
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
                title = { Text("Student Detail") },
                text = { Text(detail.message ?: "Unable to load detail") },
                confirmButton = {
                    TextButton(onClick = onDismissDetail) {
                        Text("Close")
                    }
                }
            )
        }
        is UiState.Success -> {
            CoachStudentDetailDialog(detail = detail.data, onDismiss = onDismissDetail)
        }
        else -> Unit
    }
}

@Composable
private fun CoachApplicationCard(
    application: CoachApplication,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onView: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = application.studentName ?: "Unknown student", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                application.studentAge?.let {
                    Text(text = "Age: $it", style = MaterialTheme.typography.bodyMedium)
                }
                application.studentMale?.let {
                    Text(text = if (it) "Male" else "Female", style = MaterialTheme.typography.bodyMedium)
                }
            }
            application.appliedAt?.let {
                Text(text = "Requested at $it", style = MaterialTheme.typography.bodySmall)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onApprove) {
                    Text(text = "Approve")
                }
                OutlinedButton(onClick = onReject) {
                    Text(text = "Reject")
                }
                OutlinedButton(onClick = onView) {
                    Text(text = "Details")
                }
            }
        }
    }
}

@Composable
private fun CoachStudentDetailDialog(
    detail: CoachStudentDetail,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = detail.name ?: detail.username ?: "Student Detail") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                detail.username?.let { Text(text = "Username: $it") }
                detail.phone?.let { Text(text = "Phone: $it") }
                detail.email?.let { Text(text = "Email: $it") }
                detail.age?.let { Text(text = "Age: $it") }
                detail.male?.let { Text(text = if (it) "Gender: Male" else "Gender: Female") }
                detail.schoolName?.let { Text(text = "School: $it") }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Close")
            }
        }
    )
}
