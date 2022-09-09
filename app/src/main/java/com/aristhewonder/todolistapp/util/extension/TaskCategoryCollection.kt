package com.aristhewonder.todolistapp.util.extension

import com.aristhewonder.todolistapp.data.entity.TaskCategory

fun List<TaskCategory>.second(): TaskCategory {
    if (isEmpty())
        throw NoSuchElementException("List is empty.")
    return this[1]
}

fun List<TaskCategory>.secondOrFirst(): TaskCategory {
    if (isEmpty())
        throw NoSuchElementException("List is empty.")
    return if (this.size == 1)
        this.first()
    else
        this.second()
}