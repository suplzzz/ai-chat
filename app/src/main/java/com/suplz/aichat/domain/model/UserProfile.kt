package com.suplz.aichat.domain.model

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val phone: String?,
    val photoUrl: String?,
    val tokensCount: Int
)