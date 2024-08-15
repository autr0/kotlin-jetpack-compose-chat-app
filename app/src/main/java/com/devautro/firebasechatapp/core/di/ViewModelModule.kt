package com.devautro.firebasechatapp.core.di

import android.content.Context
import com.devautro.firebasechatapp.chatScreen.data.ChatRepository
import com.devautro.firebasechatapp.chatScreen.presentation.ChatScreenViewModel
import com.devautro.firebasechatapp.chatsHome.data.ChatsHomeRepository
import com.devautro.firebasechatapp.chatsHome.presentation.ChatsHomeViewModel
import com.devautro.firebasechatapp.core.presentation.SharedChatViewModel
import com.devautro.firebasechatapp.users.data.UsersDataRepository
import com.devautro.firebasechatapp.users.presentation.UsersScreenViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

    @Provides
    @ViewModelScoped
    fun provideUsersScreenViewModel(
        usersDataRepo: UsersDataRepository,
        @ApplicationContext context: Context
    ): UsersScreenViewModel {
        return UsersScreenViewModel(usersDataRepo, context)
    }

    @Provides
    @ViewModelScoped
    fun provideChatsHomeViewModel(
        chatsHomeRepo: ChatsHomeRepository
    ): ChatsHomeViewModel {
        return ChatsHomeViewModel(chatsHomeRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideSharedChatViewModel(): SharedChatViewModel {
        return SharedChatViewModel()
    }

    @Provides
    @ViewModelScoped
    fun provideChatScreenViewModel(
        chatRepo: ChatRepository
    ): ChatScreenViewModel {
        return ChatScreenViewModel(chatRepo)
    }

}