package com.pingpong.app.feature.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pingpong.app.core.common.UiState
import com.pingpong.app.core.data.AdminRepository
import com.pingpong.app.core.model.admin.AdminCoachSummary
import com.pingpong.app.core.model.coach.CoachApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class CoachItem(
    val id: Long,
    val name: String,
    val campus: String? = null,
    val status: String? = null
)

data class AdminManagementState(
    val pendingCoaches: List<CoachItem> = emptyList(),
    val certifiedCoaches: List<CoachItem> = emptyList()
)

@HiltViewModel
class AdminManagementViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<AdminManagementState>>(UiState.Loading)
    val uiState: StateFlow<UiState<AdminManagementState>> = _uiState

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val pendingDeferred = async { adminRepository.getCoachRegister() }
            val certifiedDeferred = async { adminRepository.getCertifiedCoaches() }
            val pendingResult = pendingDeferred.await()
            val certifiedResult = certifiedDeferred.await()
            pendingResult
                .onSuccess { pending ->
                    certifiedResult
                        .onSuccess { certified ->
                            _uiState.value = UiState.Success(
                                AdminManagementState(
                                    pendingCoaches = pending.toPendingCoachItems(),
                                    certifiedCoaches = certified.toCertifiedCoachItems()
                                )
                            )
                        }
                        .onFailure { throwable ->
                            _uiState.value = UiState.Error(throwable.message ?: "Failed to load certified coaches")
                        }
                }
                .onFailure { throwable ->
                    _uiState.value = UiState.Error(throwable.message ?: "Failed to load pending coaches")
                }
        }
    }
}

private fun List<CoachApplication>.toPendingCoachItems(): List<CoachItem> = map { application ->
    CoachItem(
        id = application.coachId ?: application.relationId,
        name = application.coachName ?: application.studentName ?: "Coach",
        campus = application.status,
        status = application.status
    )
}

private fun List<AdminCoachSummary>.toCertifiedCoachItems(): List<CoachItem> = map { summary ->
    CoachItem(
        id = summary.id,
        name = summary.name ?: "Coach",
        campus = summary.schoolName,
        status = summary.status
    )
}

