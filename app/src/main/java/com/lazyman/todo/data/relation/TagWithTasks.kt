package com.lazyman.todo.data.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.lazyman.todo.data.Tag
import com.lazyman.todo.data.Task
import com.lazyman.todo.data.crossReference.TaskTagCrossRef

data class TagWithTasks(
    @Embedded
    val tag: Tag,

    @Relation(
        parentColumn = "tagName",
        entityColumn = "taskId",
        associateBy = Junction(TaskTagCrossRef::class)
    )
    val tasks: List<Task>
)
