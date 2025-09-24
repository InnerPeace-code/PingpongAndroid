package com.pingpong.app.feature.auth.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pingpong.app.core.common.UiState

@Composable
fun RegisterRoute(
    onBack: () -> Unit,
    onPending: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val schoolState by viewModel.schoolState.collectAsStateWithLifecycle()
    val submitState by viewModel.submitState.collectAsStateWithLifecycle()

    LaunchedEffect(submitState) {
        if (submitState is UiState.Success) {
            onPending()
            viewModel.resetSubmitState()
        }
    }

    RegisterScreen(
        formState = formState,
        schoolState = schoolState,
        submitState = submitState,
        onRoleChange = viewModel::updateRole,
        onUsernameChange = viewModel::updateUsername,
        onPasswordChange = viewModel::updatePassword,
        onConfirmPasswordChange = viewModel::updateConfirmPassword,
        onRealNameChange = viewModel::updateRealName,
        onGenderChange = viewModel::updateGender,
        onAgeChange = viewModel::updateAge,
        onCampusChange = viewModel::updateCampus,
        onPhoneChange = viewModel::updatePhone,
        onEmailChange = viewModel::updateEmail,
        onAchievementChange = viewModel::updateAchievements,
        onSubmit = { viewModel.register() },
        onBack = onBack
    )
}

@Composable
private fun RegisterScreen(
    formState: RegisterFormState,
    schoolState: SchoolSelectionState,
    submitState: UiState<Unit>,
    onRoleChange: (RegisterRole) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onRealNameChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onCampusChange: (Long?) -> Unit,
    onPhoneChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onAchievementChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Create Account", style = MaterialTheme.typography.headlineSmall)

        RoleTabs(selectedRole = formState.role, onRoleSelected = onRoleChange)

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = formState.username,
                    onValueChange = onUsernameChange,
                    label = { Text("Username") },
                    supportingText = { Text("4-16 characters, letters / digits / underscore") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = formState.password,
                    onValueChange = onPasswordChange,
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
                OutlinedTextField(
                    value = formState.confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    label = { Text("Confirm Password") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                OutlinedTextField(
                    value = formState.realName,
                    onValueChange = onRealNameChange,
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                GenderSelector(selected = formState.gender, onSelected = onGenderChange)

                OutlinedTextField(
                    value = formState.age,
                    onValueChange = onAgeChange,
                    label = { Text("Age") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                CampusSelector(
                    schoolState = schoolState,
                    selectedId = formState.campusId,
                    onCampusChange = onCampusChange
                )

                OutlinedTextField(
                    value = formState.phone,
                    onValueChange = onPhoneChange,
                    label = { Text("Phone") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                OutlinedTextField(
                    value = formState.email,
                    onValueChange = onEmailChange,
                    label = { Text("Email (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                if (formState.role == RegisterRole.COACH) {
                    OutlinedTextField(
                        value = formState.achievements,
                        onValueChange = onAchievementChange,
                        label = { Text("Bio & achievements") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    )
                    Text(text = "Coach photo upload coming soon", style = MaterialTheme.typography.bodySmall)
                }

                Button(
                    onClick = onSubmit,
                    enabled = formState.isValid && submitState !is UiState.Loading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = if (submitState is UiState.Loading) "Submitting..." else "Submit Application")
                }

                if (submitState is UiState.Error) {
                    Text(text = submitState.message ?: "Submission failed", color = MaterialTheme.colorScheme.error)
                }

                TextButton(onClick = onBack) {
                    Text("Back to login")
                }
            }
        }
    }
}

@Composable
private fun RoleTabs(selectedRole: RegisterRole, onRoleSelected: (RegisterRole) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        RegisterRole.entries.forEach { role ->
            Button(
                onClick = { onRoleSelected(role) },
                enabled = role != selectedRole,
                modifier = Modifier.weight(1f)
            ) {
                Text(role.displayName)
            }
        }
    }
}

@Composable
private fun GenderSelector(selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Gender")
        OutlinedTextField(
            value = when (selected) {
                "male" -> "Male"
                "female" -> "Female"
                else -> "Select gender"
            },
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null)
                }
            }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            listOf("male" to "Male", "female" to "Female").forEach { (value, label) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        expanded = false
                        onSelected(value)
                    }
                )
            }
        }
    }
}

@Composable
private fun CampusSelector(
    schoolState: SchoolSelectionState,
    selectedId: Long?,
    onCampusChange: (Long?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "School / Campus")
        OutlinedTextField(
            value = schoolState.schools.firstOrNull { it.id == selectedId }?.name ?: "Select school",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null)
                }
            }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            schoolState.schools.forEach { school ->
                DropdownMenuItem(
                    text = { Text(school.name) },
                    onClick = {
                        expanded = false
                        onCampusChange(school.id)
                    }
                )
            }
        }
    }
}

