package com.aristhewonder.todolistapp.ui.tasks

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.aristhewonder.todolistapp.ui.component.CustomTextField
import com.aristhewonder.todolistapp.ui.component.StarCheckBox
import com.aristhewonder.todolistapp.ui.tasks.EditTaskViewModel.TaskEvent.Finish
import com.aristhewonder.todolistapp.ui.ui.theme.ToDoListAppTheme
import com.aristhewonder.todolistapp.util.extension.orFalse
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditTaskFragment : Fragment() {

    private val viewModel by viewModels<EditTaskViewModel>()

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
                    Surface(modifier = Modifier.fillMaxSize()) {
                        Scaffold(
                            topBar = { AppBar() }
                        ) {
                            val keyboardController = LocalSoftwareKeyboardController.current

                            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                                viewModel.tasksEvent.collect { event ->
                                    when (event) {
                                        is Finish -> {
                                            back()
                                        }
                                    }
                                }
                            }
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(70.dp)
                                ) {
                                    val focusRequester = FocusRequester()
                                    CustomTextField(
                                        onDone = {
                                            keyboardController?.hide()
                                            viewModel.onTaskNameChanged()
                                        },
                                        onValueChange = {
                                            viewModel.taskName.value = it
                                        },
                                        text = viewModel.taskName.value,
                                        focusRequester = focusRequester
                                    )
                                }

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    elevation = 8.dp,
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        TextButton(onClick = {
                                            viewModel.onTaskCompleted()
                                        }, modifier = Modifier.padding(end = 8.dp)) {
                                            Text(text = "Mark completed")
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    private fun back() {
        findNavController().navigateUp()
    }

    @Composable
    private fun AppBar() {
        TopAppBar(
            backgroundColor = Color.Transparent,
            navigationIcon = {
                IconButton(onClick = { back() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            title = {},
            elevation = 0.dp,
            actions = {
                val stared = viewModel.task.value?.stared.orFalse()
                StarCheckBox(checked = stared) {
                    viewModel.onTaskStaredStatusChanged(!stared)
                }
                IconButton(onClick = {
                    viewModel.onDeleteTask()
                }) {
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Remove")
                }

            }
        )
    }
}