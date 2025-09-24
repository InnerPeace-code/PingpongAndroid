package com.pingpong.app.feature.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pingpong.app.core.common.UiState
import com.pingpong.app.core.common.asJsonArrayOrNull
import com.pingpong.app.core.common.asJsonObjectOrNull
import com.pingpong.app.core.common.stringOrNull
import com.pingpong.app.core.data.AuthRepository
import com.pingpong.app.core.data.CampusRepository
import com.pingpong.app.core.model.SchoolOption
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

enum class RegisterRole(val key: String, val displayName: String) {
    STUDENT("student", "Student"),
    COACH("coach", "Coach")
}

data class RegisterFormState(
    val role: RegisterRole = RegisterRole.STUDENT,
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val realName: String = "",
    val gender: String = "",
    val age: String = "",
    val campusId: Long? = null,
    val phone: String = "",
    val email: String = "",
    val achievements: String = "",
    val photoPath: String? = null,
    val isValid: Boolean = false
)

data class SchoolSelectionState(
    val schools: List<SchoolOption> = emptyList(),
    val loading: Boolean = false
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val campusRepository: CampusRepository
) : ViewModel() {

    private val _formState = MutableStateFlow(RegisterFormState())
    val formState: StateFlow<RegisterFormState> = _formState

    private val _schoolState = MutableStateFlow(SchoolSelectionState(loading = true))
    val schoolState: StateFlow<SchoolSelectionState> = _schoolState

    private val _submitState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val submitState: StateFlow<UiState<Unit>> = _submitState

    init {
        fetchSchools()
    }

    private fun fetchSchools() {
        viewModelScope.launch {
            _schoolState.update { it.copy(loading = true) }
            try {
                val response = campusRepository.fetchSchoolOptions()
                if (response.code == 20000) {
                    val schools = response.data.parseSchools()
                    _schoolState.value = SchoolSelectionState(schools = schools, loading = false)
                } else {
                    _schoolState.value = SchoolSelectionState(emptyList(), loading = false)
                }
            } catch (_: Throwable) {
                _schoolState.value = SchoolSelectionState(emptyList(), loading = false)
            }
        }
    }

    fun updateRole(role: RegisterRole) {
        _formState.update { it.copy(role = role) }
        validate()
    }

    fun updateUsername(value: String) {
        _formState.update { it.copy(username = value.trim()) }
        validate()
    }

    fun updatePassword(value: String) {
        _formState.update { it.copy(password = value) }
        validate()
    }

    fun updateConfirmPassword(value: String) {
        _formState.update { it.copy(confirmPassword = value) }
        validate()
    }

    fun updateRealName(value: String) {
        _formState.update { it.copy(realName = value) }
        validate()
    }

    fun updateGender(value: String) {
        _formState.update { it.copy(gender = value) }
        validate()
    }

    fun updateAge(value: String) {
        _formState.update { it.copy(age = value) }
        validate()
    }

    fun updateCampus(id: Long?) {
        _formState.update { it.copy(campusId = id) }
        validate()
    }

    fun updatePhone(value: String) {
        _formState.update { it.copy(phone = value) }
        validate()
    }

    fun updateEmail(value: String) {
        _formState.update { it.copy(email = value) }
        validate()
    }

    fun updateAchievements(value: String) {
        _formState.update { it.copy(achievements = value) }
        validate()
    }

    fun updatePhoto(path: String?) {
        _formState.update { it.copy(photoPath = path) }
    }

    fun register() {
        val form = _formState.value
        if (!form.isValid) return

        viewModelScope.launch {
            _submitState.value = UiState.Loading
            try {
                val payload = buildRegistrationPayload(form)
                val response = authRepository.register(form.role.key, payload)
                if (response.code == 20000) {
                    _submitState.value = UiState.Success(Unit)
                } else {
                    _submitState.value = UiState.Error(response.message)
                }
            } catch (t: Throwable) {
                _submitState.value = UiState.Error(t.message)
            }
        }
    }

    fun resetSubmitState() {
        _submitState.value = UiState.Idle
    }

    private fun validate() {
        _formState.update { state ->
            val baseValid = state.username.length >= 4 &&
                state.password.length >= 6 &&
                state.password == state.confirmPassword &&
                state.realName.isNotBlank() &&
                state.gender.isNotBlank() &&
                state.campusId != null &&
                state.phone.length >= 6
            val ageValid = state.age.toIntOrNull()?.let { it in 4..100 } ?: false
            val coachValid = if (state.role == RegisterRole.COACH) {
                state.achievements.isNotBlank()
            } else true
            state.copy(isValid = baseValid && coachValid && ageValid)
        }
    }

    private fun buildRegistrationPayload(form: RegisterFormState): JsonObject {
        val isMale = form.gender.equals("male", ignoreCase = true) || form.gender.equals("m", ignoreCase = true)
        val ageValue = form.age.toIntOrNull()
        return buildJsonObject {
            put("username", form.username)
            put("password", form.password)
            put("name", form.realName)
            put("isMale", JsonPrimitive(isMale))
            ageValue?.let { put("age", JsonPrimitive(it)) }
            form.campusId?.let { put("schoolId", JsonPrimitive(it)) }
            put("phone", form.phone)
            if (form.email.isNotBlank()) {
                put("email", form.email)
            }
            if (form.role == RegisterRole.COACH) {
                put("description", form.achievements)
                form.photoPath?.let { put("photoPath", it) }
            }
        }
    }
}

private fun JsonElement?.parseSchools(): List<SchoolOption> {
    val root = this?.asJsonObjectOrNull() ?: return emptyList()
    val candidate: JsonArray? = when {
        root.containsKey("data") -> root["data"]?.asJsonArrayOrNull()
        root.containsKey("list") -> root["list"]?.asJsonArrayOrNull()
        else -> root.values.firstOrNull { it.asJsonArrayOrNull() != null }?.asJsonArrayOrNull()
    }
    return candidate?.mapNotNull { element ->
        val obj = element.asJsonObjectOrNull() ?: return@mapNotNull null
        val id = obj.stringOrNull("id")?.toLongOrNull()
            ?: obj.stringOrNull("schoolId")?.toLongOrNull()
            ?: return@mapNotNull null
        val name = obj.stringOrNull("schoolname")
            ?: obj.stringOrNull("name")
            ?: obj.stringOrNull("label")
            ?: return@mapNotNull null
        SchoolOption(id = id, name = name)
    } ?: emptyList()
}
