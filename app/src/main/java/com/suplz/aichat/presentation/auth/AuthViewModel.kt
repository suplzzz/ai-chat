package com.suplz.aichat.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suplz.aichat.R
import com.suplz.aichat.domain.usecase.auth.LoginUseCase
import com.suplz.aichat.domain.usecase.auth.RegisterUseCase
import com.suplz.aichat.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    private val _events = Channel<AuthEvent>()
    val events = _events.receiveAsFlow()

    private var lastActionWasLogin: Boolean = true

    fun onAction(action: AuthAction) {
        when (action) {
            is AuthAction.OnEmailChange -> _state.update { it.copy(email = action.email, emailError = null) }
            is AuthAction.OnPasswordChange -> _state.update { it.copy(password = action.password, passwordError = null) }
            AuthAction.OnLoginClick -> performAuth(isLogin = true)
            AuthAction.OnRegisterClick -> performAuth(isLogin = false)
            AuthAction.OnRetryClick -> performAuth(isLogin = lastActionWasLogin)
        }
    }

    private fun validateInput(): Boolean {
        val email = state.value.email.trim()
        val password = state.value.password.trim()
        var isValid = true

        val emailError = if (email.isBlank()) {
            isValid = false
            UiText.StringResource(R.string.error_empty_field)
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            isValid = false
            UiText.StringResource(R.string.error_invalid_email)
        } else null

        val passwordError = if (password.isBlank()) {
            isValid = false
            UiText.StringResource(R.string.error_empty_field)
        } else if (password.length < 6) {
            isValid = false
            UiText.StringResource(R.string.error_short_password)
        } else null

        _state.update { it.copy(emailError = emailError, passwordError = passwordError) }
        return isValid
    }

    private fun performAuth(isLogin: Boolean) {
        lastActionWasLogin = isLogin

        if (!validateInput()) return

        val email = state.value.email.trim()
        val password = state.value.password.trim()

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = if (isLogin) {
                loginUseCase(email, password)
            } else {
                registerUseCase(email, password)
            }

            _state.update { it.copy(isLoading = false) }

            result.onSuccess {
                sendEvent(AuthEvent.NavigateToChatList)
            }.onFailure { error ->
                val msg = error.message ?: ""
                val isNetworkError = error is java.io.IOException ||
                        error.javaClass.name.contains("NetworkException") ||
                        msg.contains("Connection closed", ignoreCase = true) ||
                        msg.contains("network", ignoreCase = true) ||
                        msg.contains("timeout", ignoreCase = true) ||
                        msg.contains("host", ignoreCase = true)

                val errorMessage = if (isNetworkError) {
                    UiText.StringResource(R.string.error_no_internet)
                } else {
                    UiText.StringResource(R.string.error_auth, error.localizedMessage ?: "")
                }

                sendEvent(AuthEvent.ShowError(errorMessage, isNetworkError))
            }
        }
    }

    private fun sendEvent(event: AuthEvent) {
        viewModelScope.launch { _events.send(event) }
    }
}