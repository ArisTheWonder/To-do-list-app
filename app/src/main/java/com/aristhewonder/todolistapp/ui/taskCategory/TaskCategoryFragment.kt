package com.aristhewonder.todolistapp.ui.taskCategory

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.aristhewonder.todolistapp.ui.taskCategory.TaskCategoryViewModel.TaskCategoryState.*
import com.aristhewonder.todolistapp.ui.ui.theme.ToDoListAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class TaskCategoryFragment : Fragment() {

    protected val viewModel by viewModels<TaskCategoryViewModel>()

    abstract fun getTitleText(): String
    abstract fun getHitText(): String
    abstract fun getTextInputDefaultValue(): String
    abstract fun getActionText(): String
    abstract fun onSuccess()
    abstract fun onFailure()
    abstract fun preformAction(taskCategoryName: String)

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return ComposeView(requireContext()).apply {
            setContent {
                ToDoListAppTheme {
                    Surface {
                        Scaffold(
                            topBar = topBar(),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            RenderContent()
                        }
                    }
                }
            }
        }
    }

    private fun topBar(): @Composable () -> Unit = {
        TopAppBar(
            title = { Text(text = getTitleText()) },
            navigationIcon = {
                IconButton(onClick = { backToTasks() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    private fun RenderContent() {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = getHitText(),
                modifier = Modifier.padding(8.dp)
            )

            val keyboardController = LocalSoftwareKeyboardController.current
            var name by remember { mutableStateOf(getTextInputDefaultValue()) }
            TextField(
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }),
                value = name,
                onValueChange = {
                    name = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .height(56.dp)
            )

            val state = viewModel.state
            when (state) {
                SuccessState -> {
                    onSuccess()
                    backToTasks()
                }
                FailureState -> {
                    onFailure()
                }
                else -> {}
            }

            ActionButton(
                state = state,
                enabled = state is IdleState && name.isNotEmpty(),
                text = getActionText()
            ) {
                keyboardController?.hide()
                preformAction(taskCategoryName = name)
            }

        }
    }

    private fun backToTasks() {
        findNavController().navigateUp()
    }

    protected fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    @Composable
    private fun ActionButton(
        state: TaskCategoryViewModel.TaskCategoryState,
        enabled: Boolean,
        text: String,
        onClick: () -> Unit,
    ) {
        Button(
            enabled = enabled,
            onClick = onClick
        ) {
            if (state is LoadingState) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
            } else {
                Text(text = text)
            }
        }
    }
}
