package com.pingpong.app.feature.evaluation

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
import com.pingpong.app.core.model.evaluation.EvaluationItem
import kotlinx.coroutines.delay

@Composable
fun EvaluationRoute(
    viewModel: EvaluationViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }
    var appointmentId by rememberSaveable { mutableStateOf("") }
    var content by rememberSaveable { mutableStateOf("") }
    var formError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state.message) {
        if (state.message != null) {
            delay(2500)
            viewModel.clearMessage()
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                formError = null
            },
            title = { Text(text = "Submit evaluation") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = appointmentId,
                        onValueChange = { appointmentId = it },
                        label = { Text("Appointment ID") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Content") }
                    )
                    formError?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val id = appointmentId.toLongOrNull()
                    if (id == null) {
                        formError = "Enter a valid appointment id"
                    } else if (content.isBlank()) {
                        formError = "Content required"
                    } else {
                        formError = null
                        viewModel.submitEvaluation(id, content.trim())
                        showDialog = false
                        appointmentId = ""
                        content = ""
                    }
                }) {
                    Text(text = "Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    formError = null
                }) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    EvaluationScreen(
        state = state,
        onRefresh = viewModel::refresh,
        onDelete = viewModel::deleteEvaluation,
        onAdd = { showDialog = true }
    )
}

@Composable
private fun EvaluationScreen(
    state: EvaluationUiState,
    onRefresh: () -> Unit,
    onDelete: (Long) -> Unit,
    onAdd: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Evaluations", style = MaterialTheme.typography.titleLarge)
                state.message?.let { Text(text = it, color = MaterialTheme.colorScheme.secondary) }
                state.error?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onRefresh, enabled = !state.isLoading) {
                    Text(text = "Refresh")
                }
                Button(onClick = onAdd) {
                    Text(text = "Add")
                }
            }
        }

        when {
            state.isLoading -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Loading evaluations...")
                }
            }
            state.evaluations.isEmpty() -> {
                Text(text = "No evaluations available")
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.evaluations, key = { it.id }) { evaluation ->
                        EvaluationCard(evaluation = evaluation, onDelete = { onDelete(evaluation.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun EvaluationCard(
    evaluation: EvaluationItem,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = evaluation.content ?: "(no content)", style = MaterialTheme.typography.bodyLarge)
            evaluation.createdAt?.let { Text(text = "Created: $it", style = MaterialTheme.typography.bodySmall) }
            evaluation.updatedAt?.let { Text(text = "Updated: $it", style = MaterialTheme.typography.bodySmall) }
            evaluation.evaluatorType?.let { Text(text = "By: $it", style = MaterialTheme.typography.labelMedium) }
            OutlinedButton(onClick = onDelete) {
                Text(text = "Delete")
            }
        }
    }
}
