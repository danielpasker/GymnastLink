package com.example.gymnastlink.firebase

import com.example.gymnastlink.model.Post
import com.example.gymnastlink.utils.Constants
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot

class FirebasePostManager : FirebaseManager() {
    private var postsListener: ListenerRegistration? = null

    private fun insertPostsFromServer(snapshot: QuerySnapshot?, callback: (List<Post>) -> Unit) {
        if (snapshot != null) {
            val posts: MutableList<Post> = mutableListOf()
            for (doc in snapshot.documents) {
                doc.data?.let { Post.fromJSON(it) }?.let { posts.add(it) }
            }
            callback(posts)
        } else {
            callback(emptyList())
        }
    }

    fun getAllPosts(callback: (List<Post>) -> Unit) {
        database.collection(Constants.Collections.POSTS).get().addOnCompleteListener {
            if (it.isSuccessful) {
                insertPostsFromServer(it.result, callback)
            } else {
                callback(emptyList())
            }
        }
    }

    fun getPostsByUserId(userId: String, callback: (List<Post>) -> Unit) {
        database.collection(Constants.Collections.POSTS).whereEqualTo("userId", userId)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    insertPostsFromServer(task.result, callback)
                } else {
                    callback(emptyList())
                }
            }
    }

    fun addPost(post: Post, callback: () -> Unit) {
        database.collection(Constants.Collections.POSTS).document(post.postId).set(post.json)
            .addOnCompleteListener {
                callback()
            }
    }

    fun listenForPostChanges(callback: (List<Post>) -> Unit) {
        postsListener = database.collection(Constants.Collections.POSTS)
            .addSnapshotListener { snapshots, e ->
                if (e != null || snapshots == null) {
                    callback(emptyList())
                    return@addSnapshotListener
                }
                insertPostsFromServer(snapshots, callback)
            }
    }

    fun removePostListener() {
        postsListener?.remove()
    }

}