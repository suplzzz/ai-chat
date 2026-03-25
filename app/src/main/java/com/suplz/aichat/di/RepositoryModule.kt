package com.suplz.aichat.di

import com.suplz.aichat.data.repository.AuthRepositoryImpl
import com.suplz.aichat.data.repository.ChatRepositoryImpl
import com.suplz.aichat.data.repository.MessageRepositoryImpl
import com.suplz.aichat.data.repository.ProfileRepositoryImpl
import com.suplz.aichat.data.repository.SettingsRepositoryImpl
import com.suplz.aichat.domain.repository.AuthRepository
import com.suplz.aichat.domain.repository.ChatRepository
import com.suplz.aichat.domain.repository.MessageRepository
import com.suplz.aichat.domain.repository.ProfileRepository
import com.suplz.aichat.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        profileRepositoryImpl: ProfileRepositoryImpl
    ): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        chatRepositoryImpl: ChatRepositoryImpl
    ): ChatRepository

    @Binds
    @Singleton
    abstract fun bindMessageRepository(
        messageRepositoryImpl: MessageRepositoryImpl
    ): MessageRepository
}