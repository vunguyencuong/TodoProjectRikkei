package com.lazyman.todo.data.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.lazyman.todo.data.Tag
import com.lazyman.todo.data.Task
import com.lazyman.todo.data.crossReference.TaskTagCrossRef

data class TaskWithTags(
    @Embedded
    val task: Task,

    @Relation(
        parentColumn = "taskId",
        entityColumn = "tagName",
        associateBy = Junction(TaskTagCrossRef::class)
    )
    val tags: List<Tag>
)