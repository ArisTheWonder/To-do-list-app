package com.aristhewonder.todolistapp.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aristhewonder.todolistapp.data.entity.TaskCategory

@Composable
fun TaskCategoryItem(
    category: TaskCategory,
    modifier: Modifier,
    onClick: (category: TaskCategory) -> Unit
) {
    Box(
        modifier = modifier
            .padding(start = 8.dp, end = 8.dp)
            .height(48.dp)
            .clickable {
                onClick.invoke(category)
            },
    ) {
        Text(
            text = category.name,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}