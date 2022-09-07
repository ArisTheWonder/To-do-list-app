package com.aristhewonder.todolistapp.util

fun <T> T.isNotNull(): Boolean {
    return this != null
}

fun <T> T.isNull(): Boolean {
    return this == null
}