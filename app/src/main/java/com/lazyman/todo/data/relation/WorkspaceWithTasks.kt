package com.lazyman.todo.data.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.lazyman.todo.data.Task
import com.lazyman.todo.data.Workspace

data class WorkspaceWithTasks(

    @Embedded
    val workspace: Workspace,

    @Relation(
        parentColumn = "workspaceName",
        entityColumn = "workspaceName"
    )
    val tasks: List<Task>
)
