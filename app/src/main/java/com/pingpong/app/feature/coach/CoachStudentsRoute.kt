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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pingpong.app.core.common.UiState
import com.pingpong.app.core.model.coach.CoachStudent
import com.pingpong.app.core.model.coach.CoachStudentDetail

@Composable
fun CoachStudentsRoute(
    viewModel: CoachStudentsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    CoachStudentsScreen(
        state = state,
        onRefresh = viewModel::refresh,
        onViewDetail = viewModel::viewStudentDetail,
        onDismissDetail = viewModel::dismissDetail
    )
}

@Composable
private fun CoachStudentsScreen(
    state: CoachStudentsUiState,
    onRefresh: () -> Unit,
    onViewDetail: (Long) -> Unit,
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
            Text(text = "My Students", style = MaterialTheme.typography.titleLarge)
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
                    Text(text = "Loading students...")
                }
            }
            state.error != null -> {
                Text(text = state.error, color = MaterialTheme.colorScheme.error)
            }
            state.students.isEmpty() -> {
                Text(text = "No related students")
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.students, key = { it.id }) { student ->
                        CoachStudentCard(student = student, onViewDetail = { onViewDetail(student.id) })
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
private fun CoachStudentCard(
    student: CoachStudent,
    onViewDetail: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = student.name ?: "Unknown student", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                student.age?.let { Text(text = "Age: $it") }
                student.male?.let { Text(text = if (it) "Male" else "Female") }
            }
            student.phone?.let { Text(text = "Phone: $it") }
            student.email?.let { Text(text = "Email: $it") }
            student.schoolName?.let { Text(text = "School: $it") }
            OutlinedButton(onClick = onViewDetail) {
                Text(text = "View detail")
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
