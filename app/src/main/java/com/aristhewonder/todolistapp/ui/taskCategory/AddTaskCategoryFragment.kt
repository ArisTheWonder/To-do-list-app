package com.aristhewonder.todolistapp.ui.taskCategory

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddTaskCategoryFragment : TaskCategoryFragment() {

    override fun getTitleText(): String = "New list"

    override fun getHitText(): String = "Enter a name for your new list."

    override fun getTextInputDefaultValue(): String = ""

    override fun getActionText(): String = "Create"

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