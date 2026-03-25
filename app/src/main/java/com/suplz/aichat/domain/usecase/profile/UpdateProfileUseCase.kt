package com.suplz.aichat.domain.usecase.profile

import com.suplz.aichat.domain.repository.ProfileRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(name: String, phone: String, localImageUri: String?): Result<Unit> {
        return profileRepository.updateProfile(name, phone, localImageUri)
    }
}