package com.pingpong.app.feature.schedule

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
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pingpong.app.core.common.UiState
import com.pingpong.app.core.model.admin.SchoolSummary
import com.pingpong.app.core.model.student.TimeSlot

@Composable
fun ScheduleRoute(
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Schedule", style = MaterialTheme.typography.titleLarge)
        state.error?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }

        if (state.isLoading) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Loading schools...")
            }
        } else {
            SchoolSelector(
                schools = state.schools,
                selectedId = state.selectedSchoolId,
                onSelect = viewModel::selectSchool
            )
            when (val schedule = state.scheduleState) {
                UiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Loading schedule...")
                    }
                }
                is UiState.Error -> {
                    Text(text = schedule.message ?: "Unable to load schedule", color = MaterialTheme.colorScheme.error)
                }
                is UiState.Success -> {
                    ScheduleList(slots = schedule.data)
                }
                else -> {
                    Text(text = "Select a school to view schedule")
                }
            }
        }
    }
}

@Composable
private fun SchoolSelector(
    schools: List<SchoolSummary>,
    selectedId: Long?,
    onSelect: (Long) -> Unit
) {
    if (schools.isEmpty()) {
        Text(text = "No schools available")
        return
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Schools", style = MaterialTheme.typography.titleMedium)
        schools.forEach { school ->
            OutlinedButton(
                onClick = { onSelect(school.id) },
                enabled = selectedId != school.id,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = school.name ?: "School ${school.id}")
            }
        }
    }
}

@Composable
private fun ScheduleList(slots: List<TimeSlot>) {
    if (slots.isEmpty()) {
        Text(text = "No schedule entries")
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(slots) { slot ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    val day = slot.dayOfWeek?.let { dayOfWeekText(it) } ?: "Day"
                    Text(text = day, style = MaterialTheme.typography.titleMedium)
                    Text(text = "${slot.startTime ?: "--"} - ${slot.endTime ?: "--"}")
                }
            }
        }
    }
}

private fun dayOfWeekText(day: Int): String = when (day) {
    1 -> "Monday"
    2 -> "Tuesday"
    3 -> "Wednesday"
    4 -> "Thursday"
    5 -> "Friday"
    6 -> "Saturday"
    7 -> "Sunday"
    else -> "Day $day"
}
