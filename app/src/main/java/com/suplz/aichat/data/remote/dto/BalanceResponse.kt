package com.suplz.aichat.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class BalanceResponse(
    @SerialName("balance") val balance: List<BalanceItem>
)

@Serializable
data class BalanceItem(
    @SerialName("usage") val usage: String,
    @SerialName("value") val value: Int
)