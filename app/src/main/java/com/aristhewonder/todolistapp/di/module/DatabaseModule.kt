package com.aristhewonder.todolistapp.di.module

import android.app.Application
import androidx.room.Room
import com.aristhewonder.todolistapp.data.dao.TaskDao
import com.aristhewonder.todolistapp.data.database.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideTaskDatabase(
        application: Application,
        callback: TaskDatabase.Callback
    ): TaskDatabase = Room.databaseBuilder(
        application,
        TaskDatabase::class.java,
        "task_database"
    )
        .fallbackToDestructiveMigration()
        .addCallback(callback)
        .build()


    @Provides
    fun provideTaskDao(database: TaskDatabase): TaskDao = database.taskDao()

}