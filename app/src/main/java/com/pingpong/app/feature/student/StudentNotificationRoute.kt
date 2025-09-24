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
import com.pingpong.app.core.model.student.NotificationItem

@Composable
fun StudentNotificationRoute(
    viewModel: StudentNotificationViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    StudentNotificationScreen(state = state, onRefresh = viewModel::refresh, onMarkAsRead = viewModel::markAsRead)
}

@Composable
private fun StudentNotificationScreen(
    state: UiState<List<NotificationItem>>,
    onRefresh: () -> Unit,
    onMarkAsRead: (Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "Notifications", style = MaterialTheme.typography.titleMedium)
        when (state) {
            UiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Text("Loading unread notifications¡­", modifier = Modifier.padding(top = 8.dp))
                }
            }
            is UiState.Error -> {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(state.message ?: "Unable to load notifications", color = MaterialTheme.colorScheme.error)
                    Button(onClick = onRefresh) { Text("Retry") }
                }
            }
            is UiState.Success -> {
                val notifications = state.data
                if (notifications.isEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("You're all caught up!")
                        TextButton(onClick = onRefresh) { Text("Refresh") }
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(notifications, key = { it.id }) { notification ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    notification.title?.let { Text(it, style = MaterialTheme.typography.titleMedium) }
                                    notification.content?.let { Text(it) }
                                    notification.createdAt?.let { Text("Received: $it", style = MaterialTheme.typography.bodySmall) }
                                    Button(onClick = { onMarkAsRead(notification.id) }) { Text("Mark as read") }
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
