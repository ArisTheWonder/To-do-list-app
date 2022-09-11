package com.aristhewonder.todolistapp.ui.component.tablayout

import androidx.annotation.DrawableRes

data class TabItemModel(
    val text: String? = null,
    @DrawableRes val icon: Int? = null
)