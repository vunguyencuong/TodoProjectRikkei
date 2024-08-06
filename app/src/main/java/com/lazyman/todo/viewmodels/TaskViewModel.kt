package com.lazyman.todo.viewmodels

import androidx.lifecycle.*
import com.lazyman.todo.data.Task
import com.lazyman.todo.data.Workspace
import com.lazyman.todo.data.repository.TodoRepo
import com.lazyman.todo.others.helper.TaskHelper
import com.lazyman.todo.others.helper.sortedByCreatedDate
import com.lazyman.todo.others.helper.sortedByDueDate
import com.lazyman.todo.others.helper.sortedByLastModified
import com.lazyman.todo.others.utilities.ColorUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun MutableList<Task>.updateById(task: Task) {
    val index = this.indexOfFirst { it.taskId == task.taskId }
    if (index != -1)
        this[index] = task
}

class TaskViewModel(private val repo: TodoRepo, defaultWorkspaceName: String) : ViewModel() {

    var archiveMode = false
        private set
    private lateinit var tasks: MutableList<Task>
    private var workspaceName = defaultWorkspaceName
    private val _workspaceNameLiveData = MutableLiveData(workspaceName)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertWorkspace(Workspace(workspaceName, ColorUtils.getRandomColor()))
        }
    }

    private val _taskLiveData by lazy {
        MutableLiveData<List<Task>>().also {
            changeWorkspace()
        }
    }


    fun addTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            val newId = repo.insertTask(task)
            tasks.add(task.copy(taskId = newId))
            updateTaskLiveData()
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteTask(task)
            tasks.remove(task)
            updateTaskLiveData()
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateTask(task)
            tasks.updateById(task)
            cleanUpTasks()
            updateTaskLiveData()
        }
    }

    fun refreshWorkspaceName() {
        _workspaceNameLiveData.value = workspaceName
    }

    fun changeWorkspace(workspaceName: String = this.workspaceName) {
        archiveMode = false
        this.workspaceName = workspaceName
        _workspaceNameLiveData.value = workspaceName

        viewModelScope.launch(Dispatchers.IO) {
            tasks = repo.getTask(workspaceName).toMutableList()
            updateTaskLiveData()
        }
    }

    fun fetchArchived() {
        viewModelScope.launch(Dispatchers.IO) {
            archiveMode = true
            tasks = repo.getAllArchivedTasks().toMutableList()
            updateTaskLiveData()
        }
    }

    fun clearArchived() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.clearArchived()
            tasks.clear()
            updateTaskLiveData()
        }
    }

    private fun cleanUpTasks() {
        val disqualified = { task: Task ->
            !(
                    task.workspaceName == workspaceName &&
                            task.isArchived == archiveMode
                    )
        }
        for (i in tasks.size - 1 downTo 0) {
            if (disqualified(tasks[i])) {
                tasks.removeAt(i)
            }
        }
    }

    fun sortTasks(sortType: String) {
        when (sortType) {
            TaskHelper.DUE_DATE_ASC -> tasks = tasks.sortedByDueDate().toMutableList()
            TaskHelper.DUE_DATE_DESC -> tasks = tasks.sortedByDueDate().reversed().toMutableList()
            TaskHelper.CREATE_DATE_ASC -> tasks = tasks.sortedByCreatedDate().toMutableList()
            TaskHelper.CREATE_DATE_DESC -> tasks =
                tasks.sortedByCreatedDate().reversed().toMutableList()
            TaskHelper.LAST_MODIFIED_ASC -> tasks = tasks.sortedByLastModified().toMutableList()
            TaskHelper.LAST_MODIFIED_DESC -> tasks =
                tasks.sortedByLastModified().reversed().toMutableList()
        }
        updateTaskLiveData()
    }

    private fun updateTaskLiveData() {
        viewModelScope.launch {
            _taskLiveData.value = tasks
        }
    }

    val taskLiveData: LiveData<List<Task>> = _taskLiveData
    val workspaceNameLiveData: LiveData<String> = _workspaceNameLiveData
}

@Suppress("UNCHECKED_CAST")
class TaskViewModelFactory(private val repo: TodoRepo, private val defaultWorkspaceName: String) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            return TaskViewModel(repo, defaultWorkspaceName) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}