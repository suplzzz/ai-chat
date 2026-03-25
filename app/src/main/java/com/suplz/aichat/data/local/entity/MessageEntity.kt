package com.suplz.aichat.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.suplz.aichat.domain.model.Message

@Entity(
    tableName = "messages",
    foreignKeys =[
        ForeignKey(
            entity = ChatEntity::class,
            parentColumns = ["id"],
            childColumns = ["chatId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("chatId")]
)
data class MessageEntity(
    @PrimaryKey val id: String,
    val chatId: String,
    val text: String,
    val imageUrl: String? = null,
    val author: Message.Author,
    val type: Message.MessageType,
    val createdAt: Long,
    val status: Message.MessageStatus
)

fun MessageEntity.toDomain(): Message = Message(
    id = id,
    chatId = chatId,
    text = text,
    imageUrl = imageUrl,
    author = author,
    type = type,
    createdAt = createdAt,
    status = status
)

fun Message.toEntity(): MessageEntity = MessageEntity(
    id = id,
    chatId = chatId,
    text = text,
    imageUrl = imageUrl,
    author = author,
    type = type,
    createdAt = createdAt,
    status = status
)