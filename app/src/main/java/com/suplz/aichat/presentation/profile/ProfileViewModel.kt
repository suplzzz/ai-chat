package com.suplz.aichat.presentation.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suplz.aichat.R
import com.suplz.aichat.domain.model.AppTheme
import com.suplz.aichat.domain.usecase.auth.LogoutUseCase
import com.suplz.aichat.domain.usecase.profile.GetBalanceUseCase
import com.suplz.aichat.domain.usecase.profile.GetProfileUseCase
import com.suplz.aichat.domain.usecase.profile.UpdateProfileUseCase
import com.suplz.aichat.domain.usecase.settings.GetThemeUseCase
import com.suplz.aichat.domain.usecase.settings.SetThemeUseCase
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
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val getThemeUseCase: GetThemeUseCase,
    private val setThemeUseCase: SetThemeUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getBalanceUseCase: GetBalanceUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    private val _events = Channel<ProfileEvent>()
    val events = _events.receiveAsFlow()

    init {
        observeProfile()
        observeTheme()
        fetchBalance()
    }

    fun onAction(action: ProfileAction) {
        when (action) {
            is ProfileAction.OnThemeSelected -> setTheme(action.theme)
            is ProfileAction.OnAvatarSelected -> uploadAvatar(action.uri)
            ProfileAction.OnLogoutClick -> logout()
            ProfileAction.OnEditClick -> _state.update {
                it.copy(isEditing = true, editName = it.name, editPhone = it.phone)
            }
            is ProfileAction.OnNameChange -> _state.update { it.copy(editName = action.name) }
            is ProfileAction.OnPhoneChange -> _state.update { it.copy(editPhone = action.phone) }
            ProfileAction.OnSaveClick -> saveProfile()
        }
    }

    private fun observeProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getProfileUseCase().collect { profile ->
                if (profile != null) {
                    _state.update {
                        it.copy(
                            email = profile.email,
                            name = profile.name,
                            phone = profile.phone ?: "",
                            photoUrl = profile.photoUrl,
                            isLoading = false
                        )
                    }
                } else {
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    private fun fetchBalance() {
        viewModelScope.launch {
            getBalanceUseCase().fold(
                onSuccess = { balances ->
                    _state.update { it.copy(tokensLite = balances.lite, tokensPro = balances.pro, isTokensError = false) }
                },
                onFailure = {
                    _state.update { it.copy(isTokensError = true) }
                }
            )
        }
    }

    private fun saveProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, isEditing = false) }
            val currentUri = _state.value.photoUrl
            val result = updateProfileUseCase(
                name = _state.value.editName,
                phone = _state.value.editPhone,
                localImageUri = currentUri
            )

            _state.update { it.copy(isLoading = false) }

            result.onFailure {
                _events.send(ProfileEvent.ShowError(UiText.StringResource(R.string.error_unknown)))
            }
        }
    }

    private fun uploadAvatar(uri: Uri) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = updateProfileUseCase(
                name = _state.value.name,
                phone = _state.value.phone,
                localImageUri = uri.toString()
            )

            _state.update { it.copy(isLoading = false) }

            result.onFailure {
                _events.send(ProfileEvent.ShowError(UiText.StringResource(R.string.error_unknown)))
            }
        }
    }

    private fun observeTheme() {
        viewModelScope.launch {
            getThemeUseCase().collect { theme ->
                _state.update { it.copy(theme = theme) }
            }
        }
    }

    private fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            setThemeUseCase(theme)
        }
    }

    private fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _events.send(ProfileEvent.NavigateToAuth)
        }
    }
}