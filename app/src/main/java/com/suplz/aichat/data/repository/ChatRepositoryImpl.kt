package com.suplz.aichat.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.suplz.aichat.data.local.dao.ChatDao
import com.suplz.aichat.data.local.entity.toDomain
import com.suplz.aichat.data.remote.api.GigaChatApi
import com.suplz.aichat.data.remote.dto.ChatRequest
import com.suplz.aichat.data.remote.dto.MessageDto
import com.suplz.aichat.domain.model.Chat
import com.suplz.aichat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao,
    private val gigaChatApi: GigaChatApi
) : ChatRepository {

    override fun getChats(): Flow<PagingData<Chat>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { chatDao.getChatsPaged() }
        ).flow.map { pagingData -> pagingData.map { it.toDomain() } }
    }

    override fun searchChats(query: String): Flow<PagingData<Chat>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { chatDao.searchChatsPaged(query) }
        ).flow.map { pagingData -> pagingData.map { it.toDomain() } }
    }

    override fun getChatTitle(chatId: String): Flow<String?> {
        return chatDao.getChatTitleFlow(chatId)
    }

    override suspend fun updateChatTitle(chatId: String, newTitle: String) {
        chatDao.updateChatTitle(chatId, newTitle, System.currentTimeMillis())
    }

    override suspend fun generateTitle(prompt: String): Result<String> {
        return try {
            val request = ChatRequest(
                model = "GigaChat-Pro",
                messages = listOf(MessageDto(role = "user", content = prompt))
            )

            val response = gigaChatApi.getChatCompletion(request)
            val generatedTitle = response.choices.firstOrNull()?.message?.content?.trim('"', ' ', '\n')

            if (generatedTitle.isNullOrBlank()) {
                Result.failure(IllegalStateException("Generated title is empty"))
            } else {
                Result.success(generatedTitle)
            }
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            Result.failure(e)
        }
    }
}