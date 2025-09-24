package com.pingpong.app.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pingpong.app.core.common.UiState
import com.pingpong.app.core.data.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject


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
            try {
                val pending = adminRepository.getCoachRegister()
                val certified = adminRepository.getCertifiedCoaches()
                val students = adminRepository.getStudents()

                if (pending.code == 20000 && certified.code == 20000 && students.code == 20000) {
                    _metricsState.value = UiState.Success(
                        DashboardMetrics(
                            pendingCoachCount = pending.data.asArrayCount(),
                            certifiedCoachCount = certified.data.asArrayCount(),
                            studentCount = students.data.asArrayCount()
                        )
                    )
                } else {
                    val message = pending.message ?: certified.message ?: students.message ?: "获取数据失败"
                    _metricsState.value = UiState.Error(message)
                }
            } catch (t: Throwable) {
                _metricsState.value = UiState.Error(t.message)
            }
        }
    }
}

private fun JsonObject?.asArrayCount(): Int = this.asJsonElementCount()

private fun JsonElement?.asJsonElementCount(): Int = when (this) {
    null -> 0
    is JsonArray -> this.size
    is JsonObject -> {
        when {
            this.containsKey("list") -> this["list"].asJsonElementCount()
            this.containsKey("data") -> this["data"].asJsonElementCount()
            else -> this.values.firstOrNull { it is JsonArray }?.asJsonElementCount() ?: 0
        }
    }
    else -> 0
}
