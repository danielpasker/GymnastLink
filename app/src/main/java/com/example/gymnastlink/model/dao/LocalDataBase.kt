package com.example.gymnastlink.model.dao

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.gymnastlink.base.MyApplication
import com.example.gymnastlink.model.Converters
import com.example.gymnastlink.model.Post

@Database(entities = [Post::class], version = 3)
@TypeConverters(Converters::class)
abstract class LocalDataBaseRepository: RoomDatabase() {
    abstract fun postDao(): PostDao
}

object LocalDataBase {
    val database: LocalDataBaseRepository by lazy {

        val context = MyApplication.Globals.context ?:
        throw IllegalStateException("Application context is missing")

        Room.databaseBuilder(
            context = context,
            klass = LocalDataBaseRepository::class.java,
            name = "dbFileName.db"
        ).fallbackToDestructiveMigration()
            .build()
    }
}