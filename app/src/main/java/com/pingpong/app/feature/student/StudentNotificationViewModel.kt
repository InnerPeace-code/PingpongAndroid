package com.pingpong.app.feature.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pingpong.app.core.common.UiState
import com.pingpong.app.core.data.NotificationRepository
import com.pingpong.app.core.data.SessionRepository
import com.pingpong.app.core.model.student.NotificationItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class StudentNotificationViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<NotificationItem>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<NotificationItem>>> = _uiState

    private var userId: Long? = null
    private var userType: String? = null

    init {
        viewModelScope.launch { loadSessionAndNotifications() }
    }

    private suspend fun loadSessionAndNotifications() {
        val session = runCatching { sessionRepository.currentSession() }
            .onFailure { throwable ->
                _uiState.value = UiState.Error(throwable.message)
            }
            .getOrNull() ?: return
        userId = session.userId
        userType = session.userType ?: session.role?.uppercase()
        refresh()
    }

    fun refresh() {
        val uid = userId ?: return
        val type = userType ?: return
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            val result = notificationRepository.getUnreadNotifications(uid, type)
            result
                .onSuccess { notifications ->
                    _uiState.value = UiState.Success(notifications)
                }
                .onFailure { throwable ->
                    _uiState.value = UiState.Error(throwable.message)
                }
        }
    }

    fun markAsRead(notificationId: Long) {
        viewModelScope.launch {
            val result = notificationRepository.markAsRead(notificationId)
            result
                .onSuccess { refresh() }
                .onFailure { throwable ->
                    _uiState.update { UiState.Error(throwable.message) }
                }
        }
    }
}
