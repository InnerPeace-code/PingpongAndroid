package com.pingpong.app.feature.student

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pingpong.app.core.common.UiState
import com.pingpong.app.core.model.student.CoachChangeOption
import com.pingpong.app.core.model.student.CoachChangeRequest

@Composable
fun StudentCoachChangeRoute(
    viewModel: StudentCoachChangeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    StudentCoachChangeScreen(
        state = state,
        onRefresh = viewModel::refresh,
        onSelectCurrent = viewModel::updateCurrentSelection,
        onSelectTarget = viewModel::updateTargetSelection,
        onSubmit = viewModel::submitChangeRequest,
        onMessageConsumed = viewModel::clearMessage
    )
}

@Composable
private fun StudentCoachChangeScreen(
    state: StudentCoachChangeUiState,
    onRefresh: () -> Unit,
    onSelectCurrent: (CoachChangeOption?) -> Unit,
    onSelectTarget: (CoachChangeOption?) -> Unit,
    onSubmit: () -> Unit,
    onMessageConsumed: () -> Unit
) {
    LaunchedEffect(state.message) {
        if (state.message != null) {
            // In full app we would show snackbar; for now just auto-clear after recompose
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "Request coach change", style = MaterialTheme.typography.titleMedium)

        when (state.currentCoachState) {
            UiState.Loading -> CircularProgressIndicator()
            is UiState.Error -> {
                Text(state.currentCoachState.message ?: "Unable to load current coach", color = MaterialTheme.colorScheme.error)
                TextButton(onClick = onRefresh) { Text("Retry") }
            }
            is UiState.Success -> CurrentCoachSection(state.currentCoachState.data, state.selectedCurrent, onSelectCurrent)
            else -> Unit
        }

        when (state.schoolCoachState) {
            UiState.Loading -> CircularProgressIndicator()
            is UiState.Error -> Text(state.schoolCoachState.message ?: "Unable to load school coaches", color = MaterialTheme.colorScheme.error)
            is UiState.Success -> TargetCoachSection(state.schoolCoachState.data, state.selectedTarget, onSelectTarget)
            else -> Unit
        }

        Button(onClick = onSubmit, enabled = !state.isSubmitting) {
            if (state.isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp), strokeWidth = 2.dp)
            }
            Text("Submit request")
        }

        Text(text = "Submitted requests", style = MaterialTheme.typography.titleMedium)
        if (state.requests.isEmpty()) {
            Text("No change requests submitted yet")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.requests, key = { it.id }) { request ->
                    CoachChangeRequestCard(request)
                }
            }
        }
    }
}

@Composable
private fun CurrentCoachSection(
    currentCoaches: List<CoachChangeOption>,
    selected: CoachChangeOption?,
    onSelect: (CoachChangeOption?) -> Unit
) {
    if (currentCoaches.isEmpty()) {
        Text("No current coach on record")
        return
    }
    var expanded by remember { mutableStateOf(false) }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Current coach")
            TextButton(onClick = { expanded = true }) {
                Text(selected?.name ?: currentCoaches.firstOrNull()?.name.orEmpty())
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                currentCoaches.forEach { coach ->
                    DropdownMenuItem(
                        text = { Text(coach.name) },
                        onClick = {
                            expanded = false
                            onSelect(coach)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TargetCoachSection(
    options: List<CoachChangeOption>,
    selected: CoachChangeOption?,
    onSelect: (CoachChangeOption?) -> Unit
) {
    if (options.isEmpty()) {
        Text("No other coaches available in your school.")
        return
    }
    var expanded by remember { mutableStateOf(false) }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Target coach")
            OutlinedButton(onClick = { expanded = true }) {
                Text(selected?.name ?: "Choose coach")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { coach ->
                    DropdownMenuItem(
                        text = { Text(coach.name) },
                        onClick = {
                            expanded = false
                            onSelect(coach)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CoachChangeRequestCard(request: CoachChangeRequest) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Request #${request.id}", fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
            request.status?.let { Text("Status: $it") }
            request.createdAt?.let { Text("Created: $it") }
            request.updatedAt?.let { Text("Updated: $it") }
        }
    }
}


