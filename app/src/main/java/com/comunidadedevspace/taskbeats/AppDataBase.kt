package com.comunidadedevspace.taskbeats

import androidx.room.Database
import androidx.room.RoomDatabase

//expor o taskdao, base de dados
@Database(entities = [Task::class], version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}