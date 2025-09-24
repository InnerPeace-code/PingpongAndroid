package com.pingpong.app.feature.evaluation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pingpong.app.core.common.UiState
import com.pingpong.app.core.data.EvaluationRepository
import com.pingpong.app.core.data.SessionRepository
import com.pingpong.app.core.model.evaluation.EvaluationItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class EvaluationViewModel @Inject constructor(
    private val evaluationRepository: EvaluationRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EvaluationUiState())
    val uiState: StateFlow<EvaluationUiState> = _uiState

    private var userId: Long? = null
    private var userType: String = "STUDENT"

    init {
        viewModelScope.launch {
            loadSessionAndData()
        }
    }

    private suspend fun loadSessionAndData() {
        runCatching { sessionRepository.currentSession() }
            .onSuccess { session ->
                userId = session.userId
                userType = session.role.uppercase()
                refresh()
            }
            .onFailure { throwable ->
                _uiState.update { it.copy(error = throwable.message ?: "Unable to load session") }
            }
    }

    fun refresh() {
        val id = userId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = evaluationRepository.getEvaluationsForUser(id, userType)
            result
                .onSuccess { evaluations ->
                    _uiState.update { it.copy(isLoading = false, evaluations = evaluations) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message ?: "Failed to load evaluations") }
                }
        }
    }

    fun submitEvaluation(appointmentId: Long, content: String) {
        val id = userId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(actionState = UiState.Loading) }
            val result = evaluationRepository.createEvaluation(appointmentId, id, userType, content)
            result
                .onSuccess {
                    _uiState.update { it.copy(actionState = UiState.Success(Unit), message = "Evaluation submitted") }
                    refresh()
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(actionState = UiState.Error(throwable.message)) }
                }
        }
    }

    fun deleteEvaluation(evaluationId: Long) {
        val id = userId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(actionState = UiState.Loading) }
            val result = evaluationRepository.deleteEvaluation(evaluationId, id)
            result
                .onSuccess {
                    _uiState.update { it.copy(actionState = UiState.Success(Unit), message = "Evaluation deleted") }
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

data class EvaluationUiState(
    val evaluations: List<EvaluationItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val actionState: UiState<Unit> = UiState.Idle,
    val message: String? = null
)
