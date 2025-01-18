package com.example.gymnastlink.firebase

import com.example.gymnastlink.model.Comment
import com.example.gymnastlink.utils.Constants
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot

class FirebaseCommentManager : FirebaseManager() {
    private var commentListener: ListenerRegistration? = null

    private fun insertCommentsFromServer(snapshot: QuerySnapshot?, callback: (List<Comment>) -> Unit) {
        if (snapshot != null) {
            val comments: MutableList<Comment> = mutableListOf()
            for (doc in snapshot.documents) {
                doc.data?.let { Comment.fromJSON(it) }?.let { comments.add(it) }
            }
            callback(comments)
        } else {
            callback(emptyList())
        }
    }

    fun getCommentsByPost(callback: (List<Comment>) -> Unit, postId: String) {
        database.collection(Constants.Collections.COMMENTS).whereEqualTo("postId", postId)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    insertCommentsFromServer(task.result, callback)
                } else {
                    callback(emptyList())
                }
            }
    }

    fun addComment(comment: Comment, callback: () -> Unit) {
        database.collection(Constants.Collections.COMMENTS).document(comment.commentId)
            .set(comment.json)
            .addOnCompleteListener {
                callback()
            }
    }

    fun listenForCommentChanges(callback: (List<Comment>) -> Unit) {
        commentListener = database.collection(Constants.Collections.COMMENTS)
            .addSnapshotListener { snapshots, e ->
                if (e != null || snapshots == null) {
                    callback(emptyList())
                    return@addSnapshotListener
                }
                insertCommentsFromServer(snapshots, callback)
            }
    }

    fun removeCommentListener() {
        commentListener?.remove()
    }
}