package com.pingpong.app.feature.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pingpong.app.core.common.UiState
import com.pingpong.app.core.data.AppointmentRepository
import com.pingpong.app.core.data.ScheduleRepository
import com.pingpong.app.core.data.SessionRepository
import com.pingpong.app.core.data.StudentCoachRepository
import com.pingpong.app.core.model.student.CoachScheduleItem
import com.pingpong.app.core.model.student.CoachSummary
import com.pingpong.app.core.model.student.PendingCancelRequest
import com.pingpong.app.core.model.student.StudentAppointmentItem
import com.pingpong.app.core.model.student.TimeSlot
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class StudentBookingViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val studentCoachRepository: StudentCoachRepository,
    private val appointmentRepository: AppointmentRepository,
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentBookingUiState())
    val uiState: StateFlow<StudentBookingUiState> = _uiState

    private var studentId: Long? = null
    private var schoolId: Long? = null
    private var isSuper: Boolean = false

    private var loadScheduleJob: Job? = null

    init {
        viewModelScope.launch {
            loadSession()
        }
    }

    private suspend fun loadSession() {
        val session = runCatching { sessionRepository.currentSession() }
            .onFailure { throwable ->
                _uiState.update { it.copy(globalError = throwable.message ?: "Unable to load profile") }
            }
            .getOrNull() ?: return
        studentId = session.userId
        schoolId = session.schoolId
        isSuper = session.role.equals("super_admin", ignoreCase = true)
        refreshAll()
    }

    fun refreshAll() {
        viewModelScope.launch {
            loadCoaches()
            loadSchoolSchedule()
            loadAppointments()
            loadPendingCancelRequests()
            loadRemainingCancelCount()
        }
    }

    fun selectCoach(coachId: Long?) {
        _uiState.update { it.copy(selectedCoachId = coachId) }
        if (coachId != null) {
            loadScheduleForCoach(coachId)
        } else {
            _uiState.update { it.copy(scheduleState = UiState.Success(emptyList())) }
        }
    }

    private suspend fun loadCoaches() {
        val sid = studentId ?: return
        _uiState.update { it.copy(coachListState = UiState.Loading) }
        val result = studentCoachRepository.getRelatedCoaches(sid)
        result
            .onSuccess { coaches ->
                _uiState.update { current ->
                    val selected = current.selectedCoachId ?: coaches.firstOrNull()?.id
                    current.copy(coachListState = UiState.Success(coaches), selectedCoachId = selected)
                }
                val selected = _uiState.value.selectedCoachId
                if (selected != null) {
                    loadScheduleForCoach(selected)
                }
            }
            .onFailure { throwable ->
                _uiState.update { it.copy(coachListState = UiState.Error(throwable.message)) }
            }
    }

    private fun loadScheduleForCoach(coachId: Long) {
        loadScheduleJob?.cancel()
        loadScheduleJob = viewModelScope.launch {
            _uiState.update { it.copy(scheduleState = UiState.Loading) }
            val result = appointmentRepository.getCoachSchedule(coachId)
            result
                .onSuccess { schedule ->
                    _uiState.update { it.copy(scheduleState = UiState.Success(schedule)) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(scheduleState = UiState.Error(throwable.message)) }
                }
        }
    }

    private suspend fun loadSchoolSchedule() {
        val sid = schoolId ?: return
        val result = scheduleRepository.getSchoolSchedule(sid, isSuper)
        result
            .onSuccess { slots ->
                _uiState.update { it.copy(timeSlots = slots) }
            }
            .onFailure { throwable ->
                _uiState.update { it.copy(scheduleMessage = throwable.message) }
            }
    }

    private suspend fun loadAppointments() {
        val sid = studentId ?: return
        val result = appointmentRepository.getStudentAppointments(sid)
        result
            .onSuccess { appointments ->
                _uiState.update { it.copy(myAppointmentsState = UiState.Success(appointments)) }
            }
            .onFailure { throwable ->
                _uiState.update { it.copy(myAppointmentsState = UiState.Error(throwable.message)) }
            }
    }

    private suspend fun loadPendingCancelRequests() {
        val sid = studentId ?: return
        val result = appointmentRepository.getPendingCancelRecords(sid, "COACH")
        result
            .onSuccess { requests ->
                _uiState.update { it.copy(pendingCancelRequests = requests) }
            }
            .onFailure { throwable ->
                _uiState.update { it.copy(cancelRequestsError = throwable.message) }
            }
    }

    private suspend fun loadRemainingCancelCount() {
        val sid = studentId ?: return
        val result = appointmentRepository.getRemainingCancelCount(sid, "STUDENT")
        result
            .onSuccess { count ->
                _uiState.update { it.copy(remainingCancelCount = count) }
            }
    }

    fun openBookingDialog(date: LocalDate, startTime: String, endTime: String) {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        _uiState.update {
            it.copy(
                bookingForm = BookingFormState(
                    visible = true,
                    date = date.format(formatter),
                    startTime = startTime,
                    endTime = endTime
                )
            )
        }
    }

    fun dismissBookingDialog() {
        _uiState.update { it.copy(bookingForm = BookingFormState()) }
    }

    fun toggleAutoAssign(autoAssign: Boolean) {
        _uiState.update {
            it.copy(bookingForm = it.bookingForm.copy(autoAssign = autoAssign, tableId = if (autoAssign) null else it.bookingForm.tableId))
        }
    }

    fun updateTableId(tableId: Long?) {
        _uiState.update { it.copy(bookingForm = it.bookingForm.copy(tableId = tableId, autoAssign = tableId == null)) }
    }

    fun submitBooking() {
        val form = _uiState.value.bookingForm
        val coachId = _uiState.value.selectedCoachId
        val sid = studentId
        if (!form.visible || coachId == null || sid == null) {
            return
        }
        if (form.startTime.isBlank() || form.endTime.isBlank() || form.date.isBlank()) {
            _uiState.update { it.copy(message = "Incomplete booking information") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(bookingInProgress = true) }
            val result = appointmentRepository.bookCourse(
                coachId = coachId,
                studentId = sid,
                startTime = "${form.date}T${form.startTime}",
                endTime = "${form.date}T${form.endTime}",
                tableId = form.tableId,
                autoAssign = form.autoAssign
            )
            result
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            bookingInProgress = false,
                            bookingForm = BookingFormState(),
                            message = "Booking request submitted"
                        )
                    }
                    loadScheduleForCoach(coachId)
                    loadAppointments()
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            bookingInProgress = false,
                            message = throwable.message ?: "Failed to submit booking"
                        )
                    }
                }
        }
    }

    fun requestCancel(appointmentId: Long) {
        val sid = studentId ?: return
        viewModelScope.launch {
            val result = appointmentRepository.requestCancel(appointmentId, sid, "STUDENT")
            result
                .onSuccess {
                    _uiState.update { it.copy(message = "Cancellation request submitted") }
                    loadAppointments()
                    loadScheduleForCoach(_uiState.value.selectedCoachId ?: return@launch)
                    loadRemainingCancelCount()
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(message = throwable.message ?: "Failed to request cancellation") }
                }
        }
    }

    fun respondCoachCancel(cancelRecordId: Long, approve: Boolean) {
        viewModelScope.launch {
            val result = appointmentRepository.handleCancelRequest(cancelRecordId, approve)
            result
                .onSuccess {
                    _uiState.update { it.copy(message = if (approve) "Cancellation approved" else "Cancellation rejected") }
                    loadPendingCancelRequests()
                    loadAppointments()
                    _uiState.value.selectedCoachId?.let { loadScheduleForCoach(it) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(message = throwable.message ?: "Failed to process request") }
                }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}

data class StudentBookingUiState(
    val coachListState: UiState<List<CoachSummary>> = UiState.Loading,
    val selectedCoachId: Long? = null,
    val timeSlots: List<TimeSlot> = emptyList(),
    val scheduleState: UiState<List<CoachScheduleItem>> = UiState.Idle,
    val myAppointmentsState: UiState<List<StudentAppointmentItem>> = UiState.Loading,
    val pendingCancelRequests: List<PendingCancelRequest> = emptyList(),
    val remainingCancelCount: Int = 0,
    val scheduleMessage: String? = null,
    val cancelRequestsError: String? = null,
    val bookingForm: BookingFormState = BookingFormState(),
    val bookingInProgress: Boolean = false,
    val message: String? = null,
    val globalError: String? = null
)

data class BookingFormState(
    val visible: Boolean = false,
    val date: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val autoAssign: Boolean = true,
    val tableId: Long? = null
)
