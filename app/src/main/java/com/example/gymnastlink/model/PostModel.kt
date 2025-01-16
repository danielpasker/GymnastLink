package com.example.gymnastlink.model

import android.os.Looper
import androidx.core.os.HandlerCompat
import com.example.gymnastlink.model.dao.LocalDataBase
import com.example.gymnastlink.model.dao.LocalDataBaseRepository
import java.util.concurrent.Executors

class PostModel private constructor(){

    private val postsFirebaseModel = PostsFirebaseModel()
    private val localdatabase: LocalDataBaseRepository = LocalDataBase.database
    private val executer = Executors.newSingleThreadExecutor()
    private var mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())

    companion object {
        val shared = PostModel()
    }

    fun getAllPosts(callback: (List<Post>) -> Unit) {
        executer.execute {
            val localPosts = localdatabase.postDao().getAllPosts()
            if (localPosts.isNotEmpty()) {
                mainHandler.post { callback(localPosts) }
            } else {
                postsFirebaseModel.getAllPosts { firebasePosts ->
                    executer.execute {
                        localdatabase.postDao().insertAll(*firebasePosts.toTypedArray())
                        mainHandler.post { callback(firebasePosts) }
                    }
                }
            }
        }
    }

    fun addPost(post: Post, callback: () -> Unit) {
        executer.execute {
            localdatabase.postDao().insertAll(post)
            postsFirebaseModel.addPost(post) {
                mainHandler.post { callback() }
            }
        }
    }

    fun listenForPostChanges(callback: (List<Post>) -> Unit) {
        postsFirebaseModel.listenForPostChanges { firebasePosts ->
            executer.execute {
                localdatabase.postDao().insertAll(*firebasePosts.toTypedArray())
                mainHandler.post { callback(firebasePosts) }
            }
        }
    }

    fun removePostListener() {
        postsFirebaseModel.removePostListener()
    }
}