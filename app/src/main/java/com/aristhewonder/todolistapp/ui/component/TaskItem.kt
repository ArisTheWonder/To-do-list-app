package com.aristhewonder.todolistapp.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aristhewonder.todolistapp.data.entity.Task

@Composable
fun TaskItem(
    task: Task,
    onTaskCompletedClicked: (task: Task) -> Unit,
    onTaskStaredClicked: (task: Task, stared: Boolean) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 8.dp)
            .height(48.dp)
    ) {
        RadioButton(selected = task.completed, onClick = {
            onTaskCompletedClicked.invoke(task)
        })
        Text(text = task.name)
        Checkbox(checked = task.stared, onCheckedChange = { checked ->
            onTaskStaredClicked.invoke(task, checked)
        })
    }
}