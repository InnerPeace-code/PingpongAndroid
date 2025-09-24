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
import com.pingpong.app.core.model.admin.AdminUserSummary
import com.pingpong.app.core.model.admin.SchoolSummary
import kotlinx.coroutines.delay

@Composable
fun SuperAdminAdminsRoute(
    viewModel: SuperAdminAdminsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var creating by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<AdminUserSummary?>(null) }

    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var schoolId by rememberSaveable { mutableStateOf("") }

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
            title = { Text(text = if (isEdit) "Edit admin" else "Create admin") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (!isEdit) {
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Username") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            singleLine = true
                        )
                    }
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = schoolId,
                        onValueChange = { schoolId = it },
                        label = { Text("School ID (optional)") },
                        singleLine = true
                    )
                    if (state.schools.isNotEmpty()) {
                        Text(text = "Schools:", style = MaterialTheme.typography.bodyMedium)
                        state.schools.take(5).forEach { school ->
                            Text(text = "${school.id}: ${school.name}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val school = schoolId.toLongOrNull()
                    if (isEdit) {
                        viewModel.updateAdmin(
                            id = editing!!.id,
                            name = name,
                            phone = phone,
                            email = email,
                            schoolId = school
                        )
                    } else {
                        viewModel.createAdmin(
                            username = username,
                            password = password,
                            name = name,
                            phone = phone,
                            email = email,
                            schoolId = school
                        )
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

    SuperAdminAdminsScreen(
        state = state,
        onRefresh = viewModel::refresh,
        onCreate = {
            creating = true
            editing = null
            username = ""
            password = ""
            name = ""
            phone = ""
            email = ""
            schoolId = ""
        },
        onEdit = { admin ->
            editing = admin
            creating = false
            username = admin.username.orEmpty()
            password = ""
            name = admin.name.orEmpty()
            phone = admin.phone.orEmpty()
            email = admin.email.orEmpty()
            schoolId = admin.schoolId?.toString() ?: ""
        },
        onDelete = viewModel::deleteAdmin
    )
}

@Composable
private fun SuperAdminAdminsScreen(
    state: SuperAdminAdminsUiState,
    onRefresh: () -> Unit,
    onCreate: () -> Unit,
    onEdit: (AdminUserSummary) -> Unit,
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
                Text(text = "Campus admins", style = MaterialTheme.typography.titleLarge)
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
                    Text(text = "Loading admins...")
                }
            }
            state.admins.isEmpty() -> {
                Text(text = "No admins configured")
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.admins, key = { it.id }) { admin ->
                        AdminAccountCard(admin = admin, onEdit = { onEdit(admin) }, onDelete = { onDelete(admin.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminAccountCard(
    admin: AdminUserSummary,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = admin.username ?: "Admin", style = MaterialTheme.typography.titleMedium)
            admin.name?.let { Text(text = "Name: $it") }
            admin.phone?.let { Text(text = "Phone: $it") }
            admin.email?.let { Text(text = "Email: $it") }
            admin.schoolName?.let { Text(text = "School: $it") }
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
