package com.pingpong.app.feature.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pingpong.app.core.common.UiState
import com.pingpong.app.core.data.AuthRepository
import com.pingpong.app.core.model.LoginRequest
import com.pingpong.app.core.model.LoginResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class UserRole(val key: String, val displayName: String) {
    CAMPUS_ADMIN("campus_admin", "Campus Admin"),
    COACH("coach", "Coach"),
    STUDENT("student", "Student");

    companion object {
        fun fromDisplayName(display: String): UserRole? = entries.firstOrNull { it.displayName == display }
    }
}

data class LoginFormState(
    val username: String = "",
    val password: String = "",
    val role: UserRole = UserRole.STUDENT,
    val rememberMe: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isValid: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _formState = MutableStateFlow(LoginFormState())
    val formState: StateFlow<LoginFormState> = _formState

    private val _loginState = MutableStateFlow<UiState<LoginResponse>>(UiState.Idle)
    val loginState: StateFlow<UiState<LoginResponse>> = _loginState

    fun onUsernameChanged(value: String) {
        _formState.update { it.copy(username = value.trim()) }
        validate()
    }

    fun onPasswordChanged(value: String) {
        _formState.update { it.copy(password = value) }
        validate()
    }

    fun onRoleSelected(role: UserRole) {
        _formState.update { it.copy(role = role) }
        validate()
    }

    fun togglePasswordVisibility() {
        _formState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    private fun validate() {
        _formState.update { state ->
            state.copy(isValid = state.username.isNotBlank() && state.password.length >= 6)
        }
    }

    fun login() {
        val form = _formState.value
        if (!form.isValid) return

        viewModelScope.launch {
            _loginState.value = UiState.Loading
            try {
                val response = authRepository.login(
                    LoginRequest(
                        username = form.username,
                        password = form.password,
                        role = form.role.key
                    )
                )
                if (response.code == 20000) {
                    _loginState.value = UiState.Success(
                        response.data ?: LoginResponse(token = "")
                    )
                } else {
                    _loginState.value = UiState.Error(response.message)
                }
            } catch (t: Throwable) {
                _loginState.value = UiState.Error(t.message)
            }
        }
    }

    fun resetState() {
        _loginState.value = UiState.Idle
    }
}
