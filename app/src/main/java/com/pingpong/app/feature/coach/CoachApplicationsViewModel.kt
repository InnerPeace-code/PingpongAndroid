package com.pingpong.app.feature.coach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pingpong.app.core.common.UiState
import com.pingpong.app.core.data.CoachRepository
import com.pingpong.app.core.data.SessionRepository
import com.pingpong.app.core.model.coach.CoachApplication
import com.pingpong.app.core.model.coach.CoachStudentDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CoachApplicationsViewModel @Inject constructor(
    private val coachRepository: CoachRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CoachApplicationsUiState())
    val uiState: StateFlow<CoachApplicationsUiState> = _uiState

    private var coachId: Long? = null

    init {
        viewModelScope.launch {
            loadSessionAndData()
        }
    }

    private suspend fun loadSessionAndData() {
        runCatching { sessionRepository.currentSession() }
            .onSuccess { session ->
                coachId = session.userId
                refresh()
            }
            .onFailure { throwable ->
                _uiState.update { it.copy(error = throwable.message ?: "Unable to load coach info") }
            }
    }

    fun refresh() {
        val id = coachId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = coachRepository.getStudentApplications(id)
            result
                .onSuccess { applications ->
                    _uiState.update { it.copy(isLoading = false, applications = applications) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message ?: "Failed to load applications") }
                }
        }
    }

    fun viewStudentDetail(studentId: Long?) {
        if (studentId == null) {
            _uiState.update { it.copy(detailState = UiState.Error("Student not linked")) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(detailState = UiState.Loading) }
            val result = coachRepository.getStudentDetail(studentId)
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

    fun handleApplication(applicationId: Long, approve: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(actionState = UiState.Loading) }
            val result = coachRepository.reviewApplication(applicationId, approve)
            result
                .onSuccess {
                    _uiState.update { state ->
                        val filtered = state.applications.filterNot { it.relationId == applicationId }
                        state.copy(
                            applications = filtered,
                            actionState = UiState.Success(Unit),
                            message = if (approve) "Application approved" else "Application rejected"
                        )
                    }
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

data class CoachApplicationsUiState(
    val isLoading: Boolean = false,
    val applications: List<CoachApplication> = emptyList(),
    val error: String? = null,
    val detailState: UiState<CoachStudentDetail> = UiState.Idle,
    val actionState: UiState<Unit> = UiState.Idle,
    val message: String? = null
)
