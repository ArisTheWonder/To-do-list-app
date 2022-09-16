package com.aristhewonder.todolistapp.ui.tasks

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedVisibility
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
import com.aristhewonder.todolistapp.ui.component.tablayout.TabLayout
import com.aristhewonder.todolistapp.ui.ui.theme.ToDoListAppTheme
import com.aristhewonder.todolistapp.util.Keys
import com.aristhewonder.todolistapp.util.extension.asTabItems
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
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
        viewModel.onCreateView()
        return ComposeView(requireContext()).apply {
            setContent {
                ToDoListAppTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        bottomSheetState =
                            rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
                        coroutineScope = rememberCoroutineScope()
                        val keyboardController = LocalSoftwareKeyboardController.current
                        focusRequester = remember { FocusRequester() }

                        var taskName by remember { mutableStateOf("") }
                        var taskStared by remember { mutableStateOf(false) }

                        LaunchedEffect(bottomSheetState.currentValue) {
                            when (bottomSheetState.currentValue) {
                                ModalBottomSheetValue.Hidden -> {
                                    keyboardController?.hide()
                                    taskName = ""
                                    taskStared = false
                                }
                                ModalBottomSheetValue.Expanded -> {
                                    focusRequester.requestFocus()
                                    keyboardController?.show()
                                }
                                else -> {}
                            }
                        }
                        BackPressHandler {
                            if (bottomSheetState.currentValue == ModalBottomSheetValue.Expanded) {
                                coroutineScope.launch {
                                    bottomSheetState.hide()
                                }
                            } else {
                                requireActivity().finish()
                            }

                        }
                        ModalBottomSheetLayout(
                            sheetElevation = 16.dp,
                            sheetShape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                            sheetBackgroundColor = Color.White,
                            sheetState = bottomSheetState,
                            sheetContent = {
                                BottomSheetContent(
                                    taskName = taskName,
                                    stared = taskStared,
                                    reserved = viewModel.reserved.value,
                                    onNameChanged = { name -> taskName = name },
                                    onStarStatusChanged = { stared -> taskStared = stared },
                                    onDone = {
                                        coroutineScope.launch {
                                            bottomSheetState.hide()
                                        }
                                    })
                            }
                        ) {

                            Scaffold(
                                topBar = { AppBar() },
                                floatingActionButtonPosition = FabPosition.Center,
                                floatingActionButton = {
                                    AddTaskFab {
                                        coroutineScope.launch {
                                            bottomSheetState.show()
                                        }
                                    }
                                },
                            ) {
                                var event by remember {
                                    mutableStateOf<TasksViewModel.Events>(
                                        TasksViewModel.Events.Idle
                                    )
                                }
                                coroutineScope.launch {
                                    viewModel.events.collect {
                                        event = it
                                    }
                                }

                                Box(modifier = Modifier.fillMaxSize()) {

                                    AnimatedVisibility(visible = event is TasksViewModel.Events.Loading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.align(
                                                Alignment.Center
                                            )
                                        )
                                    }
                                    when (event) {
                                        is TasksViewModel.Events.Loading -> {
                                            CircularProgressIndicator(
                                                modifier = Modifier.align(
                                                    Alignment.Center
                                                )
                                            )
                                        }
                                        is TasksViewModel.Events.DataUpdated -> {
                                            val dataEvent =
                                                (event as TasksViewModel.Events.DataUpdated)
                                            val items = dataEvent.taskCategories.asTabItems()
                                            val pagerState =
                                                rememberPagerState(pageCount = items.size)
                                            coroutineScope.launch {
                                                pagerState.animateScrollToPage(dataEvent.index)
                                            }
                                            TabLayout(
                                                items = items,
                                                pagerState = pagerState,
                                                tabFooter = TabFooter(
                                                    text = "New list",
                                                    icon = R.drawable.plus
                                                ) {
                                                    navigateToAddTaskCategoryFragment()
                                                },
                                                onTabSelected = { index ->
                                                    viewModel.onCategoryChanged(newIndex = index)
                                                },
                                                tabContent = {
                                                    if (dataEvent.tasks.isEmpty()) {
                                                        EmptyState(
                                                            message = if (dataEvent.staredTasks)
                                                                "Not stared tasks.\nYou can mark your important tasks for easy access."
                                                            else
                                                                "Not tasks yet.",
                                                            modifier = Modifier
                                                                .align(Alignment.Center)
                                                        )
                                                    } else {
                                                        TaskList(
                                                            tasks = dataEvent.tasks,
                                                            modifier = Modifier.align(Alignment.Center),
                                                            onTaskCompletedClicked = { task ->
                                                                viewModel.onTaskCompleted(
                                                                    task
                                                                )
                                                            },
                                                            onTaskStaredClicked = { task, stared ->
                                                                viewModel.onTaskStarStatusChanged(
                                                                    task,
                                                                    stared
                                                                )
                                                            },
                                                            onTaskItemClicked = { task ->
                                                                navigateToEditTaskFragment(
                                                                    task.taskId
                                                                )
                                                            }
                                                        )
                                                    }
                                                }
                                            )
                                        }
                                        else -> {}
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

    private fun navigateToEditTaskFragment(taskId: Long) {
        val action = TasksFragmentDirections.actionTasksFragmentToEditTaskFragment(taskId)
        findNavController().navigate(action)
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

    @Composable
    fun BottomSheetContent(
        taskName: String,
        stared: Boolean,
        reserved: Boolean,
        onNameChanged: (name: String) -> Unit,
        onStarStatusChanged: (stared: Boolean) -> Unit,
        onDone: () -> Unit
    ) {


        fun insertTask() {
            viewModel.selectedTaskCategory.value?.let { taskCategory ->
                viewModel.onInsertTask(
                    taskName = taskName,
                    categoryId = taskCategory.categoryId,
                    stared = if (reserved) true else stared
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
        ) {
            CustomTextField(
                onDone = {
                    insertTask()
                    onDone.invoke()
                },
                text = taskName,
                onValueChange = {
                    onNameChanged.invoke(it)
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
            StarCheckBox(checked = if (reserved) true else stared, enabled = !reserved) {
                onStarStatusChanged.invoke(!stared)
            }

            TextButton(
                enabled = taskName.isNotEmpty(),
                onClick = {
                    insertTask()
                    onDone.invoke()
                }
            ) {
                Text(text = "Save")
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

    @Composable
    fun AddTaskFab(onClick: () -> Unit) {
        FloatingActionButton(onClick = onClick, backgroundColor = MaterialTheme.colors.primary) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "AddTask"
            )
        }
    }

}