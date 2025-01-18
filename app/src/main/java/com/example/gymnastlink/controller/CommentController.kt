package com.example.gymnastlink.controller

import android.os.Looper
import androidx.core.os.HandlerCompat
import com.example.gymnastlink.dao.LocalDataBase
import com.example.gymnastlink.dao.LocalDataBaseRepository
import com.example.gymnastlink.firebase.FirebaseCommentManager
import com.example.gymnastlink.model.Comment
import java.util.concurrent.Executors

class CommentController {
    private val firebaseCommentManager = FirebaseCommentManager()
    private val localdatabase: LocalDataBaseRepository = LocalDataBase.database
    private val executer = Executors.newSingleThreadExecutor()
    private var mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())

    companion object {
        val shared = CommentController()
    }

    fun getCommentsByPost(callback: (List<Comment>) -> Unit, postId: String) {
        executer.execute {
            val localComments = localdatabase.commentDao().getCommentsByPost(postId)
            if (localComments.isNotEmpty()) {
                mainHandler.post { callback(localComments) }
            } else {
                firebaseCommentManager.getCommentsByPost ({ firebaseComments ->
                    executer.execute {
                        localdatabase.commentDao().insertAll(*firebaseComments.toTypedArray())
                        mainHandler.post { callback(firebaseComments) }
                    }
                }, postId)
            }
        }
    }

    fun addComment(comment: Comment, callback: () -> Unit) {
        executer.execute {
            firebaseCommentManager.addComment(comment) {
                mainHandler.post { callback() }
            }
        }
    }

    fun listenForCommentChanges(callback: (List<Comment>) -> Unit, postId: String) {
        firebaseCommentManager.listenForCommentChanges { firebaseComments ->
            executer.execute {
                localdatabase.commentDao().insertAll(*firebaseComments.toTypedArray())
                val allComments = localdatabase.commentDao().getCommentsByPost(postId)
                mainHandler.post { callback(allComments) }
            }
        }
    }

    fun removeCommentListener() {
        firebaseCommentManager.removeCommentListener()
    }
}