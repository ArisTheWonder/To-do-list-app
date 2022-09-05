package com.aristhewonder.todolistapp.ui.tasks

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.aristhewonder.todolistapp.ui.ui.theme.ToDoListAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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
                                CategoryList(
                                    modifier = Modifier.align(Alignment.TopCenter)
                                )
                                TaskList(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .padding(top = 50.dp)
                                )

                                Button(
                                    modifier = Modifier.align(Alignment.BottomCenter),
                                    onClick = {
                                        viewModel.selectedCategory.value?.let {
                                            viewModel.insertTask(DateFormat.getDateTimeInstance().format(System.currentTimeMillis()), it.categoryId)
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

    @Composable
    fun CategoryList(modifier: Modifier) {
        val scrollState = rememberLazyListState()
        val scope = rememberCoroutineScope()

        val categories =
            viewModel.categories.collectAsState(initial = emptyList())

        with(categories.value) {
            if (isNotEmpty()) {
                viewModel.selectCategory(categories.value.first())
                LazyRow(
                    state = scrollState,
                    modifier = modifier
                        .fillMaxWidth()
                        .height(40.dp)

                ) {
                    items(this@with) { category ->
                        TextButton(onClick = {
                            viewModel.selectCategory(category)
                        }) {
                            Text(text = category.name)
                        }
                    }
                }

                scope.launch {
                    scrollState.animateScrollToItem(categories.value.lastIndex)
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun TaskList(modifier: Modifier) {
        if (viewModel.selectedCategory.value == null)
            return
        val scrollState = rememberLazyListState()

        val tasks =
            viewModel.tasks.collectAsState(initial = emptyList())
        with(tasks.value) {
            if (isNotEmpty()) {
                LazyColumn(
                    state = scrollState,
                    modifier = modifier
                        .fillMaxWidth()
                ) {
                    itemsIndexed(this@with, key = { _, item -> item.taskId }) { _, task ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItemPlacement()
                                .padding(8.dp)
                                .height(48.dp)
                            ) {
                            Text(text = task.name)
                            TextButton(onClick = {
                                viewModel.updateTask(task.copy(completed = true))
                            }) {
                                Text(text = "Mark completed")
                            }
                        }
                    }
                }
            }
            else {
                Text(text = "No tasks yet.", modifier = modifier)
            }
        }
    }

}