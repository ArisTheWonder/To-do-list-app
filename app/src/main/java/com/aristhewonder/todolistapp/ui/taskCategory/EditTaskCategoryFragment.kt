package com.aristhewonder.todolistapp.ui.taskCategory

import com.aristhewonder.todolistapp.data.entity.TaskCategory
import com.aristhewonder.todolistapp.util.Keys
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditTaskCategoryFragment: TaskCategoryFragment() {

    private val taskCategory: TaskCategory by lazy {
        arguments?.getParcelable(Keys.TASK_CATEGORY)!!
    }

    override fun getTitleText(): String  = "Rename list"

    override fun getHitText(): String = "Enter a new name for you list"

    override fun getTextInputDefaultValue(): String = taskCategory.name

    override fun getActionText(): String = "Rename"

    override fun onSuccess() {
        showMessage("List was renamed successfully.")
    }

    override fun onFailure() {
        showMessage(message = "Failed to rename list.")
    }

    override fun preformAction(taskCategoryName: String) {
        viewModel.onUpdate(
            taskCategory.copy(name = taskCategoryName)
        )
    }
}