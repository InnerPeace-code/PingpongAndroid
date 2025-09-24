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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pingpong.app.core.model.student.PendingCancelRequest
import com.pingpong.app.core.model.student.StudentAppointmentItem
import kotlinx.coroutines.delay

@Composable
fun CoachAppointmentsRoute(
    viewModel: CoachAppointmentsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.message) {
        if (state.message != null) {
            delay(2500)
            viewModel.clearMessage()
        }
    }

    CoachAppointmentsScreen(
        state = state,
        onRefresh = viewModel::refresh,
        onConfirm = viewModel::confirmAppointment,
        onRequestCancel = viewModel::requestCancellation,
        onHandleCancel = viewModel::handleCancelRequest
    )
}

@Composable
private fun CoachAppointmentsScreen(
    state: CoachAppointmentsUiState,
    onRefresh: () -> Unit,
    onConfirm: (Long, Boolean) -> Unit,
    onRequestCancel: (Long) -> Unit,
    onHandleCancel: (Long, Boolean) -> Unit
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
                Text(text = "Appointments", style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "Remaining cancellations: ${state.remainingCancelCount}",
                    style = MaterialTheme.typography.bodyMedium
                )
                state.message?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.secondary)
                }
                state.error?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
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
                Text(text = "Loading appointments...")
            }
        } else {
            if (state.appointments.isEmpty()) {
                Text(text = "No appointments scheduled")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.appointments, key = { it.id ?: it.hashCode().toLong() }) { appointment ->
                        CoachAppointmentCard(
                            appointment = appointment,
                            onConfirm = { accept -> appointment.id?.let { onConfirm(it, accept) } },
                            onCancel = { appointment.id?.let(onRequestCancel) }
                        )
                    }
                }
            }
        }

        Divider()

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "Pending Cancel Requests", style = MaterialTheme.typography.titleMedium)
            state.cancelError?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
            if (state.pendingCancelRequests.isEmpty()) {
                Text(text = "No pending cancel requests", style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.pendingCancelRequests, key = { it.id }) { request ->
                        CoachCancelRequestCard(
                            request = request,
                            onHandle = { approve -> onHandleCancel(request.id, approve) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CoachAppointmentCard(
    appointment: StudentAppointmentItem,
    onConfirm: (Boolean) -> Unit,
    onCancel: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = appointment.studentName ?: "Student", style = MaterialTheme.typography.titleMedium)
            appointment.startTime?.let { start ->
                val end = appointment.endTime ?: ""
                Text(text = "Time: $start - $end", style = MaterialTheme.typography.bodyMedium)
            }
            appointment.tableId?.let { Text(text = "Table: $it") }
            appointment.status?.let { Text(text = "Status: ${it.replace('_', ' ')}") }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (appointment.status.equals("PENDING_CONFIRM", ignoreCase = true)) {
                    Button(onClick = { onConfirm(true) }) {
                        Text(text = "Confirm")
                    }
                    OutlinedButton(onClick = { onConfirm(false) }) {
                        Text(text = "Reject")
                    }
                }
                if (appointment.status.equals("CONFIRMED", ignoreCase = true)) {
                    OutlinedButton(onClick = onCancel) {
                        Text(text = "Request cancel")
                    }
                }
            }
        }
    }
}

@Composable
private fun CoachCancelRequestCard(
    request: PendingCancelRequest,
    onHandle: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = "Cancel request #${request.id}", style = MaterialTheme.typography.titleMedium)
            request.coachName?.let { Text(text = "Coach: $it") }
            request.reason?.let { Text(text = "Reason: $it") }
            request.createTime?.let { Text(text = "Submitted at: $it") }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { onHandle(true) }) {
                    Text(text = "Approve")
                }
                OutlinedButton(onClick = { onHandle(false) }) {
                    Text(text = "Reject")
                }
            }
        }
    }
}
