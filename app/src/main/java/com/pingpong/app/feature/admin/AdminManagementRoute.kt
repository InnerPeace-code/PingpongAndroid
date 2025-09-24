package com.pingpong.app.feature.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AdminManagementRoute() {
    var currentSection by rememberSaveable { mutableStateOf(AdminSection.COACH_AUDIT) }

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = currentSection.ordinal,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            AdminSection.entries.forEachIndexed { index, section ->
                Tab(
                    selected = index == currentSection.ordinal,
                    onClick = { currentSection = section },
                    text = { Text(section.title) }
                )
            }
        }
        Divider(modifier = Modifier.fillMaxWidth())
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            when (currentSection) {
                AdminSection.COACH_AUDIT -> AdminCoachAuditRoute()
                AdminSection.COACH_MANAGE -> AdminCoachManageRoute()
                AdminSection.STUDENT_MANAGE -> AdminStudentManageRoute()
                AdminSection.CHANGE_REQUESTS -> AdminChangeManageRoute()
                AdminSection.NOTIFICATIONS -> AdminNotificationRoute()
            }
        }
    }
}

enum class AdminSection(val title: String) {
    COACH_AUDIT("Coach audit"),
    COACH_MANAGE("Coach manage"),
    STUDENT_MANAGE("Student manage"),
    CHANGE_REQUESTS("Change requests"),
    NOTIFICATIONS("Notifications")
}
