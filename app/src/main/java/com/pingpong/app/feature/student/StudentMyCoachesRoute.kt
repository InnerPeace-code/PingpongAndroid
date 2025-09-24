package com.pingpong.app.feature.student

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.pingpong.app.core.model.student.CoachSummary

@Composable
fun StudentMyCoachesRoute(
    viewModel: StudentMyCoachesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    StudentMyCoachesScreen(state = state, onRefresh = viewModel::refresh)
}

@Composable
private fun StudentMyCoachesScreen(
    state: UiState<List<CoachSummary>>,
    onRefresh: () -> Unit
) {
    when (state) {
        UiState.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Text("Loading your coaches¡­", modifier = Modifier.padding(top = 8.dp))
            }
        }
        is UiState.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(state.message ?: "Unable to load coaches", color = MaterialTheme.colorScheme.error)
                Button(onClick = onRefresh) { Text("Retry") }
            }
        }
        is UiState.Success -> {
            val coaches = state.data
            if (coaches.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("You haven't connected with any coaches yet.")
                    TextButton(onClick = onRefresh) { Text("Refresh") }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(coaches, key = { it.id }) { coach ->
                        Card(modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp)) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(coach.name, style = MaterialTheme.typography.titleMedium)
                                coach.schoolName?.let { Text("School: $it", style = MaterialTheme.typography.bodyMedium) }
                                coach.description?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                            }
                        }
                    }
                }
            }
        }
        else -> Unit
    }
}
