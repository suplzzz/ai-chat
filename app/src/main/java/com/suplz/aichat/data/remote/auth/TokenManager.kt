package com.suplz.aichat.data.remote.auth

import com.suplz.aichat.data.remote.api.GigaChatAuthApi
import com.suplz.aichat.BuildConfig
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    private val authApi: GigaChatAuthApi
) {
    private val authKey = BuildConfig.GIGACHAT_AUTH_KEY

    private var currentToken: String? = null
    private var expiresAt: Long = 0
    private val mutex = Mutex()

    suspend fun getValidToken(): String {
        mutex.withLock {
            val currentTime = System.currentTimeMillis()
            if (currentToken == null || expiresAt - currentTime < 60_000) {
                val rqUid = UUID.randomUUID().toString()
                val response = authApi.getToken(
                    authHeader = "Basic $authKey",
                    rqUid = rqUid
                )
                currentToken = response.accessToken
                expiresAt = response.expiresAt
            }
            return currentToken ?: throw IllegalStateException("Failed to get token")
        }
    }
}