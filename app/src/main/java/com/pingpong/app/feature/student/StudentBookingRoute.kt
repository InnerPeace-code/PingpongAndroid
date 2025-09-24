package com.pingpong.app.feature.student

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.pingpong.app.core.model.student.CoachScheduleItem
import com.pingpong.app.core.model.student.CoachSummary
import com.pingpong.app.core.model.student.PendingCancelRequest
import com.pingpong.app.core.model.student.StudentAppointmentItem
import com.pingpong.app.core.model.student.TimeSlot
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Composable
fun StudentBookingRoute(
    viewModel: StudentBookingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    StudentBookingScreen(
        state = uiState,
        onCoachSelected = viewModel::selectCoach,
        onRefreshAll = viewModel::refreshAll,
        onOpenBooking = viewModel::openBookingDialog,
        onDismissBooking = viewModel::dismissBookingDialog,
        onToggleAutoAssign = viewModel::toggleAutoAssign,
        onTableSelected = viewModel::updateTableId,
        onSubmitBooking = viewModel::submitBooking,
        onRequestCancel = viewModel::requestCancel,
        onRespondCancel = viewModel::respondCoachCancel,
        onMessageConsumed = viewModel::clearMessage
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudentBookingScreen(
    state: StudentBookingUiState,
    onCoachSelected: (Long?) -> Unit,
    onRefreshAll: () -> Unit,
    onOpenBooking: (LocalDate, String, String) -> Unit,
    onDismissBooking: () -> Unit,
    onToggleAutoAssign: (Boolean) -> Unit,
    onTableSelected: (Long?) -> Unit,
    onSubmitBooking: () -> Unit,
    onRequestCancel: (Long) -> Unit,
    onRespondCancel: (Long, Boolean) -> Unit,
    onMessageConsumed: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            onMessageConsumed()
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Course Booking", style = MaterialTheme.typography.headlineSmall)
                TextButton(onClick = onRefreshAll) { Text("Refresh") }
            }

            state.globalError?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            CoachSelectorSection(state.coachListState, state.selectedCoachId, onCoachSelected)

            Divider()

            TimeSlotSection(
                slots = state.timeSlots,
                scheduleState = state.scheduleState,
                onOpenBooking = onOpenBooking
            )

            BookingSummary(state = state)

            ScheduleListSection(state.scheduleState)

            PendingCancelRequestsSection(state.pendingCancelRequests, onRespondCancel)

            MyAppointmentsSection(state.myAppointmentsState, onRequestCancel)
        }
    }

    if (state.bookingForm.visible) {
        BookingDialog(
            formState = state.bookingForm,
            bookingInProgress = state.bookingInProgress,
            onDismiss = onDismissBooking,
            onToggleAutoAssign = onToggleAutoAssign,
            onTableSelected = onTableSelected,
            onSubmit = onSubmitBooking
        )
    }
}

