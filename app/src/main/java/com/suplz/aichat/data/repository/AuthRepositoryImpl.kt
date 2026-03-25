package com.suplz.aichat.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.suplz.aichat.data.local.room.AppDatabase
import com.suplz.aichat.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val database: AppDatabase
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(email: String, password: String): Result<Unit> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid

            if (userId != null) {
                val initialProfile = mapOf(
                    "id" to userId,
                    "email" to email,
                    "name" to email.substringBefore("@"),
                    "phone" to null,
                    "photoUrl" to null,
                    "tokensCount" to 0
                )
                firestore.collection("users").document(userId).set(initialProfile).await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        auth.signOut()
        withContext(Dispatchers.IO) {
            database.clearAllTables()
        }
    }

    override fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}