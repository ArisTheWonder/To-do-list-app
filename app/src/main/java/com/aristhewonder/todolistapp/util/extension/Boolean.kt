package com.aristhewonder.todolistapp.util.extension

fun Boolean?.orFalse() = this ?: false

fun Boolean?.orTrue() = this ?: true