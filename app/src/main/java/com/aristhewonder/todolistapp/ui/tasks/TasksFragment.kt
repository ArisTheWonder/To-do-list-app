package com.aristhewonder.todolistapp.ui.tasks

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.aristhewonder.todolistapp.R
import com.aristhewonder.todolistapp.ui.component.EmptyState
import com.aristhewonder.todolistapp.ui.component.TaskList
import com.aristhewonder.todolistapp.ui.component.tablayout.TabFooter
import com.aristhewonder.todolistapp.ui.component.tablayout.TabItemModel
import com.aristhewonder.todolistapp.ui.component.tablayout.TabLayout
import com.aristhewonder.todolistapp.ui.ui.theme.ToDoListAppTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksFragment : Fragment() {

    private val viewModel by viewModels<TasksViewModel>()

    @OptIn(ExperimentalPagerApi::class)
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
                            topBar = { AppBar() }
                        ) {
                            viewModel.taskCategories.value.let {
                                if (it.isNotEmpty()) {
                                    val items = it.subList(1, it.size).map { taskCategory ->
                                        TabItemModel(text = taskCategory.name)
                                    }.toMutableList()
                                    items.add(0, TabItemModel(icon = R.drawable.star_filled))

                                    TabLayout(
                                        items = items,
                                        tabFooter = TabFooter(
                                            text = "New list",
                                            icon = R.drawable.plus
                                        ) {
                                          navigateToAddTaskCategoryFragment()
                                        },
                                        defaultSelectedItemIndex = viewModel.selectedIndex.value,
                                        onTabSelected = { index ->
                                            viewModel.onTaskCategorySelected(it[index], index)
                                        },
                                        tabContent = { pageIndex ->
                                            Box(modifier = Modifier.fillMaxSize()) {
                                                when {
                                                    viewModel.loading.value -> {
                                                        CircularProgressIndicator(
                                                            strokeWidth = 2.dp,
                                                            modifier = Modifier
                                                                .align(Alignment.TopCenter)
                                                                .padding(8.dp)
                                                                .size(28.dp)
                                                        )
                                                    }
                                                    viewModel.tasks.value.isEmpty() -> {
                                                        EmptyState(
                                                            message = "Not tasks yet.",
                                                            modifier = Modifier
                                                                .align(Alignment.Center)
                                                        )
                                                    }
                                                    viewModel.tasks.value.isNotEmpty() -> {
                                                        TaskList(
                                                            tasks = viewModel.tasks.value,
                                                            modifier = Modifier.align(Alignment.Center),
                                                            onTaskCompletedClicked = {},
                                                            onTaskStaredClicked = { task, stared -> }
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }

                    }
                }
            }
        }
    }


    private fun navigateToAddTaskCategoryFragment() {
        findNavController().navigate(R.id.action_tasksFragment_to_addTaskCategoryFragment)
    }

    @Composable
    fun AppBar() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Tasks",
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Medium),
                textAlign = TextAlign.Center
            )
        }
    }

}