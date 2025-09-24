package com.pingpong.app.feature.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pingpong.app.core.common.UiState
import com.pingpong.app.core.data.SessionRepository
import com.pingpong.app.core.data.StudentCoachRepository
import com.pingpong.app.core.model.student.CoachDetail
import com.pingpong.app.core.model.student.CoachSummary
import com.pingpong.app.core.model.student.StudentCoachFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class StudentCoachBrowserViewModel @Inject constructor(
    private val studentCoachRepository: StudentCoachRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CoachBrowserUiState())
    val uiState: StateFlow<CoachBrowserUiState> = _uiState

    private var studentId: Long? = null
    private var schoolId: Long? = null

    init {
        viewModelScope.launch {
            loadSessionAndData()
        }
    }

    private suspend fun loadSessionAndData() {
        runCatching { sessionRepository.currentSession() }
            .onSuccess { session ->
                studentId = session.userId
                schoolId = session.schoolId
                refresh()
            }
            .onFailure { throwable ->
                _uiState.update { it.copy(error = throwable.message ?: "Unable to load session") }
            }
    }

    fun updateName(name: String) {
        _uiState.update { it.copy(filter = it.filter.copy(name = name)) }
    }

    fun updateGender(isMale: Boolean?) {
        _uiState.update { it.copy(filter = it.filter.copy(isMale = isMale)) }
    }

    fun updateLevel(level: Int?) {
        _uiState.update { it.copy(filter = it.filter.copy(level = level)) }
    }

    fun updateAgeRange(low: Int?, high: Int?) {
        _uiState.update { it.copy(filter = it.filter.copy(ageLow = low, ageHigh = high)) }
    }

    fun refresh() {
        val sid = schoolId ?: return
        val filter = _uiState.value.filter
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = studentCoachRepository.getCoachList(filter, sid)
            result
                .onSuccess { coaches ->
                    _uiState.update { it.copy(isLoading = false, coaches = coaches) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message ?: "Failed to load coaches") }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun loadCoachDetail(coachId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(detailState = UiState.Loading) }
            val result = studentCoachRepository.getCoachDetail(coachId)
            result
                .onSuccess { detail ->
                    _uiState.update { it.copy(detailState = UiState.Success(detail)) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(detailState = UiState.Error(throwable.message)) }
                }
        }
    }

    fun dismissCoachDetail() {
        _uiState.update { it.copy(detailState = UiState.Idle) }
    }

    fun selectCoach(coachId: Long) {
        val sid = studentId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(actionState = UiState.Loading) }
            val result = studentCoachRepository.selectCoach(coachId, sid)
            result
                .onSuccess {
                    _uiState.update { it.copy(actionState = UiState.Success(Unit), message = "Request submitted") }
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

data class CoachBrowserUiState(
    val filter: StudentCoachFilter = StudentCoachFilter(),
    val coaches: List<CoachSummary> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val detailState: UiState<CoachDetail> = UiState.Idle,
    val actionState: UiState<Unit> = UiState.Idle,
    val message: String? = null
)
