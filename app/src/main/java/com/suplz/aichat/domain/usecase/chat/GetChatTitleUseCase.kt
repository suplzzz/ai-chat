package com.suplz.aichat.domain.usecase.chat

import com.suplz.aichat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatTitleUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(chatId: String): Flow<String?> {
        return chatRepository.getChatTitle(chatId)
    }
}