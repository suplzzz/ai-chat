package com.suplz.aichat.domain.usecase.settings

import com.suplz.aichat.domain.model.AppTheme
import com.suplz.aichat.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetThemeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<AppTheme> = settingsRepository.getTheme()
}