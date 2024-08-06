package com.lazyman.todo.data.database

import androidx.room.*
import com.lazyman.todo.data.Tag
import com.lazyman.todo.data.Task
import com.lazyman.todo.data.Workspace
import com.lazyman.todo.data.crossReference.TaskTagCrossRef
import com.lazyman.todo.data.relation.TagWithTasks
import com.lazyman.todo.data.relation.TaskWithTags

@Dao
interface TodoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWorkspace(workspace: Workspace): Long

    @Delete
    suspend fun deleteWorkspace(workspace: Workspace)

    @Update
    suspend fun updateWorkspace(workspace: Workspace)

    @Query("SELECT * FROM workspace_table")
    fun getWorkspaces(): List<Workspace>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTask(task: Task): Long

    @Delete
    suspend fun deleteTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: Tag): Long

    @Delete
    suspend fun deleteTag(tag: Tag)

    @Update
    suspend fun updateTag(tag: Tag)

    @Query("SELECT * FROM task_table WHERE isArchived = 1")
    suspend fun getAllArchivedTasks(): List<Task>

    @Transaction
    @Query("SELECT * FROM task_table WHERE workspaceName = :workspaceName AND isArchived = 0")
    suspend fun getTasks(workspaceName: String): List<Task>

    @Query("DELETE FROM task_table WHERE isArchived = 1")
    suspend fun clearArchived()

    @Transaction
    @Query("SELECT * FROM task_table WHERE taskId = :taskId")
    suspend fun getTaskWithTags(taskId: Long): TaskWithTags?

    @Transaction
    @Query("SELECT * FROM tag_table WHERE tagName = :tagName")
    suspend fun getTagWithTasks(tagName: String): TagWithTasks

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun bindTaskAndTag(taskTagCrossRef: TaskTagCrossRef)

    @Delete
    suspend fun unbindTaskAndTag(taskTagCrossRef: TaskTagCrossRef)

    suspend fun insertTagToTask(taskId: Long, tag: Tag) {
        insertTag(tag)
        val taskTagCrossRef = TaskTagCrossRef(tag.tagName, taskId)
        bindTaskAndTag(taskTagCrossRef)
    }

    suspend fun removeTagFromTask(taskId: Long, tag: Tag) {
        val taskTagCrossRef = TaskTagCrossRef(tag.tagName, taskId)
        unbindTaskAndTag(taskTagCrossRef)
    }

    suspend fun removeTagsWithTask(taskId: Long) {
        val taskWithTags = getTaskWithTags(taskId)
        taskWithTags?.tags?.forEach { tag ->
            removeTagFromTask(taskId, tag)
        }
    }

    suspend fun removeTasksWithWorkspace(workspace: Workspace) {
        val workspaceWithTasks = getTasks(workspace.workspaceName)
        workspaceWithTasks.forEach { task ->
            removeTagsWithTask(task.taskId)
            deleteTask(task)
        }
    }

    suspend fun removeTagFromTasks(tag: Tag) {
        val tagWithTasks = getTagWithTasks(tag.tagName)
        tagWithTasks.tasks.forEach { task ->
            removeTagFromTask(task.taskId, tag)
        }
    }

}