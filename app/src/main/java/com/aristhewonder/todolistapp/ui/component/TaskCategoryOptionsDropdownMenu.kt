package com.aristhewonder.todolistapp.ui.component

import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun TaskCategoryOptionsDropdownMenu(
    showMenu: Boolean,
    onDismissRequest: () -> Unit,
    onRenameItemClicked: () -> Unit,
    onRemoveItemClicked: () -> Unit,
    onNewListClicked: () -> Unit,
) {
    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = onDismissRequest,
    ) {
        DropdownMenuItem(onClick = onRenameItemClicked) {
            Text(text = "Rename list")
        }
        DropdownMenuItem(onClick = onRemoveItemClicked) {
            Text(text = "Remove list")
        }
        DropdownMenuItem(onClick = onNewListClicked) {
            Text(text = "New list")
        }
    }
}