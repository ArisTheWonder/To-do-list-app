package com.aristhewonder.todolistapp.ui.tasks

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.aristhewonder.todolistapp.data.entity.Task
import com.aristhewonder.todolistapp.data.entity.TaskCategory
import com.aristhewonder.todolistapp.ui.component.EmptyState
import com.aristhewonder.todolistapp.ui.component.TaskCategoryList
import com.aristhewonder.todolistapp.ui.component.TaskList
import com.aristhewonder.todolistapp.ui.ui.theme.ToDoListAppTheme
import com.aristhewonder.todolistapp.util.isNotNull
import com.aristhewonder.todolistapp.util.isNull
import com.aristhewonder.todolistapp.util.second
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
                        Scaffold {
                            Box(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Categories(
                                    categories = collectCategoriesAsState().value,
                                    modifier = Modifier.align(Alignment.TopCenter)
                                )

                                //Make sure a category is selected.
                                if (viewModel.selectedCategory.value.isNotNull()) {
                                    Tasks(
                                        tasks = collectTasksAsState().value,
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

                                    Button(
                                        onClick = {
                                            viewModel.insertTaskCategory("New list")
                                        }) {
                                        Text(text = "Create new List")
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
    fun Categories(
        categories: List<TaskCategory>,
        modifier: Modifier
    ) {
        with(categories) {
            if (isNotEmpty()) {
                if (viewModel.selectedCategory.value.isNull()) {
                    viewModel.selectCategory(second())
                }
                TaskCategoryList(
                    categories = this,
                    modifier = modifier
                ) {
                    viewModel.selectCategory(category = it)
                }
            }
        }
    }

    @Composable
    private fun collectCategoriesAsState() =
        viewModel.categories.collectAsState(initial = emptyList())

    @Composable
    fun Tasks(
        tasks: List<Task>,
        modifier: Modifier
    ) {

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

    @Composable
    private fun collectTasksAsState() =
        viewModel.tasks.collectAsState(initial = emptyList())

}