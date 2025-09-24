package com.pingpong.app.feature.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pingpong.app.core.auth.TokenProvider
import com.pingpong.app.core.common.UiState
import com.pingpong.app.core.data.ScheduleRepository
import com.pingpong.app.core.data.SessionRepository
import com.pingpong.app.core.model.admin.SchoolSummary
import com.pingpong.app.core.model.student.TimeSlot
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val sessionRepository: SessionRepository,
    private val tokenProvider: TokenProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState

    private var isSuperAdmin: Boolean = false
    private var token: String? = null

    init {
        viewModelScope.launch {
            loadInitialData()
        }
    }

    private suspend fun loadInitialData() {
        runCatching { sessionRepository.currentSession() }
            .onSuccess { session ->
                isSuperAdmin = session.role == "super_admin"
                token = if (isSuperAdmin) null else tokenProvider.currentToken()
                loadSchools()
            }
            .onFailure { throwable ->
                _uiState.update { it.copy(error = throwable.message ?: "Unable to load session") }
            }
    }

    private fun loadSchools() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = scheduleRepository.getAvailableSchools(isSuperAdmin, token)
            result
                .onSuccess { schools ->
                    val selected = schools.firstOrNull()?.id
                    _uiState.update { it.copy(isLoading = false, schools = schools, selectedSchoolId = selected) }
                    if (selected != null) {
                        loadSchedule(selected)
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message ?: "Failed to load schools") }
                }
        }
    }

    fun selectSchool(id: Long) {
        _uiState.update { it.copy(selectedSchoolId = id) }
        loadSchedule(id)
    }

    private fun loadSchedule(schoolId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(scheduleState = UiState.Loading) }
            val result = scheduleRepository.getSchoolSchedule(schoolId, isSuperAdmin)
            result
                .onSuccess { slots ->
                    _uiState.update { it.copy(scheduleState = UiState.Success(slots)) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(scheduleState = UiState.Error(throwable.message)) }
                }
        }
    }
}

data class ScheduleUiState(
    val schools: List<SchoolSummary> = emptyList(),
    val selectedSchoolId: Long? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val scheduleState: UiState<List<TimeSlot>> = UiState.Idle
)
