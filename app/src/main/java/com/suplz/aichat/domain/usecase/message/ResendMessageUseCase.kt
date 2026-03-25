package com.suplz.aichat.domain.usecase.message

import com.suplz.aichat.domain.repository.MessageRepository
import javax.inject.Inject

class ResendMessageUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    suspend operator fun invoke(messageId: String): Result<Unit> =
        messageRepository.resendMessage(messageId)
}