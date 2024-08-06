package com.lazyman.todo.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lazyman.todo.data.Tag
import com.lazyman.todo.data.Task
import com.lazyman.todo.data.Workspace
import com.lazyman.todo.data.crossReference.TaskTagCrossRef

@Database(
    entities = [
        Tag::class,
        Task::class,
        Workspace::class,
        TaskTagCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TodoDatabase : RoomDatabase() {

    abstract val todoDAO: TodoDao

    companion object {

        @Volatile
        private var INSTANCE: TodoDatabase? = null

        fun getInstance(context: Context): TodoDatabase {

            // Just only one thread can execute code block at the same time.
            // Prevents more than one threads create database simultaneously.
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    TodoDatabase::class.java,
                    "todo_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}