package com.aristhewonder.todolistapp.ui.component.tablayout

import androidx.annotation.DrawableRes

data class TabFooter(
    val text: String,
    @DrawableRes val icon: Int? = null,
    val onClick: ()-> Unit
)