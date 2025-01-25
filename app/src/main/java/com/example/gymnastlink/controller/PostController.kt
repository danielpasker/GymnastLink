package com.example.gymnastlink.controller

import android.os.Looper
import androidx.core.os.HandlerCompat
import com.example.gymnastlink.dao.LocalDataBase
import com.example.gymnastlink.dao.LocalDataBaseRepository
import com.example.gymnastlink.firebase.FirebasePostManager
import com.example.gymnastlink.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.Serializable
import java.time.LocalDate
import java.util.concurrent.Executors

class PostController private constructor(){

    private val firebasePostManager = FirebasePostManager()
    private val localdatabase: LocalDataBaseRepository = LocalDataBase.database
    private val executer = Executors.newSingleThreadExecutor()
    private var mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())

    companion object {
        val shared = PostController()
    }

    fun getAllPosts(callback: (List<Post>) -> Unit) {
        executer.execute {
            val localPosts = localdatabase.postDao().getAllPosts().sortedByDescending { it.date }
            if (localPosts.isNotEmpty()) {
                mainHandler.post { callback(localPosts) }
            } else {
                firebasePostManager.getAllPosts { firebasePosts ->
                    val sortedFirebasePosts = firebasePosts.sortedByDescending { it.date }
                    executer.execute {
                        localdatabase.postDao().insertAll(*sortedFirebasePosts.toTypedArray())
                        mainHandler.post { callback(sortedFirebasePosts) }
                    }
                }
            }
        }
    }

    fun getPostsByUserId(userId: String, callback: (List<Post>) -> Unit) {
        executer.execute {
            val localPosts = localdatabase.postDao().getPostsByUserId(userId).sortedByDescending { it.date }
            if (localPosts.isNotEmpty()) {
                mainHandler.post { callback(localPosts) }
            } else {
                firebasePostManager.getPostsByUserId (userId) { firebasePosts ->
                    val sortedFirebasePosts = firebasePosts.sortedByDescending { it.date }
                    executer.execute {
                        localdatabase.postDao().insertAll(*sortedFirebasePosts.toTypedArray())
                        mainHandler.post { callback(sortedFirebasePosts) }
                    }
                }
            }
        }
    }

    fun addPost(post: Post, callback: () -> Unit) {
        executer.execute {
            firebasePostManager.addPost(post) {
                mainHandler.post { callback() }
            }
        }
    }

    fun update(postId: String, updatedData: Map<String, Serializable?>, callback: () -> Unit) {
        runBlocking {
            launch(Dispatchers.IO) {
                val post = localdatabase.postDao().getPostById(postId)

                post?.let {
                    val updatedPost = it.copy(
                        postId = updatedData["postId"] as? String ?: it.postId,
                        userId = updatedData["userId"] as? String ?: it.userId,
                        userName = updatedData["userName"] as? String ?: it.userName,
                        userTitle = updatedData["userTitle"] as? String ?: it.userTitle,
                        title = updatedData["title"] as? String ?: it.title,
                        content = updatedData["content"] as? String ?: it.content,
                        image = updatedData["image"] as? String ?: it.image,
                        date = updatedData["date"] as? LocalDate ?: it.date
                    )
                    localdatabase.postDao().update(updatedPost)
                }

                firebasePostManager.update(postId, updatedData, callback)
            }
        }
    }

    fun deletePost(post: Post, callback: () -> Unit) {
        executer.execute {
            localdatabase.postDao().deletePost(post)

            firebasePostManager.deletePost(post) {
                mainHandler.post { callback() }
            }
        }
    }

    fun listenForPostChanges(callback: (List<Post>) -> Unit) {
        firebasePostManager.listenForPostChanges { firebasePosts ->
            executer.execute {
                localdatabase.postDao().insertAll(*firebasePosts.toTypedArray())
                val allPosts = localdatabase.postDao().getAllPosts()
                mainHandler.post { callback(allPosts) }
            }
        }
    }

    fun removePostListener() {
        firebasePostManager.removePostListener()
    }
}