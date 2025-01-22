package com.example.gymnastlink.dao

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.gymnastlink.model.Comment
import com.example.gymnastlink.utils.MyApplication
import com.example.gymnastlink.utils.Converters
import com.example.gymnastlink.model.Post
import com.example.gymnastlink.model.User

@Database(entities = [Post::class, Comment::class, User::class], version = 5)
@TypeConverters(Converters::class)
abstract class LocalDataBaseRepository: RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao
    abstract fun userDao(): UserDao
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