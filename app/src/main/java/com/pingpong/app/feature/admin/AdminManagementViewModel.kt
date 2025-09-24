package com.pingpong.app.feature.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pingpong.app.core.common.UiState
import com.pingpong.app.core.data.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive


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
            try {
                val pending = adminRepository.getCoachRegister()
                val certified = adminRepository.getCertifiedCoaches()

                if (pending.code == 20000 && certified.code == 20000) {
                    _uiState.value = UiState.Success(
                        AdminManagementState(
                            pendingCoaches = pending.data.parseCoachList(),
                            certifiedCoaches = certified.data.parseCoachList()
                        )
                    )
                } else {
                    _uiState.value = UiState.Error(pending.message ?: certified.message ?: "Failed to load coach list")
                }
            } catch (t: Throwable) {
                _uiState.value = UiState.Error(t.message)
            }
        }
    }
}

private fun JsonObject?.parseCoachList(): List<CoachItem> {
    val array: JsonArray? = when {
        this == null -> null
        this.containsKey("list") -> this["list"] as? JsonArray
        this.containsKey("data") -> this["data"] as? JsonArray
        else -> this.values.firstOrNull { it is JsonArray } as? JsonArray
    }

    return array?.mapNotNull { element ->
        val obj = element as? JsonObject ?: return@mapNotNull null
        val id = obj["id"]?.jsonPrimitive?.longOrNullValue
            ?: obj["coachId"]?.jsonPrimitive?.longOrNullValue
            ?: return@mapNotNull null
        val name = obj["realName"]?.jsonPrimitive?.contentOrNullValue
            ?: obj["name"]?.jsonPrimitive?.contentOrNullValue
            ?: "Unknown coach"
        val campus = obj["schoolName"]?.jsonPrimitive?.contentOrNullValue
            ?: obj["campusName"]?.jsonPrimitive?.contentOrNullValue
        val status = obj["status"]?.jsonPrimitive?.contentOrNullValue
        CoachItem(id = id, name = name, campus = campus, status = status)
    } ?: emptyList()
}

private val JsonPrimitive.longOrNullValue: Long?
    get() = content.toLongOrNull()

private val JsonPrimitive.contentOrNullValue: String?
    get() = if (isString) content else runCatching { content }.getOrNull()

