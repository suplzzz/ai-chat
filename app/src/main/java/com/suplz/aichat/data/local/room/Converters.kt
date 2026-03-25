package com.suplz.aichat.data.local.room

import androidx.room.TypeConverter
import com.suplz.aichat.domain.model.Message

class Converters {
    @TypeConverter
    fun fromAuthor(author: Message.Author): String = author.name

    @TypeConverter
    fun toAuthor(value: String): Message.Author = Message.Author.valueOf(value)

    @TypeConverter
    fun fromMessageType(type: Message.MessageType): String = type.name

    @TypeConverter
    fun toMessageType(value: String): Message.MessageType = Message.MessageType.valueOf(value)

    @TypeConverter
    fun fromMessageStatus(status: Message.MessageStatus): String = status.name

    @TypeConverter
    fun toMessageStatus(value: String): Message.MessageStatus = Message.MessageStatus.valueOf(value)
}