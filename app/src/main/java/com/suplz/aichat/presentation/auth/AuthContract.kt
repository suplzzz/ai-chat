package com.suplz.aichat.presentation.auth

import com.suplz.aichat.presentation.util.UiText

data class AuthState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val emailError: UiText? = null,
    val passwordError: UiText? = null
)

sealed interface AuthAction {
    data class OnEmailChange(val email: String) : AuthAction
    data class OnPasswordChange(val password: String) : AuthAction
    object OnLoginClick : AuthAction
    object OnRegisterClick : AuthAction
    object OnRetryClick : AuthAction
}

sealed interface AuthEvent {
    object NavigateToChatList : AuthEvent
    data class ShowError(val message: UiText, val isNetworkError: Boolean = false) : AuthEvent
}