package com.pingpong.app.feature.superadmin

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
fun SuperAdminManagementRoute() {
    var currentSection by rememberSaveable { mutableStateOf(SuperAdminSection.ADMINS) }

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = currentSection.ordinal,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            SuperAdminSection.entries.forEachIndexed { index, section ->
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
                SuperAdminSection.ADMINS -> SuperAdminAdminsRoute()
                SuperAdminSection.SCHOOLS -> SuperAdminSchoolsRoute()
                SuperAdminSection.COACHES -> SuperAdminCoachesRoute()
                SuperAdminSection.STUDENTS -> SuperAdminStudentsRoute()
            }
        }
    }
}

enum class SuperAdminSection(val title: String) {
    ADMINS("Admins"),
    SCHOOLS("Schools"),
    COACHES("Coaches"),
    STUDENTS("Students")
}
