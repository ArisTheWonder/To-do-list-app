package com.aristhewonder.todolistapp.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

@Parcelize
@Entity(tableName = "task_category_table")
data class TaskCategory(
    @PrimaryKey(autoGenerate = true) val categoryId: Long = 0,
    val name: String,
    val creationDate: Long = System.currentTimeMillis(),
    val reserved: Boolean = false
): Parcelable {
    val creationDateFormatted: String
        get() = DateFormat.getDateTimeInstance().format(this.creationDate)
}