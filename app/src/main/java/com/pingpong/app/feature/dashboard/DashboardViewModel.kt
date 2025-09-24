package com.pingpong.app.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pingpong.app.core.common.UiState
import com.pingpong.app.core.data.AdminRepository
import com.pingpong.app.core.model.admin.AdminStudentSummary
import com.pingpong.app.core.model.coach.CoachApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class DashboardMetrics(
    val pendingCoachCount: Int = 0,
    val certifiedCoachCount: Int = 0,
    val studentCount: Int = 0
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _metricsState = MutableStateFlow<UiState<DashboardMetrics>>(UiState.Loading)
    val metricsState: StateFlow<UiState<DashboardMetrics>> = _metricsState

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _metricsState.value = UiState.Loading
            val pendingDeferred = async { adminRepository.getCoachRegister() }
            val certifiedDeferred = async { adminRepository.getCertifiedCoaches() }
            val studentsDeferred = async { adminRepository.getStudents() }
            val pendingResult = pendingDeferred.await()
            val certifiedResult = certifiedDeferred.await()
            val studentsResult = studentsDeferred.await()

            pendingResult
                .onSuccess { pending ->
                    certifiedResult
                        .onSuccess { certified ->
                            studentsResult
                                .onSuccess { students ->
                                    _metricsState.update {
                                        UiState.Success(
                                            DashboardMetrics(
                                                pendingCoachCount = pending.size,
                                                certifiedCoachCount = certified.size,
                                                studentCount = students.size
                                            )
                                        )
                                    }
                                }
                                .onFailure { throwable ->
                                    _metricsState.value = UiState.Error(throwable.message ?: "Failed to load students")
                                }
                        }
                        .onFailure { throwable ->
                            _metricsState.value = UiState.Error(throwable.message ?: "Failed to load certified coaches")
                        }
                }
                .onFailure { throwable ->
                    _metricsState.value = UiState.Error(throwable.message ?: "Failed to load pending coaches")
                }
        }
    }
}
