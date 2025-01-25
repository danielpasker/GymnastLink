package com.example.gymnastlink.dao
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gymnastlink.model.Post

@Dao
interface PostDao {

    @Query("SELECT * FROM Post ORDER BY date DESC")
    fun getAllPosts(): List<Post>

    @Query("SELECT * FROM Post WHERE userId =:userId")
    fun getPostsByUserId(userId: String): List<Post>

    @Query("SELECT * FROM Post WHERE postId = :postId")
    suspend fun getPostById(postId: String): Post?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg post: Post)

    @Update
    fun update(post: Post)

    @Delete
    fun deletePost(post: Post)

}