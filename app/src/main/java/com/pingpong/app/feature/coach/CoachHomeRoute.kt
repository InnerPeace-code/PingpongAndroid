package com.pingpong.app.feature.coach

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pingpong.app.feature.evaluation.EvaluationRoute

@Composable
fun CoachHomeRoute() {
    var currentSection by rememberSaveable { mutableStateOf(CoachSection.APPLICATIONS) }

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = currentSection.ordinal,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            CoachSection.entries.forEachIndexed { index, section ->
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
                CoachSection.APPLICATIONS -> CoachApplicationsRoute()
                CoachSection.STUDENTS -> CoachStudentsRoute()
                CoachSection.APPOINTMENTS -> CoachAppointmentsRoute()
                CoachSection.CHANGE_REQUESTS -> CoachChangeRoute()
                CoachSection.ACCOUNT -> CoachAccountRoute()
                CoachSection.EVALUATIONS -> EvaluationRoute()
            }
        }
    }
}

enum class CoachSection(val title: String) {
    APPLICATIONS("Applications"),
    STUDENTS("My Students"),
    APPOINTMENTS("Appointments"),
    CHANGE_REQUESTS("Change Requests"),
    ACCOUNT("Account"),
    EVALUATIONS("Course Reviews")
}
