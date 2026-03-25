package com.suplz.aichat.presentation.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.suplz.aichat.R
import com.suplz.aichat.domain.model.Message
import com.suplz.aichat.domain.usecase.chat.GenerateChatTitleUseCase
import com.suplz.aichat.domain.usecase.chat.GetChatTitleUseCase
import com.suplz.aichat.domain.usecase.message.GetMessagesUseCase
import com.suplz.aichat.domain.usecase.message.ResendMessageUseCase
import com.suplz.aichat.domain.usecase.message.SendMessageUseCase
import com.suplz.aichat.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val resendMessageUseCase: ResendMessageUseCase,
    private val generateChatTitleUseCase: GenerateChatTitleUseCase,
    private val getChatTitleUseCase: GetChatTitleUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val chatId: String = checkNotNull(savedStateHandle["chatId"])
    private val isNewChat: Boolean = savedStateHandle["isNew"] ?: false

    private val _state = MutableStateFlow(ChatState(chatId = chatId, inputText = savedStateHandle["inputText"] ?: ""))
    val state = _state.asStateFlow()

    private val _events = Channel<ChatEvent>()
    val events = _events.receiveAsFlow()

    val messagesPagingFlow: Flow<PagingData<Message>> = getMessagesUseCase(chatId)
        .cachedIn(viewModelScope)

    private var isFirstMessageInSession = isNewChat
    private var messageJob: Job? = null

    init {
        viewModelScope.launch {
            getChatTitleUseCase(chatId).collect { fetchedTitle ->
                if (fetchedTitle != null) {
                    _state.update { it.copy(title = fetchedTitle) }
                } else if (isNewChat) {
                    _state.update { it.copy(title = "Новый чат") }
                }
            }
        }
    }

    fun onAction(action: ChatAction) {
        when (action) {
            is ChatAction.OnInputTextChanged -> {
                _state.update { it.copy(inputText = action.text) }
                savedStateHandle["inputText"] = action.text
            }
            ChatAction.OnSendMessage -> sendMessage()
            is ChatAction.OnResendMessage -> resendMessage(action.messageId)
        }
    }

    private fun sendMessage() {
        val text = _state.value.inputText.trim()
        if (text.isBlank() || _state.value.isSending) return

        _state.update { it.copy(inputText = "", isSending = true) }
        savedStateHandle["inputText"] = ""

        messageJob = viewModelScope.launch {
            val result = sendMessageUseCase(chatId, text, isFirstMessageInSession)

            if (isFirstMessageInSession && result.isSuccess) {
                isFirstMessageInSession = false
                generateChatTitleUseCase(chatId, text)
            }

            if (result.isFailure) {
                _events.send(ChatEvent.ShowError(UiText.StringResource(R.string.error_unknown)))
            }

            _state.update { it.copy(isSending = false) }
        }
    }

    private fun resendMessage(messageId: String) {
        messageJob = viewModelScope.launch {
            _state.update { it.copy(isSending = true) }
            val result = resendMessageUseCase(messageId)
            if (result.isFailure) {
                _events.send(ChatEvent.ShowError(UiText.StringResource(R.string.error_unknown)))
            }
            _state.update { it.copy(isSending = false) }
        }
    }

    override fun onCleared() {
        messageJob?.cancel()
        super.onCleared()
    }
}