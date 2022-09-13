package com.aristhewonder.todolistapp.ui.tasks

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.aristhewonder.todolistapp.R
import com.aristhewonder.todolistapp.data.entity.TaskCategory
import com.aristhewonder.todolistapp.ui.component.*
import com.aristhewonder.todolistapp.ui.component.tablayout.TabFooter
import com.aristhewonder.todolistapp.ui.component.tablayout.TabItemModel
import com.aristhewonder.todolistapp.ui.component.tablayout.TabLayout
import com.aristhewonder.todolistapp.ui.tasks.TasksViewModel.TasksState.*
import com.aristhewonder.todolistapp.ui.ui.theme.ToDoListAppTheme
import com.aristhewonder.todolistapp.util.Keys
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TasksFragment : Fragment() {

    private val viewModel by viewModels<TasksViewModel>()

    @OptIn(ExperimentalMaterialApi::class)
    private lateinit var bottomSheetState: ModalBottomSheetState

    private lateinit var coroutineScope: CoroutineScope

    private lateinit var focusRequester: FocusRequester

    @OptIn(
        ExperimentalPagerApi::class, ExperimentalMaterialApi::class,
        ExperimentalComposeUiApi::class
    )
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
                        bottomSheetState =
                            rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
                        coroutineScope = rememberCoroutineScope()
                        val keyboardController = LocalSoftwareKeyboardController.current
                        focusRequester = remember { FocusRequester() }

                        LaunchedEffect(bottomSheetState.currentValue) {
                            when (bottomSheetState.currentValue) {
                                ModalBottomSheetValue.Hidden -> {
                                    keyboardController?.hide()
                                }
                                ModalBottomSheetValue.Expanded -> {
                                    focusRequester.requestFocus()
                                    keyboardController?.show()
                                }
                                else -> {}
                            }
                        }
                        BackPressHandler {
                            coroutineScope.launch {
                                bottomSheetState.hide()
                            }
                        }
                        ModalBottomSheetLayout(
                            sheetElevation = 16.dp,
                            sheetShape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                            sheetBackgroundColor = Color.White,
                            sheetState = bottomSheetState,
                            sheetContent = {
                                BottomSheetContent()
                            }
                        ) {
                            Scaffold(
                                topBar = { AppBar() },
                                floatingActionButtonPosition = FabPosition.Center,
                                floatingActionButton = {
                                    ExtendedFloatingActionButton(onClick = {
                                        coroutineScope.launch {
                                            bottomSheetState.show()
                                        }

                                    },
                                        icon = {
                                            Icon(
                                                imageVector = Icons.Filled.Add,
                                                contentDescription = "AddTask"
                                            )
                                        },
                                        text = { Text(text = "Add new task") })
                                },
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
                                            tabContent = {
                                                Box(modifier = Modifier.fillMaxSize()) {
                                                    when (val state = viewModel.tasksState.value) {
                                                        is Loading -> {
                                                            CircularProgressIndicator(
                                                                strokeWidth = 2.dp,
                                                                modifier = Modifier
                                                                    .align(Alignment.TopCenter)
                                                                    .padding(8.dp)
                                                                    .size(28.dp)
                                                            )
                                                        }
                                                        is Empty -> {
                                                            EmptyState(
                                                                message = if (state.staredTasks)
                                                                    "Not stared tasks.\nYou can mark your important tasks for easy access."
                                                                else
                                                                    "Not tasks yet.",
                                                                modifier = Modifier
                                                                    .align(Alignment.Center)
                                                            )
                                                        }
                                                        is NotEmpty -> {
                                                            TaskList(
                                                                tasks = state.tasks,
                                                                modifier = Modifier.align(Alignment.Center),
                                                                onTaskCompletedClicked = { task ->
                                                                    viewModel.onTaskCompleted(task)
                                                                },
                                                                onTaskStaredClicked = { task, stared ->
                                                                    viewModel.onTaskStarStatusChanged(
                                                                        task,
                                                                        stared
                                                                    )
                                                                }
                                                            )
                                                        }
                                                        else -> {}
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
    }


    private fun navigateToAddTaskCategoryFragment() {
        findNavController().navigate(R.id.action_tasksFragment_to_addTaskCategoryFragment)
    }

    private fun navigateToEditTaskCategoryFragment(taskCategory: TaskCategory) {
        val destination = R.id.action_tasksFragment_to_editTaskCategoryFragment
        val bundle = bundleOf(Keys.TASK_CATEGORY to taskCategory)
        findNavController().navigate(destination, bundle)
    }

    @Composable
    fun AppBar() {
        TopAppBar(
            elevation = 0.dp,
            backgroundColor = Color.Transparent
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "Tasks",
                    style = TextStyle(fontSize = 24.sp),
                    textAlign = TextAlign.Center
                )
                var showMenu by remember { mutableStateOf(false) }
                IconButton(onClick = {
                    showMenu = true
                }, modifier = Modifier.align(Alignment.CenterEnd)) {
                    Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "More options")
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(top = 56.dp, end = 8.dp)
                ) {
                    TaskCategoryOptionsDropdownMenu(
                        showMenu = showMenu,
                        actionAllowed = !viewModel.reserved.value,
                        onDismissRequest = { showMenu = false },
                        onRenameItemClicked = {
                            viewModel.selectedTaskCategory.value?.let {
                                navigateToEditTaskCategoryFragment(taskCategory = it)
                            }
                        },
                        onRemoveItemClicked = { viewModel.onDeleteTaskCategory() },
                        onNewListClicked = { navigateToAddTaskCategoryFragment() })
                }
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun BottomSheetContent() {
        var taskName by remember { mutableStateOf("") }
        var stared by remember { mutableStateOf(false) }

        fun insertTask() {
            viewModel.selectedTaskCategory.value?.let { taskCategory ->
                viewModel.onInsertTask(
                    taskName = taskName,
                    categoryId = taskCategory.categoryId,
                    stared = stared
                )
            }
        }

        fun done() {
            insertTask()
            coroutineScope.launch {
                bottomSheetState.hide()
            }
            taskName = ""
            stared = false
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
        ) {
            CustomTextField(
                onDone = {
                    done()
                },
                text = taskName,
                onValueChange = {
                    taskName = it
                },
                hintText = "New task",
                focusRequester = focusRequester
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {


            StarCheckBox(checked = stared) {
                stared = !stared
            }

            TextButton(
                enabled = taskName.isNotEmpty(),
                onClick = { done() }
            ) {
                if (viewModel.tasksState.value is InsertingNewTask) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text(text = "Save")
                }
            }
        }

    }

    @Composable
    fun BackPressHandler(
        backPressedDispatcher: OnBackPressedDispatcher? =
            LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher,
        onBackPressed: () -> Unit
    ) {
        val currentOnBackPressed by rememberUpdatedState(newValue = onBackPressed)

        val backCallback = remember {
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    currentOnBackPressed()
                }
            }
        }

        DisposableEffect(key1 = backPressedDispatcher) {
            backPressedDispatcher?.addCallback(backCallback)

            onDispose {
                backCallback.remove()
            }
        }
    }

}