@Composable
private fun CoachSelectorSection(
    coachListState: UiState<List<CoachSummary>>,
    selectedCoachId: Long?,
    onCoachSelected: (Long?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Preferred coach", style = MaterialTheme.typography.titleMedium)
        when (coachListState) {
            UiState.Loading -> LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            is UiState.Error -> Text(coachListState.message ?: "Unable to load coaches", color = MaterialTheme.colorScheme.error)
            is UiState.Success -> {
                val coaches = coachListState.data
                if (coaches.isEmpty()) {
                    Text("You haven't connected with a coach yet.")
                } else {
                    var expanded by remember { mutableStateOf(false) }
                    val selectedCoach = coaches.firstOrNull { it.id == selectedCoachId }
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            TextButton(onClick = { expanded = true }) {
                                Text(selectedCoach?.name ?: "Select coach")
                            }
                            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                coaches.forEach { coach ->
                                    DropdownMenuItem(
                                        text = { Text(coach.name) },
                                        onClick = {
                                            expanded = false
                                            onCoachSelected(coach.id)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            else -> Unit
        }
    }
}

@Composable
private fun TimeSlotSection(
    slots: List<TimeSlot>,
    scheduleState: UiState<List<CoachScheduleItem>>,
    onOpenBooking: (LocalDate, String, String) -> Unit
) {
    if (slots.isEmpty()) {
        Text(text = "No campus time slots published yet.")
        return
    }
    val today = LocalDate.now()
    val upcomingDays = (0..6).map { today.plusDays(it.toLong()) }
    val scheduledAppointments = (scheduleState as? UiState.Success)?.data ?: emptyList()

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "Available time slots", style = MaterialTheme.typography.titleMedium)
        upcomingDays.forEach { date ->
            val matchingSlots = slots.filter { it.matches(date) }
            if (matchingSlots.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = date.format(DateTimeFormatter.ofPattern("MMM dd, E")), fontWeight = FontWeight.SemiBold)
                    matchingSlots.chunked(3).forEach { rowSlots ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            rowSlots.forEach { slot ->
                                val conflict = scheduledAppointments.hasConflict(date, slot.startTime, slot.endTime)
                                val label = slot.formatLabel()
                                OutlinedButton(
                                    enabled = !conflict,
                                    onClick = {
                                        onOpenBooking(date, slot.startTime ?: return@OutlinedButton, slot.endTime ?: return@OutlinedButton)
                                    }
                                ) {
                                    Text(text = if (conflict) "$label (booked)" else label)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookingSummary(state: StudentBookingUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Remaining cancellations: ${state.remainingCancelCount}")
        state.scheduleMessage?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
    }
}

@Composable
private fun ScheduleListSection(scheduleState: UiState<List<CoachScheduleItem>>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Coach schedule", style = MaterialTheme.typography.titleMedium)
        when (scheduleState) {
            UiState.Loading -> LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            is UiState.Error -> Text(scheduleState.message ?: "Unable to load schedule", color = MaterialTheme.colorScheme.error)
            is UiState.Success -> {
                val items = scheduleState.data.sortedBy { it.startTime }
                if (items.isEmpty()) {
                    Text("No appointments yet.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(items) { item ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(item.startTime.formatDateTimeRange(item.endTime), fontWeight = FontWeight.SemiBold)
                                    item.status?.let { Text("Status: $it") }
                                    item.tableId?.let { Text("Table: $it") }
                                }
                            }
                        }
                    }
                }
            }
            else -> Unit
        }
    }
}

@Composable
private fun PendingCancelRequestsSection(
    requests: List<PendingCancelRequest>,
    onRespond: (Long, Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Coach cancellation requests", style = MaterialTheme.typography.titleMedium)
        if (requests.isEmpty()) {
            Text("No pending requests from coaches.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(requests, key = { it.id }) { request ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Coach: ${request.coachName ?: request.coachId}", fontWeight = FontWeight.SemiBold)
                            request.createTime?.let { Text("Requested at: $it") }
                            request.reason?.let { Text("Reason: $it") }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { onRespond(request.id, true) }) { Text("Approve") }
                                OutlinedButton(onClick = { onRespond(request.id, false) }) { Text("Decline") }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MyAppointmentsSection(
    appointmentState: UiState<List<StudentAppointmentItem>>,
    onRequestCancel: (Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "My appointments", style = MaterialTheme.typography.titleMedium)
        when (appointmentState) {
            UiState.Loading -> LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            is UiState.Error -> Text(appointmentState.message ?: "Failed to load appointments", color = MaterialTheme.colorScheme.error)
            is UiState.Success -> {
                val appointments = appointmentState.data.sortedBy { it.startTime }
                if (appointments.isEmpty()) {
                    Text("You have no bookings yet.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(appointments, key = { it.id ?: 0 }) { appointment ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(appointment.startTime.formatDateTimeRange(appointment.endTime), fontWeight = FontWeight.SemiBold)
                                    appointment.coachName?.let { Text("Coach: $it") }
                                    appointment.status?.let { Text("Status: $it") }
                                    appointment.tableId?.let { Text("Table: $it") }
                                    appointment.id?.let { id ->
                                        OutlinedButton(onClick = { onRequestCancel(id) }) {
                                            Text("Request cancellation")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else -> Unit
        }
    }
}

@Composable
private fun BookingDialog(
    formState: BookingFormState,
    bookingInProgress: Boolean,
    onDismiss: () -> Unit,
    onToggleAutoAssign: (Boolean) -> Unit,
    onTableSelected: (Long?) -> Unit,
    onSubmit: () -> Unit
) {
    val tableOptions = remember { listOf<Long>(1, 2, 3, 4) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm booking") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Date: ${formState.date}")
                Text("Time: ${formState.startTime} - ${formState.endTime}")
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Auto assign table")
                    Switch(
                        checked = formState.autoAssign,
                        onCheckedChange = onToggleAutoAssign,
                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                    )
                }
                if (!formState.autoAssign) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        tableOptions.chunked(3).forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                row.forEach { tableId ->
                                    val selected = formState.tableId == tableId
                                    OutlinedButton(
                                        onClick = { onTableSelected(tableId) },
                                        colors = if (selected) ButtonDefaults.outlinedButtonColors(
                                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                            contentColor = MaterialTheme.colorScheme.primary
                                        ) else ButtonDefaults.outlinedButtonColors()
                                    ) {
                                        Text("Table $tableId")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onSubmit, enabled = !bookingInProgress) {
                if (bookingInProgress) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CircularProgressIndicator(modifier = Modifier.padding(vertical = 4.dp), strokeWidth = 2.dp)
                        Text("Submit")
                    }
                } else {
                    Text("Submit")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

private fun TimeSlot.matches(date: LocalDate): Boolean {
    val slotDay = this.dayOfWeek ?: return false
    val normalized = when (slotDay) {
        in 1..7 -> slotDay
        0 -> DayOfWeek.SUNDAY.value
        else -> slotDay
    }
    return normalized == date.dayOfWeek.value
}

private fun TimeSlot.formatLabel(): String {
    val startShort = this.startTime?.take(5) ?: "--"
    val endShort = this.endTime?.take(5) ?: "--"
    return "$startShort-$endShort"
}

private fun List<CoachScheduleItem>.hasConflict(date: LocalDate, start: String?, end: String?): Boolean {
    val startTime = start?.parseLocalTime() ?: return true
    val endTime = end?.parseLocalTime()
    return any { item ->
        val startDateTime = item.startTime?.parseLocalDateTime()
        val endDateTime = item.endTime?.parseLocalDateTime()
        if (startDateTime == null || endDateTime == null) return@any false
        startDateTime.toLocalDate() == date && startDateTime.toLocalTime() == startTime && (endTime == null || endDateTime.toLocalTime() == endTime)
    }
}

private fun String?.formatDateTimeRange(end: String?): String {
    if (this == null) return "--"
    val startDateTime = this.parseLocalDateTime()
    val endDateTime = end?.parseLocalDateTime()
    return if (startDateTime != null) {
        val datePart = startDateTime.toLocalDate().format(DateTimeFormatter.ofPattern("MMM dd"))
        val startPart = startDateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
        val endPart = endDateTime?.toLocalTime()?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "--"
        "$datePart $startPart - $endPart"
    } else {
        this + (end?.let { " - $it" } ?: "")
    }
}

private fun String.parseLocalDateTime(): LocalDateTime? {
    return runCatching { LocalDateTime.parse(this) }.getOrNull()
        ?: runCatching { LocalDateTime.parse(this.replace(' ', 'T')) }.getOrNull()
}

private fun String.parseLocalTime(): LocalTime? {
    return runCatching { LocalTime.parse(this) }.getOrNull()
        ?: runCatching { LocalTime.parse(this.takeLast(8)) }.getOrNull()
}

