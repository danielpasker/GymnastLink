package com.example.gymnastlink.controller

import android.os.Looper
import androidx.core.os.HandlerCompat
import com.example.gymnastlink.dao.LocalDataBase
import com.example.gymnastlink.dao.LocalDataBaseRepository
import com.example.gymnastlink.firebase.FirebasePostManager
import com.example.gymnastlink.model.Post
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

    fun addPost(post: Post, callback: () -> Unit) {
        executer.execute {
            localdatabase.postDao().insertAll(post)
            firebasePostManager.addPost(post) {
                mainHandler.post { callback() }
            }
        }
    }

    fun listenForPostChanges(callback: (List<Post>) -> Unit) {
        firebasePostManager.listenForPostChanges { firebasePosts ->
            executer.execute {
                localdatabase.postDao().insertAll(*firebasePosts.toTypedArray())
                mainHandler.post { callback(firebasePosts) }
            }
        }
    }

    fun removePostListener() {
        firebasePostManager.removePostListener()
    }
}