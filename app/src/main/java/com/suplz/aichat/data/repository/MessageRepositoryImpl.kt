package com.suplz.aichat.data.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.suplz.aichat.data.local.dao.ChatDao
import com.suplz.aichat.data.local.dao.MessageDao
import com.suplz.aichat.data.local.entity.ChatEntity
import com.suplz.aichat.data.local.entity.MessageEntity
import com.suplz.aichat.data.local.entity.toDomain
import com.suplz.aichat.data.remote.api.GigaChatApi
import com.suplz.aichat.data.remote.dto.ChatRequest
import com.suplz.aichat.data.remote.dto.MessageDto
import com.suplz.aichat.domain.model.Message
import com.suplz.aichat.domain.repository.MessageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class MessageRepositoryImpl @Inject constructor(
    private val messageDao: MessageDao,
    private val chatDao: ChatDao,
    private val gigaChatApi: GigaChatApi,
    @param:ApplicationContext private val context: Context
) : MessageRepository {

    override fun getMessages(chatId: String): Flow<PagingData<Message>> {
        return Pager(
            config = PagingConfig(pageSize = 30, enablePlaceholders = false),
            pagingSourceFactory = { messageDao.getMessagesPaged(chatId) }
        ).flow.map { pagingData -> pagingData.map { it.toDomain() } }
    }

    override suspend fun sendMessage(chatId: String, text: String, isNewChat: Boolean): Result<Unit> {
        val currentTime = System.currentTimeMillis()

        if (isNewChat) {
            val newChat = ChatEntity(id = chatId, title = "Новый чат", createdAt = currentTime, updatedAt = currentTime)
            chatDao.insertChatIfNotExists(newChat)
        }

        val userMessage = MessageEntity(
            id = UUID.randomUUID().toString(),
            chatId = chatId,
            text = text,
            author = Message.Author.USER,
            type = Message.MessageType.TEXT,
            createdAt = currentTime,
            status = Message.MessageStatus.SENT
        )
        messageDao.insertMessage(userMessage)

        val aiMessageId = UUID.randomUUID().toString()
        val aiMessage = MessageEntity(
            id = aiMessageId,
            chatId = chatId,
            text = "",
            author = Message.Author.AI,
            type = Message.MessageType.TEXT,
            createdAt = currentTime + 1,
            status = Message.MessageStatus.SENDING
        )
        messageDao.insertMessage(aiMessage)

        chatDao.updateChatTimestamp(chatId, currentTime)

        return try {
            val historyEntities = messageDao.getRecentMessagesSync(chatId).reversed()
            val dtos = historyEntities
                .filter { it.status == Message.MessageStatus.SENT }
                .map { entity ->
                    val role = if (entity.author == Message.Author.USER) "user" else "assistant"
                    MessageDto(role = role, content = entity.text)
                }

            val request = ChatRequest(model = "GigaChat-Pro", messages = dtos)

            val response = gigaChatApi.getChatCompletion(request)
            val aiResponseText = response.choices.firstOrNull()?.message?.content ?: throw IllegalStateException("Empty response from GigaChat")

            handleAndSaveAiResponse(aiMessageId, aiResponseText)
            Result.success(Unit)
        } catch (e: Exception) {
            // КЛЮЧЕВОЙ ФИКС: Ловим отмену и выставляем ERROR
            if (e is CancellationException) {
                messageDao.updateMessageStatus(aiMessageId, Message.MessageStatus.ERROR)
                throw e
            }
            messageDao.updateMessageStatus(aiMessageId, Message.MessageStatus.ERROR)
            Result.failure(e)
        }
    }

    override suspend fun resendMessage(messageId: String): Result<Unit> {
        val failedAiMessage = messageDao.getMessageById(messageId) ?: return Result.failure(IllegalArgumentException("Message not found"))

        if (failedAiMessage.author != Message.Author.AI || failedAiMessage.status != Message.MessageStatus.ERROR) {
            return Result.failure(IllegalStateException("Can only resend failed AI messages"))
        }

        messageDao.updateMessageStatus(messageId, Message.MessageStatus.SENDING)

        return try {
            val historyEntities = messageDao.getRecentMessagesSync(failedAiMessage.chatId).reversed()

            val dtos = historyEntities
                .filter { it.createdAt < failedAiMessage.createdAt && it.status == Message.MessageStatus.SENT }
                .map { entity ->
                    val role = if (entity.author == Message.Author.USER) "user" else "assistant"
                    MessageDto(role = role, content = entity.text)
                }

            val request = ChatRequest(model = "GigaChat-Pro", messages = dtos)

            val response = gigaChatApi.getChatCompletion(request)
            val aiResponseText = response.choices.firstOrNull()?.message?.content ?: throw IllegalStateException("Empty response from GigaChat")

            handleAndSaveAiResponse(messageId, aiResponseText)
            Result.success(Unit)
        } catch (e: Exception) {
            // КЛЮЧЕВОЙ ФИКС: Ловим отмену и выставляем ERROR
            if (e is CancellationException) {
                messageDao.updateMessageStatus(messageId, Message.MessageStatus.ERROR)
                throw e
            }
            messageDao.updateMessageStatus(messageId, Message.MessageStatus.ERROR)
            Result.failure(e)
        }
    }

    private suspend fun handleAndSaveAiResponse(messageId: String, aiResponseText: String) {
        var finalText = aiResponseText
        var finalImageUrl: String? = null
        var finalType = Message.MessageType.TEXT

        val imgRegex = "<img\\s+src=\"([^\"]+)\"".toRegex()
        val match = imgRegex.find(aiResponseText)

        if (match != null) {
            val fileId = match.groupValues[1]
            try {
                val responseBody = gigaChatApi.getFileContent(fileId)
                val file = File(context.filesDir, "$fileId.jpg")
                file.writeBytes(responseBody.bytes())
                finalImageUrl = file.absolutePath
                finalType = Message.MessageType.IMAGE
                finalText = aiResponseText.replace(Regex("<img[^>]*>"), "").trim()
            } catch (e: Exception) { /* Игнор */ }
        }

        messageDao.updateMessageContent(
            messageId = messageId,
            text = finalText,
            imageUrl = finalImageUrl,
            type = finalType,
            status = Message.MessageStatus.SENT
        )
    }
}