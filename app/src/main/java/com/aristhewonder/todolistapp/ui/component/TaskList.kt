package com.aristhewonder.todolistapp.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.aristhewonder.todolistapp.data.entity.Task

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskList(
    tasks: List<Task>,
    modifier: Modifier,
    onTaskCompletedClicked: (task: Task) -> Unit,
    onTaskStaredClicked: (task: Task, stared: Boolean) -> Unit,
) {
    val scrollState = rememberLazyListState()
    LazyColumn(
        state = scrollState,
        modifier = modifier
            .fillMaxWidth()
    ) {

        itemsIndexed(tasks, key = { _, item -> item.taskId }) { _, task ->
            Box(modifier = Modifier.animateItemPlacement().fillMaxWidth(),) {
                TaskItem(
                    task = task,
                    onTaskCompletedClicked = onTaskCompletedClicked,
                    onTaskStaredClicked = onTaskStaredClicked
                )
            }
        }
    }
}