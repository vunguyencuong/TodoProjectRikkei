package com.lazyman.todo.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "workspace_table")
data class Workspace(

    @PrimaryKey(autoGenerate = false)
    val workspaceName: String,
    val workspaceColor: Int,
) : Serializable
