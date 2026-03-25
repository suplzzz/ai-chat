package com.suplz.aichat.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChoiceDto(
    @SerialName("message") val message: MessageDto,
    @SerialName("index") val index: Int,
    @SerialName("finish_reason") val finishReason: String? = null
)