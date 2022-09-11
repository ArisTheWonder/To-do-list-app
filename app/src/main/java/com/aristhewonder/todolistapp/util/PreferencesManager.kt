package com.aristhewonder.todolistapp.util

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.aristhewonder.todolistapp.util.extension.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    val preferencesFlow = context.dataStore.data
        .catch {
            emit(emptyPreferences())
        }.map { preferences ->
            val selectedTaskCategoryIndex =
                preferences[PreferencesKeys.SELECTED_TASK_CATEGORY_INDEX]
            UserPreferences(
                selectedTaskCategoryIndex = selectedTaskCategoryIndex
            )
        }

    suspend fun updateSelectedTaskCategoryIndex(index: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_TASK_CATEGORY_INDEX] = index
        }
    }

    private object PreferencesKeys {
        val SELECTED_TASK_CATEGORY_INDEX = intPreferencesKey("selected_task_category_index")
    }

    data class UserPreferences(
        val selectedTaskCategoryIndex: Int? = null
    )

}