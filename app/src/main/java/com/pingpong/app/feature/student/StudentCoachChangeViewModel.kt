package com.pingpong.app.feature.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pingpong.app.core.common.UiState
import com.pingpong.app.core.data.CoachChangeRepository
import com.pingpong.app.core.data.SessionRepository
import com.pingpong.app.core.model.student.CoachChangeOption
import com.pingpong.app.core.model.student.CoachChangeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class StudentCoachChangeViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val coachChangeRepository: CoachChangeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentCoachChangeUiState())
    val uiState: StateFlow<StudentCoachChangeUiState> = _uiState

    private var studentId: Long? = null

    init {
        viewModelScope.launch { loadSessionAndData() }
    }

    private suspend fun loadSessionAndData() {
        val session = runCatching { sessionRepository.currentSession() }
            .onFailure { throwable ->
                _uiState.update { it.copy(message = throwable.message ?: "Unable to load coach relationship") }
            }
            .getOrNull() ?: return
        studentId = session.userId
        refresh()
    }

    fun refresh() {
        val sid = studentId ?: return
        _uiState.update { it.copy(currentCoachState = UiState.Loading, schoolCoachState = UiState.Loading) }
        viewModelScope.launch {
            val currentResult = coachChangeRepository.getCurrentCoaches(sid)
            val schoolResult = coachChangeRepository.getSchoolCoaches(sid)
            currentResult
                .onSuccess { list ->
                    _uiState.update { it.copy(currentCoachState = UiState.Success(list)) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(currentCoachState = UiState.Error(throwable.message)) }
                }
            schoolResult
                .onSuccess { list ->
                    _uiState.update { it.copy(schoolCoachState = UiState.Success(list)) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(schoolCoachState = UiState.Error(throwable.message)) }
                }
            loadRequests()
        }
    }

    private suspend fun loadRequests() {
        val sid = studentId ?: return
        val result = coachChangeRepository.getRelatedRequests(sid, "STUDENT")
        result
            .onSuccess { requests ->
                _uiState.update { it.copy(requests = requests) }
            }
            .onFailure { throwable ->
                _uiState.update { it.copy(message = throwable.message ?: "Unable to load requests") }
            }
    }

    fun updateCurrentSelection(option: CoachChangeOption?) {
        _uiState.update { it.copy(selectedCurrent = option) }
    }

    fun updateTargetSelection(option: CoachChangeOption?) {
        _uiState.update { it.copy(selectedTarget = option) }
    }

    fun submitChangeRequest() {
        val sid = studentId ?: return
        val currentCoach = _uiState.value.selectedCurrent ?: run {
            _uiState.update { it.copy(message = "Please choose your current coach") }
            return
        }
        val targetCoach = _uiState.value.selectedTarget ?: run {
            _uiState.update { it.copy(message = "Please choose a target coach") }
            return
        }
        if (currentCoach.id == targetCoach.id) {
            _uiState.update { it.copy(message = "Please choose a different coach") }
            return
        }
        _uiState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            val result = coachChangeRepository.submitChangeRequest(sid, currentCoach.id, targetCoach.id)
            result
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            message = "Request submitted",
                            selectedTarget = null
                        )
                    }
                    loadRequests()
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isSubmitting = false, message = throwable.message ?: "Failed to submit request") }
                }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}

data class StudentCoachChangeUiState(
    val currentCoachState: UiState<List<CoachChangeOption>> = UiState.Loading,
    val schoolCoachState: UiState<List<CoachChangeOption>> = UiState.Loading,
    val requests: List<CoachChangeRequest> = emptyList(),
    val selectedCurrent: CoachChangeOption? = null,
    val selectedTarget: CoachChangeOption? = null,
    val isSubmitting: Boolean = false,
    val message: String? = null
)
