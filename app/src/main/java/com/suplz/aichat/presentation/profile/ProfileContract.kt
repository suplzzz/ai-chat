package com.suplz.aichat.presentation.profile

import android.net.Uri
import com.suplz.aichat.domain.model.AppTheme
import com.suplz.aichat.presentation.util.UiText

data class ProfileState(
    val email: String = "",
    val name: String = "",
    val phone: String = "",
    val photoUrl: String? = null,
    val tokensLite: Int = 0,
    val tokensPro: Int = 0,
    val isTokensError: Boolean = false,
    val theme: AppTheme = AppTheme.SYSTEM_DEFAULT,
    val isLoading: Boolean = false,
    val isEditing: Boolean = false,
    val editName: String = "",
    val editPhone: String = ""
)

sealed interface ProfileAction {
    data class OnThemeSelected(val theme: AppTheme) : ProfileAction
    data class OnAvatarSelected(val uri: Uri) : ProfileAction
    object OnLogoutClick : ProfileAction
    object OnEditClick : ProfileAction
    data class OnNameChange(val name: String) : ProfileAction
    data class OnPhoneChange(val phone: String) : ProfileAction
    object OnSaveClick : ProfileAction
}

sealed interface ProfileEvent {
    object NavigateToAuth : ProfileEvent
    data class ShowError(val message: UiText) : ProfileEvent
}