package com.pingpong.app.feature.superadmin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pingpong.app.core.auth.TokenProvider
import com.pingpong.app.core.common.UiState
import com.pingpong.app.core.data.SuperAdminRepository
import com.pingpong.app.core.model.admin.AdminStudentSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@HiltViewModel
class SuperAdminStudentsViewModel @Inject constructor(
    private val superAdminRepository: SuperAdminRepository,
    private val tokenProvider: TokenProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(SuperAdminStudentsUiState())
    val uiState: StateFlow<SuperAdminStudentsUiState> = _uiState

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = superAdminRepository.getAllStudents()
            result
                .onSuccess { students ->
                    _uiState.update { it.copy(isLoading = false, students = students) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message ?: "Failed to load students") }
                }
        }
    }

    fun updateStudent(student: AdminStudentSummary, phone: String, email: String, status: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(actionState = UiState.Loading) }
            val token = tokenProvider.currentToken()
            val payload = buildJsonObject {
                put("id", kotlinx.serialization.json.JsonPrimitive(student.id))
                student.name?.let { put("name", it) }
                put("phone", phone)
                put("email", email)
                status?.let { put("status", it) }
            }
            val result = superAdminRepository.updateStudent(token, payload)
            result
                .onSuccess {
                    _uiState.update { it.copy(actionState = UiState.Success(Unit), message = "Student updated") }
                    refresh()
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(actionState = UiState.Error(throwable.message)) }
                }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null, actionState = UiState.Idle) }
    }
}

data class SuperAdminStudentsUiState(
    val students: List<AdminStudentSummary> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val actionState: UiState<Unit> = UiState.Idle,
    val message: String? = null
)
