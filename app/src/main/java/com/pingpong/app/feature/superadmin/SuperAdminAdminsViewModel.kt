package com.pingpong.app.feature.superadmin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pingpong.app.core.common.UiState
import com.pingpong.app.core.data.SuperAdminRepository
import com.pingpong.app.core.model.admin.AdminUserSummary
import com.pingpong.app.core.model.admin.SchoolSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@HiltViewModel
class SuperAdminAdminsViewModel @Inject constructor(
    private val superAdminRepository: SuperAdminRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SuperAdminAdminsUiState())
    val uiState: StateFlow<SuperAdminAdminsUiState> = _uiState

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val adminsDeferred = async { superAdminRepository.getAdmins() }
            val schoolsDeferred = async { superAdminRepository.getSchools() }
            val results = awaitAll(adminsDeferred, schoolsDeferred)
            val adminsResult = results[0] as Result<List<AdminUserSummary>>
            val schoolsResult = results[1] as Result<List<SchoolSummary>>
            adminsResult
                .onSuccess { admins ->
                    schoolsResult
                        .onSuccess { schools ->
                            _uiState.update { it.copy(isLoading = false, admins = admins, schools = schools) }
                        }
                        .onFailure { throwable ->
                            _uiState.update { it.copy(isLoading = false, error = throwable.message ?: "Failed to load schools", admins = admins) }
                        }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message ?: "Failed to load admins") }
                }
        }
    }

    fun createAdmin(username: String, password: String, name: String, phone: String, email: String, schoolId: Long?) {
        viewModelScope.launch {
            _uiState.update { it.copy(actionState = UiState.Loading) }
            val payload = buildJsonObject {
                put("username", username)
                put("password", password)
                put("name", name)
                put("phone", phone)
                put("email", email)
                schoolId?.let { put("schoolId", kotlinx.serialization.json.JsonPrimitive(it)) }
            }
            val result = superAdminRepository.createAdmin(payload)
            result
                .onSuccess {
                    _uiState.update { it.copy(actionState = UiState.Success(Unit), message = "Admin created") }
                    refresh()
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(actionState = UiState.Error(throwable.message)) }
                }
        }
    }

    fun updateAdmin(id: Long, name: String, phone: String, email: String, schoolId: Long?) {
        viewModelScope.launch {
            _uiState.update { it.copy(actionState = UiState.Loading) }
            val payload = buildJsonObject {
                put("id", id)
                put("name", name)
                put("phone", phone)
                put("email", email)
                schoolId?.let { put("schoolId", kotlinx.serialization.json.JsonPrimitive(it)) }
            }
            val result = superAdminRepository.updateAdmin(payload)
            result
                .onSuccess {
                    _uiState.update { it.copy(actionState = UiState.Success(Unit), message = "Admin updated") }
                    refresh()
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(actionState = UiState.Error(throwable.message)) }
                }
        }
    }

    fun deleteAdmin(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(actionState = UiState.Loading) }
            val result = superAdminRepository.deleteAdmin(id)
            result
                .onSuccess {
                    _uiState.update { it.copy(actionState = UiState.Success(Unit), message = "Admin deleted") }
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

data class SuperAdminAdminsUiState(
    val admins: List<AdminUserSummary> = emptyList(),
    val schools: List<SchoolSummary> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val actionState: UiState<Unit> = UiState.Idle,
    val message: String? = null
)
