package com.example.gymnastlink.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gymnastlink.model.Post

@Dao
interface PostDao {

    @Query("SELECT * FROM Post ORDER BY date DESC")
    fun getAllPosts(): List<Post>

//    TODO: to be implemented when profile page is created
    @Query("SELECT * FROM Post WHERE userName =:useName")
    fun getPostsByUserName(useName: String): List<Post>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg post: Post)

}