package com.suplz.aichat.presentation.chat_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.suplz.aichat.domain.model.Chat
import com.suplz.aichat.domain.usecase.chat.GetChatsUseCase
import com.suplz.aichat.domain.usecase.chat.SearchChatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val getChatsUseCase: GetChatsUseCase,
    private val searchChatsUseCase: SearchChatsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ChatListState())
    val state = _state.asStateFlow()

    private val _events = Channel<ChatListEvent>()
    val events = _events.receiveAsFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val chatsPagingFlow: Flow<PagingData<Chat>> = _state
        .map { it.activeSearchQuery }
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank()) {
                getChatsUseCase()
            } else {
                searchChatsUseCase(query)
            }
        }
        .cachedIn(viewModelScope)

    fun onAction(action: ChatListAction) {
        when (action) {
            is ChatListAction.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = action.query) }
            }
            ChatListAction.OnSearchClick -> {
                _state.update { it.copy(activeSearchQuery = it.searchQuery.trim()) }
            }
        }
    }
}