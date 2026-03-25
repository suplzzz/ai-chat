package com.suplz.aichat.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.suplz.aichat.data.remote.api.GigaChatApi
import com.suplz.aichat.data.remote.cloud.YandexS3Uploader
import com.suplz.aichat.domain.model.TokenBalances
import com.suplz.aichat.domain.model.UserProfile
import com.suplz.aichat.domain.repository.ProfileRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val s3Uploader: YandexS3Uploader,
    private val gigaChatApi: GigaChatApi
) : ProfileRepository {

    override fun getProfile(): Flow<UserProfile?> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val documentRef = firestore.collection("users").document(userId)

        val subscription = documentRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val profile = UserProfile(
                    id = snapshot.getString("id") ?: userId,
                    name = snapshot.getString("name") ?: "",
                    email = snapshot.getString("email") ?: "",
                    phone = snapshot.getString("phone") ?: "",
                    photoUrl = snapshot.getString("photoUrl"),
                    tokensCount = 0
                )
                trySend(profile)
            } else {
                trySend(null)
            }
        }

        awaitClose { subscription.remove() }
    }

    override suspend fun updateProfile(name: String, phone: String, localImageUri: String?): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
            val updates = mutableMapOf<String, Any>(
                "name" to name,
                "phone" to phone
            )

            if (localImageUri != null && !localImageUri.startsWith("http")) {
                val uploadedUrl = s3Uploader.uploadImage(localImageUri)
                updates["photoUrl"] = uploadedUrl
            }

            firestore.collection("users").document(userId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            Result.failure(e)
        }
    }

    override suspend fun getBalanceTokens(): Result<TokenBalances> {
        return try {
            val response = gigaChatApi.getBalance()
            val lite = response.balance.firstOrNull { it.usage == "GigaChat" }?.value ?: 0
            val pro = response.balance.firstOrNull { it.usage == "GigaChat-Pro" }?.value ?: 0
            Result.success(TokenBalances(lite = lite, pro = pro))
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            Result.failure(e)
        }
    }
}