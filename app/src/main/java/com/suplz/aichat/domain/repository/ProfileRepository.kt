package com.suplz.aichat.domain.repository

import com.suplz.aichat.domain.model.TokenBalances
import com.suplz.aichat.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfile(): Flow<UserProfile?>
    suspend fun updateProfile(name: String, phone: String, localImageUri: String?): Result<Unit>
    suspend fun getBalanceTokens(): Result<TokenBalances>
}