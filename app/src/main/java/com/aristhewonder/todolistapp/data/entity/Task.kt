package com.aristhewonder.todolistapp.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

@Parcelize
@Entity(tableName = "task_table")
data class Task(
    @PrimaryKey(autoGenerate = true) val taskId: Int = 0,
    val name: String,
    val categoryId: Int,
    val completed: Boolean = false,
    val stared: Boolean = false,
    val creationDate: Long = System.currentTimeMillis()
): Parcelable {
    val creationDateFormatted: String
        get() = DateFormat.getDateTimeInstance().format(this.creationDate)
}