package com.aristhewonder.todolistapp.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.aristhewonder.todolistapp.data.entity.Task

@Composable
fun TaskItem(
    task: Task,
    onTaskCompletedClicked: (task: Task) -> Unit,
    onTaskStaredClicked: (task: Task, stared: Boolean) -> Unit,
    onTaskItemClicked: (task: Task) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 8.dp)
            .height(48.dp)
            .clickable { onTaskItemClicked.invoke(task) }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = task.completed, onClick = {
                onTaskCompletedClicked.invoke(task)
            })
            Text(text = task.name)
        }

        StarCheckBox(checked = task.stared) {
            onTaskStaredClicked.invoke(task, !task.stared)
        }
    }
}