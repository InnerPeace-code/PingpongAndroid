package com.pingpong.app.feature.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pingpong.app.core.common.UiState
import com.pingpong.app.core.data.AdminRepository
import com.pingpong.app.core.model.student.CoachApplication
import com.pingpong.app.core.model.student.CoachDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AdminCoachAuditViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminCoachAuditUiState())
    val uiState: StateFlow<AdminCoachAuditUiState> = _uiState

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = adminRepository.getCoachRegister()
            result
                .onSuccess { applications ->
                    _uiState.update { it.copy(isLoading = false, applications = applications) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message ?: "Failed to load applications") }
                }
        }
    }

    fun viewCoachDetail(coachId: Long?) {
        if (coachId == null) {
            _uiState.update { it.copy(detailState = UiState.Error("Coach id unavailable")) }
            return
        }
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

    fun certify(coachId: Long?, isAccepted: Boolean, level: Int?) {
        val id = coachId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(actionState = UiState.Loading) }
            val result = adminRepository.certifyCoach(id, isAccepted, level)
            result
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            actionState = UiState.Success(Unit),
                            message = if (isAccepted) "Coach approved" else "Coach rejected"
                        )
                    }
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

data class AdminCoachAuditUiState(
    val isLoading: Boolean = false,
    val applications: List<CoachApplication> = emptyList(),
    val error: String? = null,
    val detailState: UiState<CoachDetail> = UiState.Idle,
    val actionState: UiState<Unit> = UiState.Idle,
    val message: String? = null
)
