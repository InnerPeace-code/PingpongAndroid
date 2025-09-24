package com.pingpong.app.feature.superadmin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pingpong.app.core.common.UiState
import com.pingpong.app.core.data.SuperAdminRepository
import com.pingpong.app.core.model.admin.SchoolSummary
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
class SuperAdminSchoolsViewModel @Inject constructor(
    private val superAdminRepository: SuperAdminRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SuperAdminSchoolsUiState())
    val uiState: StateFlow<SuperAdminSchoolsUiState> = _uiState

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = superAdminRepository.getSchools()
            result
                .onSuccess { schools ->
                    _uiState.update { it.copy(isLoading = false, schools = schools) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message ?: "Failed to load schools") }
                }
        }
    }

    fun createSchool(name: String, address: String, description: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(actionState = UiState.Loading) }
            val payload = buildJsonObject {
                put("name", name)
                put("address", address)
                put("description", description)
            }
            val result = superAdminRepository.createSchool(payload)
            result
                .onSuccess {
                    _uiState.update { it.copy(actionState = UiState.Success(Unit), message = "School created") }
                    refresh()
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(actionState = UiState.Error(throwable.message)) }
                }
        }
    }

    fun updateSchool(id: Long, name: String, address: String, description: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(actionState = UiState.Loading) }
            val payload = buildJsonObject {
                put("id", kotlinx.serialization.json.JsonPrimitive(id))
                put("name", name)
                put("address", address)
                put("description", description)
            }
            val result = superAdminRepository.updateSchool(payload)
            result
                .onSuccess {
                    _uiState.update { it.copy(actionState = UiState.Success(Unit), message = "School updated") }
                    refresh()
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(actionState = UiState.Error(throwable.message)) }
                }
        }
    }

    fun deleteSchool(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(actionState = UiState.Loading) }
            val result = superAdminRepository.deleteSchool(id)
            result
                .onSuccess {
                    _uiState.update { it.copy(actionState = UiState.Success(Unit), message = "School deleted") }
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

data class SuperAdminSchoolsUiState(
    val schools: List<SchoolSummary> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val actionState: UiState<Unit> = UiState.Idle,
    val message: String? = null
)
