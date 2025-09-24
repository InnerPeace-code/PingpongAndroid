package com.pingpong.app.feature.superadmin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pingpong.app.core.common.UiState
import com.pingpong.app.core.data.SuperAdminRepository
import com.pingpong.app.core.model.admin.AdminCoachSummary
import com.pingpong.app.core.model.coach.CoachApplication
import com.pingpong.app.core.model.student.CoachDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SuperAdminCoachesViewModel @Inject constructor(
    private val superAdminRepository: SuperAdminRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SuperAdminCoachesUiState())
    val uiState: StateFlow<SuperAdminCoachesUiState> = _uiState

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val pendingDeferred = async { superAdminRepository.getAllUncertifiedCoaches() }
            val certifiedDeferred = async { superAdminRepository.getAllCertifiedCoaches() }
            val results = awaitAll(pendingDeferred, certifiedDeferred)
            val pendingResult = results[0] as Result<List<CoachApplication>>
            val certifiedResult = results[1] as Result<List<AdminCoachSummary>>
            pendingResult
                .onSuccess { pending ->
                    certifiedResult
                        .onSuccess { certified ->
                            _uiState.update { it.copy(isLoading = false, pending = pending, certified = certified) }
                        }
                        .onFailure { throwable ->
                            _uiState.update { it.copy(isLoading = false, error = throwable.message ?: "Failed to load certified coaches", pending = pending) }
                        }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message ?: "Failed to load pending coaches") }
                }
        }
    }

    fun viewCoachDetail(coachId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(detailState = UiState.Loading) }
            val result = superAdminRepository.getCoachDetail(coachId)
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

    fun certify(coachId: Long, isAccepted: Boolean, level: Int?) {
        viewModelScope.launch {
            _uiState.update { it.copy(actionState = UiState.Loading) }
            val result = superAdminRepository.certifyCoach(coachId, isAccepted, level)
            result
                .onSuccess {
                    _uiState.update { it.copy(actionState = UiState.Success(Unit), message = if (isAccepted) "Coach approved" else "Coach rejected") }
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

data class SuperAdminCoachesUiState(
    val pending: List<CoachApplication> = emptyList(),
    val certified: List<AdminCoachSummary> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val detailState: UiState<CoachDetail> = UiState.Idle,
    val actionState: UiState<Unit> = UiState.Idle,
    val message: String? = null
)
