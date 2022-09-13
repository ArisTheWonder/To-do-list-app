package com.aristhewonder.todolistapp.ui.taskCategory

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddTaskCategoryFragment : TaskCategoryFragment() {

    override fun getTitleText(): String = "Create new list"

    override fun getHitText(): String = "Enter list title"

    override fun getTextInputDefaultValue(): String = ""

    override fun onSuccess() {
        showMessage(message = "New list was created successfully.")
    }

    override fun onFailure() {
        showMessage(message = "Failed to create the new list.")
    }

    override fun preformAction(taskCategoryName: String) {
        viewModel.onInsert(taskCategoryName)
    }


}