package com.aristhewonder.todolistapp.ui.tasks

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.aristhewonder.todolistapp.R
import com.aristhewonder.todolistapp.ui.component.EmptyState
import com.aristhewonder.todolistapp.ui.component.TaskCategoryList
import com.aristhewonder.todolistapp.ui.component.TaskCategoryOptionsDropdownMenu
import com.aristhewonder.todolistapp.ui.component.TaskList
import com.aristhewonder.todolistapp.ui.ui.theme.ToDoListAppTheme
import com.aristhewonder.todolistapp.util.Keys
import com.aristhewonder.todolistapp.util.extension.isNotNull
import com.aristhewonder.todolistapp.util.extension.isNull
import com.aristhewonder.todolistapp.util.extension.second
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat

@AndroidEntryPoint
class TasksFragment : Fragment() {

    private val viewModel by viewModels<TasksViewModel>()

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
                        var showMenu by remember { mutableStateOf(false) }
                        Scaffold(
                            topBar = {
                                TopAppBar(
                                    title = { Text(text = "Tasks") },
                                    actions = {
                                        IconButton(onClick = { showMenu = !showMenu }) {
                                            Icon(Icons.Default.MoreVert, "")
                                        }
                                        TaskCategoryOptionsDropdownMenu(
                                            showMenu = showMenu,
                                            onDismissRequest = { showMenu = false },
                                            onRenameItemClicked = { navigateToEditTaskCategoryFragment() },
                                            onRemoveItemClicked = {
                                                showMenu = false
                                                viewModel.onDeleteSelectedTaskCategory()
                                            },
                                            onNewListClicked = { navigateToAddTaskCategoryFragment() })
                                    }

                                )
                            }
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize()
                            ) {

                                Categories(
                                    modifier = Modifier.align(Alignment.TopCenter)
                                )

                                //Make sure a category is selected.
                                if (viewModel.selectedCategory.value.isNotNull()) {
                                    Tasks(
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .fillMaxSize()
                                            .padding(top = 50.dp)
                                    )
                                }

                                Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                                    Button(
                                        onClick = {
                                            viewModel.selectedCategory.value?.let {
                                                viewModel.insertTask(
                                                    DateFormat.getDateTimeInstance()
                                                        .format(System.currentTimeMillis()),
                                                    it.categoryId
                                                )
                                            }
                                        }) {
                                        Text(text = "Create new task")
                                    }
                                }
                            }

                        }

                    }
                }
            }
        }
    }

    @Composable
    fun Categories(modifier: Modifier) {
        val categories = viewModel.categories.collectAsState(initial = emptyList()).value
        with(categories) {
            if (isNotEmpty()) {
                TaskCategoryList(
                    categories = this,
                    modifier = modifier,
                    onItemClick = {
                        viewModel.onCategorySelected(category = it)
                    },
                    onNewListClicked = { navigateToAddTaskCategoryFragment() }
                )
            }
        }
    }


    @Composable
    fun Tasks(modifier: Modifier) {
        val tasks = viewModel.tasks.collectAsState(initial = emptyList()).value
        with(tasks) {
            if (isEmpty()) {
                EmptyState(message = "No tasks yet.", modifier = modifier)
                return
            }
            TaskList(
                tasks,
                modifier,
                onTaskCompletedClicked = { task ->
                    viewModel.updateTask(task.copy(completed = true))
                },
                onTaskStaredClicked = { task, stared ->
                    viewModel.updateTask(task.copy(stared = stared))
                }
            )
        }
    }

    private fun navigateToAddTaskCategoryFragment() {
        findNavController().navigate(R.id.action_tasksFragment_to_addTaskCategoryFragment)
    }

    private fun navigateToEditTaskCategoryFragment() {
        viewModel.selectedCategory.value.let { taskCategory ->
            val destination = R.id.action_tasksFragment_to_editTaskCategoryFragment
            val bundle = bundleOf(Keys.TASK_CATEGORY to taskCategory)
            findNavController().navigate(destination, bundle)
        }
    }

}