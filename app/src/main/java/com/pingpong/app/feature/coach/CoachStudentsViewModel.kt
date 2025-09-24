package com.pingpong.app.feature.coach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pingpong.app.core.common.UiState
import com.pingpong.app.core.data.CoachRepository
import com.pingpong.app.core.data.SessionRepository
import com.pingpong.app.core.model.coach.CoachStudent
import com.pingpong.app.core.model.coach.CoachStudentDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CoachStudentsViewModel @Inject constructor(
    private val coachRepository: CoachRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CoachStudentsUiState())
    val uiState: StateFlow<CoachStudentsUiState> = _uiState

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
            val result = coachRepository.getRelatedStudents(id)
            result
                .onSuccess { students ->
                    _uiState.update { it.copy(isLoading = false, students = students) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message ?: "Failed to load students") }
                }
        }
    }

    fun viewStudentDetail(studentId: Long) {
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
}

data class CoachStudentsUiState(
    val students: List<CoachStudent> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val detailState: UiState<CoachStudentDetail> = UiState.Idle
)
