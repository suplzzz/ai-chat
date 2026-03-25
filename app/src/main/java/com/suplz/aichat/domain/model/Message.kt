package com.suplz.aichat.domain.model

data class Message(
    val id: String,
    val chatId: String,
    val text: String,
    val imageUrl: String? = null,
    val author: Author,
    val type: MessageType,
    val createdAt: Long,
    val status: MessageStatus
) {
    enum class Author {
        USER, AI
    }

    enum class MessageType {
        TEXT, IMAGE, ERROR
    }

    enum class MessageStatus {
        SENDING, SENT, ERROR
    }
}