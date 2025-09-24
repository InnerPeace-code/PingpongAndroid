package com.pingpong.app.feature.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pingpong.app.core.common.UiState
import com.pingpong.app.core.data.SessionRepository
import com.pingpong.app.core.data.StudentCoachRepository
import com.pingpong.app.core.model.student.CoachSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class StudentMyCoachesViewModel @Inject constructor(
    private val studentCoachRepository: StudentCoachRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<CoachSummary>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<CoachSummary>>> = _uiState

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val sessionResult = runCatching { sessionRepository.currentSession() }
            sessionResult
                .onFailure { throwable ->
                    _uiState.value = UiState.Error(throwable.message)
                }
                .onSuccess { session ->
                    val studentId = session.userId
                    if (studentId == null) {
                        _uiState.value = UiState.Error("Missing student id")
                        return@onSuccess
                    }
                    val result = studentCoachRepository.getRelatedCoaches(studentId)
                    result
                        .onSuccess { coaches ->
                            _uiState.value = UiState.Success(coaches)
                        }
                        .onFailure { throwable ->
                            _uiState.value = UiState.Error(throwable.message)
                        }
                }
        }
    }
}
