package com.aristhewonder.todolistapp.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aristhewonder.todolistapp.data.entity.TaskCategory

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskCategoryList(
    categories: List<TaskCategory>,
    modifier: Modifier,
    onItemClick: (category: TaskCategory) -> Unit,
    onNewListClicked: () -> Unit
) {
    val scrollState = rememberLazyListState()
    LazyRow(
        state = scrollState,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)

    ) {
        items(categories) { category ->
            TaskCategoryItem(
                category, onClick = onItemClick,
                modifier = Modifier.animateItemPlacement()
            )
        }
        item {
            IconButton(onClick = onNewListClicked) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "New list")
            }
        }
    }

    LaunchedEffect(key1 = categories.size) {
        scrollState.animateScrollToItem(categories.lastIndex)
    }
}