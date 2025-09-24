package com.pingpong.app.feature.student

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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pingpong.app.core.common.UiState
import com.pingpong.app.core.model.student.CoachDetail
import com.pingpong.app.core.model.student.CoachSummary
import com.pingpong.app.core.model.student.StudentCoachFilter

@Composable
fun StudentCoachBrowserRoute(
    viewModel: StudentCoachBrowserViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    StudentCoachBrowserScreen(
        uiState = uiState,
        onNameChange = viewModel::updateName,
        onGenderChange = viewModel::updateGender,
        onLevelChange = viewModel::updateLevel,
        onAgeRangeChange = viewModel::updateAgeRange,
        onRefresh = viewModel::refresh,
        onClearError = viewModel::clearError,
        onViewDetail = viewModel::loadCoachDetail,
        onDismissDetail = viewModel::dismissCoachDetail,
        onApplyCoach = viewModel::selectCoach,
        onMessageConsumed = viewModel::clearMessage
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudentCoachBrowserScreen(
    uiState: CoachBrowserUiState,
    onNameChange: (String) -> Unit,
    onGenderChange: (Boolean?) -> Unit,
    onLevelChange: (Int?) -> Unit,
    onAgeRangeChange: (Int?, Int?) -> Unit,
    onRefresh: () -> Unit,
    onClearError: () -> Unit,
    onViewDetail: (Long) -> Unit,
    onDismissDetail: () -> Unit,
    onApplyCoach: (Long) -> Unit,
    onMessageConsumed: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.message) {
        val message = uiState.message
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            onMessageConsumed()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Browse Coaches", style = MaterialTheme.typography.headlineSmall)
            FilterSection(
                filter = uiState.filter,
                onNameChange = onNameChange,
                onGenderChange = onGenderChange,
                onLevelChange = onLevelChange,
                onAgeRangeChange = onAgeRangeChange,
                onRefresh = onRefresh
            )

            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            uiState.error?.let { error ->
                ErrorCard(message = error, onDismiss = onClearError)
            }

            if (uiState.coaches.isEmpty() && !uiState.isLoading && uiState.error == null) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.coaches, key = { it.id }) { coach ->
                        CoachCard(
                            coach = coach,
                            onViewDetail = { onViewDetail(coach.id) },
                            onApply = { onApplyCoach(coach.id) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(32.dp)) }
                }
            }
        }
    }

    when (val detailState = uiState.detailState) {
        UiState.Loading -> {
            AlertDialog(
                onDismissRequest = onDismissDetail,
                title = { Text("Loading coach info") },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CircularProgressIndicator()
                        Text("Fetching details...")
                    }
                },
                confirmButton = {
                    TextButton(onClick = onDismissDetail) {
                        Text("Close")
                    }
                }
            )
        }
        is UiState.Success -> {
            CoachDetailDialog(detailState.data, onDismissDetail, onApplyCoach)
        }
        is UiState.Error -> {
            AlertDialog(
                onDismissRequest = onDismissDetail,
                title = { Text("Unable to load details") },
                text = { Text(detailState.message ?: "Unexpected error") },
                confirmButton = {
                    TextButton(onClick = onDismissDetail) { Text("Close") }
                }
            )
        }
        else -> Unit
    }
}

@Composable
private fun FilterSection(
    filter: StudentCoachFilter,
    onNameChange: (String) -> Unit,
    onGenderChange: (Boolean?) -> Unit,
    onLevelChange: (Int?) -> Unit,
    onAgeRangeChange: (Int?, Int?) -> Unit,
    onRefresh: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = filter.name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Name contains") },
            singleLine = true
        )

        GenderFilterRow(selected = filter.isMale, onGenderChange = onGenderChange)

        LevelFilterRow(selected = filter.level, onLevelChange = onLevelChange)

        AgeFilterRow(filter.ageLow, filter.ageHigh, onAgeRangeChange)

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onRefresh) {
                Text("Search")
            }
        }
    }
}

@Composable
private fun GenderFilterRow(selected: Boolean?, onGenderChange: (Boolean?) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            onClick = { onGenderChange(null) },
            label = { Text("All") },
            leadingIcon = null,
            selected = selected == null
        )
        FilterChip(
            onClick = { onGenderChange(true) },
            label = { Text("Male") },
            selected = selected == true
        )
        FilterChip(
            onClick = { onGenderChange(false) },
            label = { Text("Female") },
            selected = selected == false
        )
    }
}

@Composable
private fun LevelFilterRow(selected: Int?, onLevelChange: (Int?) -> Unit) {
    val options = listOf(
        null to "All levels",
        10 to "Beginner",
        100 to "Intermediate",
        1000 to "Advanced"
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { (value, label) ->
            FilterChip(
                onClick = { onLevelChange(value) },
                label = { Text(label) },
                selected = selected == value
            )
        }
    }
}

@Composable
private fun AgeFilterRow(low: Int?, high: Int?, onAgeRangeChange: (Int?, Int?) -> Unit) {
    var minAge by remember(low) { mutableStateOf(low?.toString() ?: "") }
    var maxAge by remember(high) { mutableStateOf(high?.toString() ?: "") }

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = minAge,
            onValueChange = {
                minAge = it
                onAgeRangeChange(it.toIntOrNull(), maxAge.toIntOrNull())
            },
            label = { Text("Age from") },
            singleLine = true,
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            value = maxAge,
            onValueChange = {
                maxAge = it
                onAgeRangeChange(minAge.toIntOrNull(), it.toIntOrNull())
            },
            label = { Text("to") },
            singleLine = true,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun CoachCard(
    coach: CoachSummary,
    onViewDetail: () -> Unit,
    onApply: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = coach.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                coach.schoolName?.let { Text(text = it, style = MaterialTheme.typography.bodyMedium) }
                coach.level?.let { Text(text = "Level $it", style = MaterialTheme.typography.bodyMedium) }
            }
            coach.description?.let { Text(text = it, style = MaterialTheme.typography.bodySmall) }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TextButton(onClick = onViewDetail) { Text("Details") }
                Button(onClick = onApply) { Text("Apply") }
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "No coaches match the current filters.", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Try adjusting your search criteria.", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun ErrorCard(message: String, onDismiss: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = message, color = MaterialTheme.colorScheme.error)
            TextButton(onClick = onDismiss) { Text("Dismiss") }
        }
    }
}

@Composable
private fun CoachDetailDialog(
    detail: CoachDetail,
    onDismiss: () -> Unit,
    onApplyCoach: (Long) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = detail.name) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                detail.schoolName?.let { Text("School: $it") }
                detail.level?.let { Text("Level: $it") }
                detail.description?.let { Text("Profile: $it") }
                detail.achievements?.let { Text("Achievements: $it") }
                detail.phone?.let { Text("Phone: $it") }
                detail.email?.let { Text("Email: $it") }
            }
        },
        confirmButton = {
            Button(onClick = {
                onApplyCoach(detail.id)
                onDismiss()
            }) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

