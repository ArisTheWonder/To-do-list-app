package com.aristhewonder.todolistapp.ui.taskCategory

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.aristhewonder.todolistapp.ui.component.CustomTextField
import com.aristhewonder.todolistapp.ui.taskCategory.TaskCategoryViewModel.TaskCategoryState.*
import com.aristhewonder.todolistapp.ui.ui.theme.ToDoListAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class TaskCategoryFragment : Fragment() {

    protected val viewModel by viewModels<TaskCategoryViewModel>()

    abstract fun getTitleText(): String
    abstract fun getHintText(): String
    abstract fun getTextInputDefaultValue(): String
    abstract fun onSuccess()
    abstract fun onFailure()
    abstract fun preformAction(taskCategoryName: String)

    @OptIn(ExperimentalComposeUiApi::class)
    private var keyboardController: SoftwareKeyboardController? = null

    @OptIn(ExperimentalComposeUiApi::class)
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
                        keyboardController = LocalSoftwareKeyboardController.current
                        viewModel.onCategoryNameChanged(getTextInputDefaultValue())
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

    @OptIn(ExperimentalComposeUiApi::class)
    private fun topBar(): @Composable () -> Unit = {
        val state = viewModel.state
        val name = viewModel.enteredCategoryName.value
        TopAppBar(
            title = { Text(text = getTitleText()) },
            backgroundColor = Transparent,
            elevation = 0.dp,
            navigationIcon = {
                IconButton(onClick = { backToTasks() }) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                ActionButton(
                    state = state,
                    enabled = state is IdleState && name.isNotEmpty()
                ) {
                    keyboardController?.hide()
                    preformAction(taskCategoryName = viewModel.enteredCategoryName.value)
                }
            }
        )
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    private fun RenderContent() {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Divider()
                var name by remember { mutableStateOf(getTextInputDefaultValue()) }
                CustomTextField(
                    onDone = {
                        keyboardController?.hide()
                        preformAction(taskCategoryName = viewModel.enteredCategoryName.value)
                    },
                    text = name,
                    onValueChange = {
                        name = it
                        viewModel.onCategoryNameChanged(it)
                    },
                    hintText = getHintText(),
                    focusRequester = FocusRequester()
                )
                Divider()
            }

            when (viewModel.state) {
                SuccessState -> {
                    onSuccess()
                    backToTasks()
                }
                FailureState -> {
                    onFailure()
                }
                else -> {}
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
        onClick: () -> Unit,
    ) {
        TextButton(
            enabled = enabled,
            onClick = onClick
        ) {
            if (state is LoadingState) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
            } else {
                Text(text = "Done")
            }
        }
    }
}
