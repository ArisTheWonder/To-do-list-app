package com.aristhewonder.todolistapp.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.aristhewonder.todolistapp.R

@Composable
fun StarCheckBox(
    checked: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick, enabled = enabled) {
        val drawable = if (checked) {
            R.drawable.star_filled
        } else {
            R.drawable.star_outlined
        }
        Icon(painter = painterResource(id = drawable), contentDescription = "", modifier = Modifier.size(24.dp))
    }
}