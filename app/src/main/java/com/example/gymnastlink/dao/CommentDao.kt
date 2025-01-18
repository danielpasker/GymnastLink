package com.example.gymnastlink.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gymnastlink.model.Comment

@Dao
interface CommentDao {

    @Query("SELECT * FROM Comment WHERE postId = :pickedPostId")
    fun getCommentsByPost(pickedPostId : String): List<Comment>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg comment: Comment)
}