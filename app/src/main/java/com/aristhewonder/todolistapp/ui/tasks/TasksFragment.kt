package com.aristhewonder.todolistapp.ui.tasks

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
                            topBar = {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "Tasks",
                                        style = TextStyle(color = Color.Black),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 20.sp,
                                        modifier = Modifier.padding(all = 5.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        ) {
                            viewModel.taskCategories.value.let {
                                if (it.isNotEmpty()) {
                                    TabLayout(
                                        items = it.map { taskCategory -> TabItemModel(text = taskCategory.name) },
                                        defaultSelectedItemIndex = viewModel.selectedIndex.value,
                                        onTabSelected = { index ->
                                            viewModel.onTaskCategorySelected(it[index], index)
                                        },
                                        tabContent = { pageIndex ->
                                            TabContentScreen(pageIndex.toString())
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
    fun TabContentScreen(data: String) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = data,
                style = MaterialTheme.typography.h5,
                color = Color.Blue,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }

}