package com.suplz.aichat.domain.usecase.profile

import com.suplz.aichat.domain.model.TokenBalances
import com.suplz.aichat.domain.repository.ProfileRepository
import javax.inject.Inject

class GetBalanceUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(): Result<TokenBalances> = profileRepository.getBalanceTokens()
}