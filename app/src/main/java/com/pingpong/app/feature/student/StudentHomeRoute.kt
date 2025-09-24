package com.pingpong.app.feature.student

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
fun StudentHomeRoute() {
    var currentSection by rememberSaveable { mutableStateOf(StudentSection.COACH_BROWSER) }

    StudentHomeScreen(
        currentSection = currentSection,
        onSectionSelected = { currentSection = it }
    )
}

@Composable
private fun StudentHomeScreen(
    currentSection: StudentSection,
    onSectionSelected: (StudentSection) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = currentSection.ordinal,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            StudentSection.entries.forEachIndexed { index, section ->
                Tab(
                    selected = index == currentSection.ordinal,
                    onClick = { onSectionSelected(section) },
                    text = { Text(section.title) }
                )
            }
        }
        Divider(modifier = Modifier.fillMaxWidth())
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            when (currentSection) {
                StudentSection.COACH_BROWSER -> StudentCoachBrowserRoute()
                StudentSection.MY_COACHES -> StudentMyCoachesRoute()
                StudentSection.BOOKING -> StudentBookingRoute()
                StudentSection.RECHARGE -> StudentRechargeRoute()
                StudentSection.NOTIFICATIONS -> StudentNotificationRoute()
                StudentSection.COACH_CHANGE -> StudentCoachChangeRoute()
                StudentSection.EVALUATIONS -> EvaluationRoute()
            }
        }
    }
}

enum class StudentSection(val title: String) {
    COACH_BROWSER("Coach Finder"),
    MY_COACHES("My Coaches"),
    BOOKING("Course Booking"),
    RECHARGE("Account"),
    NOTIFICATIONS("Notifications"),
    COACH_CHANGE("Coach Change"),
    EVALUATIONS("Course Reviews")
}
