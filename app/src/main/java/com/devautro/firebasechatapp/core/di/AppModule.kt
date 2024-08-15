package com.devautro.firebasechatapp.core.di

import com.devautro.firebasechatapp.chatScreen.data.ChatRepository
import com.devautro.firebasechatapp.chatsHome.data.ChatsHomeRepository
import com.devautro.firebasechatapp.users.data.UsersDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUsersDataRepository(): UsersDataRepository {
        return UsersDataRepository()
    }

    @Provides
    @Singleton
    fun provideChatsHomeRepository(): ChatsHomeRepository {
        return ChatsHomeRepository()
    }

    @Provides
    @Singleton
    fun provideChatRepository(): ChatRepository {
        return ChatRepository()
    }

}
