package com.devautro.firebasechatapp.core.di

import com.devautro.firebasechatapp.chatScreen.data.ChatRepository
import com.devautro.firebasechatapp.chatsHome.data.ChatsHomeRepository
import com.devautro.firebasechatapp.profile.data.ProfileDataUploader
import com.devautro.firebasechatapp.users.data.UsersDataRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
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
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    @Provides
    @Singleton
    fun provideUsersDataRepository(
        auth: FirebaseAuth,
        database: FirebaseDatabase
    ): UsersDataRepository {
        return UsersDataRepository(auth, database)
    }

    @Provides
    @Singleton
    fun provideChatsHomeRepository(
        auth: FirebaseAuth,
        database: FirebaseDatabase
    ): ChatsHomeRepository {
        return ChatsHomeRepository(auth, database)
    }

    @Provides
    @Singleton
    fun provideChatRepository(
        auth: FirebaseAuth,
        database: FirebaseDatabase
    ): ChatRepository {
        return ChatRepository(auth, database)
    }

    @Provides
    @Singleton
    fun provideProfileDataUploader(
        auth: FirebaseAuth,
        database: FirebaseDatabase
    ): ProfileDataUploader {
        return ProfileDataUploader(auth, database)
    }

}
