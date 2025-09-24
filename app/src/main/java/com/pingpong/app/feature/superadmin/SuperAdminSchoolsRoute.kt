package com.pingpong.app.feature.superadmin

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pingpong.app.core.model.admin.SchoolSummary
import kotlinx.coroutines.delay

@Composable
fun SuperAdminSchoolsRoute(
    viewModel: SuperAdminSchoolsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var creating by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<SchoolSummary?>(null) }
    var name by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(state.message) {
        if (state.message != null) {
            delay(2500)
            viewModel.clearMessage()
        }
    }

    if (creating || editing != null) {
        val isEdit = editing != null
        AlertDialog(
            onDismissRequest = {
                creating = false
                editing = null
            },
            title = { Text(text = if (isEdit) "Edit school" else "Create school") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Address") }
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (isEdit) {
                        viewModel.updateSchool(editing!!.id, name, address, description)
                    } else {
                        viewModel.createSchool(name, address, description)
                    }
                    creating = false
                    editing = null
                }) {
                    Text(text = if (isEdit) "Save" else "Create")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    creating = false
                    editing = null
                }) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    SuperAdminSchoolsScreen(
        state = state,
        onRefresh = viewModel::refresh,
        onCreate = {
            creating = true
            editing = null
            name = ""
            address = ""
            description = ""
        },
        onEdit = { school ->
            editing = school
            creating = false
            name = school.name.orEmpty()
            address = school.address.orEmpty()
            description = school.description.orEmpty()
        },
        onDelete = viewModel::deleteSchool
    )
}

@Composable
private fun SuperAdminSchoolsScreen(
    state: SuperAdminSchoolsUiState,
    onRefresh: () -> Unit,
    onCreate: () -> Unit,
    onEdit: (SchoolSummary) -> Unit,
    onDelete: (Long) -> Unit
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
                Text(text = "Schools", style = MaterialTheme.typography.titleLarge)
                state.message?.let { Text(text = it, color = MaterialTheme.colorScheme.secondary) }
                state.error?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onRefresh, enabled = !state.isLoading) {
                    Text(text = "Refresh")
                }
                Button(onClick = onCreate) {
                    Text(text = "Create")
                }
            }
        }

        when {
            state.isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Loading schools...")
                }
            }
            state.schools.isEmpty() -> {
                Text(text = "No schools configured")
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.schools, key = { it.id }) { school ->
                        SchoolCard(school = school, onEdit = { onEdit(school) }, onDelete = { onDelete(school.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun SchoolCard(
    school: SchoolSummary,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = school.name ?: "School", style = MaterialTheme.typography.titleMedium)
            school.address?.let { Text(text = "Address: $it") }
            school.description?.let { Text(text = it) }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onEdit) {
                    Text(text = "Edit")
                }
                Button(onClick = onDelete) {
                    Text(text = "Delete")
                }
            }
        }
    }
}
