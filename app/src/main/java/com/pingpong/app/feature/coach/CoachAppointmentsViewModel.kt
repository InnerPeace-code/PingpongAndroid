package com.pingpong.app.feature.coach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pingpong.app.core.common.UiState
import com.pingpong.app.core.data.AppointmentRepository
import com.pingpong.app.core.data.SessionRepository
import com.pingpong.app.core.model.student.PendingCancelRequest
import com.pingpong.app.core.model.student.StudentAppointmentItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val USER_TYPE_COACH = "COACH"

@HiltViewModel
class CoachAppointmentsViewModel @Inject constructor(
    private val appointmentRepository: AppointmentRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CoachAppointmentsUiState())
    val uiState: StateFlow<CoachAppointmentsUiState> = _uiState

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
            val appointmentsResult = appointmentRepository.getCoachAppointments(id)
            appointmentsResult
                .onSuccess { appointments ->
                    _uiState.update { it.copy(isLoading = false, appointments = appointments) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message ?: "Failed to load appointments") }
                }
            refreshSupplementalData(id)
        }
    }

    private suspend fun refreshSupplementalData(coachId: Long) = coroutineScope {
        val cancelDeferred = async { appointmentRepository.getPendingCancelRecords(coachId, USER_TYPE_COACH) }
        val remainingDeferred = async { appointmentRepository.getRemainingCancelCount(coachId, USER_TYPE_COACH) }
        cancelDeferred.await()
            .onSuccess { requests ->
                _uiState.update { it.copy(pendingCancelRequests = requests) }
            }
            .onFailure { throwable ->
                _uiState.update { it.copy(cancelError = throwable.message) }
            }
        remainingDeferred.await()
            .onSuccess { count ->
                _uiState.update { it.copy(remainingCancelCount = count) }
            }
            .onFailure { throwable ->
                _uiState.update { it.copy(cancelError = throwable.message) }
            }
    }

    fun confirmAppointment(appointmentId: Long, accept: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(actionState = UiState.Loading) }
            val result = appointmentRepository.handleCoachConfirmation(appointmentId, accept)
            result
                .onSuccess {
                    _uiState.update { it.copy(actionState = UiState.Success(Unit), message = if (accept) "Appointment confirmed" else "Appointment rejected") }
                    refresh()
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(actionState = UiState.Error(throwable.message)) }
                }
        }
    }

    fun requestCancellation(appointmentId: Long) {
        val id = coachId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(actionState = UiState.Loading) }
            val result = appointmentRepository.requestCancel(appointmentId, id, USER_TYPE_COACH)
            result
                .onSuccess {
                    _uiState.update { it.copy(actionState = UiState.Success(Unit), message = "Cancellation request submitted") }
                    refresh()
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(actionState = UiState.Error(throwable.message)) }
                }
        }
    }

    fun handleCancelRequest(cancelRecordId: Long, approve: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(actionState = UiState.Loading) }
            val result = appointmentRepository.handleCancelRequest(cancelRecordId, approve)
            result
                .onSuccess {
                    _uiState.update { it.copy(actionState = UiState.Success(Unit), message = if (approve) "Cancellation approved" else "Cancellation rejected") }
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

data class CoachAppointmentsUiState(
    val appointments: List<StudentAppointmentItem> = emptyList(),
    val pendingCancelRequests: List<PendingCancelRequest> = emptyList(),
    val remainingCancelCount: Int = 0,
    val isLoading: Boolean = false,
    val cancelError: String? = null,
    val error: String? = null,
    val actionState: UiState<Unit> = UiState.Idle,
    val message: String? = null
)
