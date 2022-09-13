package com.aristhewonder.todolistapp.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun CustomTextField(
    onDone: KeyboardActionScope.() -> Unit,
    onValueChange: (String) -> Unit,
    text: String,
    hintText: String? = null,
    focusRequester: FocusRequester
) {
    OutlinedTextField(
        singleLine = true,
        maxLines = 1,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = onDone
        ),
        value = text,
        onValueChange = onValueChange,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
        placeholder = {
            hintText?.let {
                Text(
                    text = it,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    )
}