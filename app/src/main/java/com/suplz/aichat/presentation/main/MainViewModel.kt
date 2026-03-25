package com.suplz.aichat.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suplz.aichat.domain.model.AppTheme
import com.suplz.aichat.domain.usecase.auth.CheckAuthStatusUseCase
import com.suplz.aichat.domain.usecase.settings.GetThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getThemeUseCase: GetThemeUseCase,
    checkAuthStatusUseCase: CheckAuthStatusUseCase
) : ViewModel() {

    val theme: StateFlow<AppTheme> = getThemeUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppTheme.SYSTEM_DEFAULT
    )

    val startDestination: String = if (checkAuthStatusUseCase()) {
        Screen.ChatList.route
    } else {
        Screen.Auth.route
    }
}