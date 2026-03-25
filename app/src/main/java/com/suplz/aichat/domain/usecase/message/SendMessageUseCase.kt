package com.suplz.aichat.domain.usecase.message

import com.suplz.aichat.domain.repository.MessageRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    suspend operator fun invoke(chatId: String, text: String, isNewChat: Boolean): Result<Unit> {
        return messageRepository.sendMessage(chatId, text, isNewChat)
    }
}