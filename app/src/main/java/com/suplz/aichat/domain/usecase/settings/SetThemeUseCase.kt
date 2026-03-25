package com.suplz.aichat.domain.usecase.settings

import com.suplz.aichat.domain.model.AppTheme
import com.suplz.aichat.domain.repository.SettingsRepository
import javax.inject.Inject

class SetThemeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(theme: AppTheme) = settingsRepository.setTheme(theme)
}