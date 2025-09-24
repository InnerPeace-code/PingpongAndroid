package com.pingpong.app.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pingpong.app.core.common.UiState
import kotlinx.coroutines.delay

@Composable
fun ProfileRoute(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.message, state.error, state.validationError) {
        if (state.message != null || state.error != null || state.validationError != null) {
            delay(2500)
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(state.logoutState) {
        if (state.logoutState is UiState.Success) {
            onLogout()
            viewModel.onLogoutHandled()
        }
    }

    ProfileScreen(
        state = state,
        onBack = onBack,
        onSave = viewModel::saveProfile,
        onLogout = viewModel::logout,
        onRefresh = viewModel::refresh,
        onNameChange = viewModel::onNameChanged,
        onPhoneChange = viewModel::onPhoneChanged,
        onEmailChange = viewModel::onEmailChanged,
        onMaleChange = viewModel::onMaleChanged,
        onAgeChange = viewModel::onAgeChanged,
        onDescriptionChange = viewModel::onDescriptionChanged,
        onPhotoPathChange = viewModel::onPhotoPathChanged,
        onPasswordChange = viewModel::onPasswordChanged,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChanged
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileScreen(
    state: ProfileUiState,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onLogout: () -> Unit,
    onRefresh: () -> Unit,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onMaleChange: (Boolean) -> Unit,
    onAgeChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPhotoPathChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit
) {
    val form = state.form
    val scrollState = rememberScrollState()
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = onSave, enabled = !state.isSaving && !state.isLoading) {
                        if (state.isSaving) {
                            CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.height(18.dp))
                        } else {
                            Text(text = "Save")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (state.isSaving) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 640.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AssistChip(onClick = {}, label = { Text(text = form.role.ifBlank { "unknown" }) })

                    when {
                        state.isLoading -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CircularProgressIndicator()
                                Text(text = "Loading profile...")
                            }
                        }

                        else -> {
                            state.message?.let { Text(text = it, color = MaterialTheme.colorScheme.secondary) }
                            state.validationError?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
                            state.error?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }

                            OutlinedTextField(
                                value = form.username,
                                onValueChange = {},
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Username") },
                                enabled = false
                            )

                            OutlinedTextField(
                                value = form.name,
                                onValueChange = onNameChange,
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Name") }
                            )

                            OutlinedTextField(
                                value = form.phone,
                                onValueChange = onPhoneChange,
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Phone") }
                            )

                            OutlinedTextField(
                                value = form.email,
                                onValueChange = onEmailChange,
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Email") }
                            )

                            if (form.role != "campus_admin") {
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    FilterChip(
                                        selected = form.male == true,
                                        onClick = { onMaleChange(true) },
                                        label = { Text(text = "Male") }
                                    )
                                    FilterChip(
                                        selected = form.male == false,
                                        onClick = { onMaleChange(false) },
                                        label = { Text(text = "Female") }
                                    )
                                }

                                OutlinedTextField(
                                    value = form.age,
                                    onValueChange = onAgeChange,
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text("Age") },
                                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                                )
                            }

                            if (form.role == "student" || form.role == "coach") {
                                OutlinedTextField(
                                    value = form.schoolId,
                                    onValueChange = {},
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text("School ID") },
                                    enabled = false
                                )
                            }

                            if (form.role == "coach") {
                                OutlinedTextField(
                                    value = form.photoPath,
                                    onValueChange = onPhotoPathChange,
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text("Photo path") }
                                )
                                OutlinedTextField(
                                    value = form.description,
                                    onValueChange = onDescriptionChange,
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text("Description") },
                                    minLines = 3
                                )
                            }

                            OutlinedTextField(
                                value = form.password,
                                onValueChange = onPasswordChange,
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("New password") },
                                placeholder = { Text("Leave blank to keep current password") },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null)
                                    }
                                }
                            )

                            OutlinedTextField(
                                value = form.confirmPassword,
                                onValueChange = onConfirmPasswordChange,
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Confirm password") },
                                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                        Icon(imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null)
                                    }
                                }
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Button(onClick = onSave, enabled = !state.isSaving) {
                                    Text(text = "Save changes")
                                }
                                OutlinedButton(onClick = onRefresh, enabled = !state.isSaving) {
                                    Text(text = "Reset")
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = onLogout,
                                enabled = state.logoutState !is UiState.Loading,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (state.logoutState is UiState.Loading) {
                                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.height(18.dp))
                                } else {
                                    Text(text = "Log out")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
