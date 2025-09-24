package com.pingpong.app.feature.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pingpong.app.core.common.UiState
import com.pingpong.app.core.data.CoachChangeRepository
import com.pingpong.app.core.data.SessionRepository
import com.pingpong.app.core.model.student.CoachChangeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AdminChangeManageViewModel @Inject constructor(
    private val coachChangeRepository: CoachChangeRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminChangeManageUiState())
    val uiState: StateFlow<AdminChangeManageUiState> = _uiState

    private var userId: Long? = null

    init {
        viewModelScope.launch {
            loadSessionAndData()
        }
    }

    private suspend fun loadSessionAndData() {
        runCatching { sessionRepository.currentSession() }
            .onSuccess { session ->
                userId = session.userId
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
            val result = coachChangeRepository.getRelatedRequests(id, "ADMIN")
            result
                .onSuccess { requests ->
                    _uiState.update { it.copy(isLoading = false, requests = requests) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message ?: "Failed to load requests") }
                }
        }
    }

    fun handle(requestId: Long, approve: Boolean) {
        val id = userId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(actionState = UiState.Loading) }
            val result = coachChangeRepository.handleChangeRequest(requestId, id, "ADMIN", approve)
            result
                .onSuccess {
                    _uiState.update { it.copy(actionState = UiState.Success(Unit), message = if (approve) "Approved" else "Rejected") }
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

data class AdminChangeManageUiState(
    val requests: List<CoachChangeRequest> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val actionState: UiState<Unit> = UiState.Idle,
    val message: String? = null
)
