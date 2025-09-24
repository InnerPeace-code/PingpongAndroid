package com.pingpong.app.feature.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pingpong.app.core.common.UiState
import com.pingpong.app.core.data.AdminRepository
import com.pingpong.app.core.model.admin.AdminCoachSummary
import com.pingpong.app.core.model.student.CoachDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AdminCoachManageViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminCoachManageUiState())
    val uiState: StateFlow<AdminCoachManageUiState> = _uiState

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = adminRepository.getCertifiedCoaches()
            result
                .onSuccess { coaches ->
                    _uiState.update { it.copy(isLoading = false, coaches = coaches) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message ?: "Failed to load coaches") }
                }
        }
    }

    fun viewCoachDetail(coachId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(detailState = UiState.Loading) }
            val result = adminRepository.getCoachDetail(coachId)
            result
                .onSuccess { detail ->
                    _uiState.update { it.copy(detailState = UiState.Success(detail)) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(detailState = UiState.Error(throwable.message)) }
                }
        }
    }

    fun dismissDetail() {
        _uiState.update { it.copy(detailState = UiState.Idle) }
    }

    fun toggleStatus(coachId: Long, status: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(actionState = UiState.Loading) }
            val result = adminRepository.updateCoachStatus(coachId, status)
            result
                .onSuccess {
                    _uiState.update { it.copy(actionState = UiState.Success(Unit), message = "Status updated") }
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

data class AdminCoachManageUiState(
    val coaches: List<AdminCoachSummary> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val detailState: UiState<CoachDetail> = UiState.Idle,
    val actionState: UiState<Unit> = UiState.Idle,
    val message: String? = null
)
