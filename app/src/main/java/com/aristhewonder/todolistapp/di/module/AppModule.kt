package com.aristhewonder.todolistapp.di.module

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module(
    includes = [
        DatabaseModule::class,
        CoroutineModule::class
    ]
)
@InstallIn(SingletonComponent::class)
object AppModule