package com.suplz.aichat.presentation.images

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.suplz.aichat.data.local.dao.MessageDao
import com.suplz.aichat.data.local.entity.toDomain
import com.suplz.aichat.domain.model.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ImageGalleryViewModel @Inject constructor(
    private val messageDao: MessageDao
) : ViewModel() {

    val imagesPagingFlow: Flow<PagingData<Message>> = Pager(
        config = PagingConfig(pageSize = 30, enablePlaceholders = false),
        pagingSourceFactory = { messageDao.getAllImagesPaged() }
    ).flow
        .map { pagingData -> pagingData.map { it.toDomain() } }
        .cachedIn(viewModelScope)
}