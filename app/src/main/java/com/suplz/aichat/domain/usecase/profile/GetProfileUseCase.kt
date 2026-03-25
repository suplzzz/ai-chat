package com.suplz.aichat.domain.usecase.profile

import com.suplz.aichat.domain.model.UserProfile
import com.suplz.aichat.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke(): Flow<UserProfile?> = profileRepository.getProfile()
}