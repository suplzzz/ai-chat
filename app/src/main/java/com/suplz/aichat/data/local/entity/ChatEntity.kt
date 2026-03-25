package com.suplz.aichat.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.suplz.aichat.domain.model.Chat

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey val id: String,
    val title: String,
    val createdAt: Long,
    val updatedAt: Long
)

fun ChatEntity.toDomain(): Chat = Chat(
    id = id,
    title = title,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Chat.toEntity(): ChatEntity = ChatEntity(
    id = id,
    title = title,
    createdAt = createdAt,
    updatedAt = updatedAt
)