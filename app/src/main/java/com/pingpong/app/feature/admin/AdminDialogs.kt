package com.pingpong.app.feature.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import com.pingpong.app.core.model.student.CoachDetail

@Composable
fun CoachDetailDialog(
    detail: CoachDetail,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = detail.name ?: "Coach detail") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                detail.level?.let { Text(text = "Level: $it") }
                detail.phone?.let { Text(text = "Phone: $it") }
                detail.email?.let { Text(text = "Email: $it") }
                detail.schoolName?.let { Text(text = "School: $it") }
                detail.description?.let { Text(text = it) }
                detail.achievements?.let { Text(text = it) }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Close")
            }
        }
    )
}
