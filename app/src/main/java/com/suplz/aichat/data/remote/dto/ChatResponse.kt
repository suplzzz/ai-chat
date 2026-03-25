package com.suplz.aichat.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatResponse(
    @SerialName("choices") val choices: List<ChoiceDto>,
    @SerialName("created") val created: Long,
    @SerialName("model") val model: String
)