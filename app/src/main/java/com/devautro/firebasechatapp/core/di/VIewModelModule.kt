package com.devautro.firebasechatapp.core.di

import android.content.Context
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
        usersDataRep: UsersDataRepository,
        @ApplicationContext context: Context
    ): UsersScreenViewModel {
        return UsersScreenViewModel(usersDataRep, context)
    }

}