package com.aristhewonder.todolistapp.di.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CoroutineModule {

    companion object {

        @JvmStatic
        @Singleton
        @Provides
        fun provideApplicationScopeCoroutine() = CoroutineScope(SupervisorJob())

    }

}