package com.example.gymnastlink.model

import com.example.gymnastlink.base.Constants
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ListenerRegistration

class PostsFirebaseModel {
    private val database = Firebase.firestore
    private var postsListener: ListenerRegistration? = null

    init {
        val settings = firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings {  })
        }

        database.firestoreSettings = settings
    }

    fun getAllPosts(callback: (List<Post>) -> Unit) {
        database.collection(Constants.Collections.POSTS).get().addOnCompleteListener {
            when (it.isSuccessful) {
                true -> {
                    val posts: MutableList<Post> = mutableListOf()
                    for (json in it.result) {
                        posts.add(Post.fromJSON(json.data))
                    }
                    callback(posts)
                }
                false -> callback(listOf())
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
                if (e != null) {
                    callback(listOf())
                    return@addSnapshotListener
                }

                val posts: MutableList<Post> = mutableListOf()
                for (doc in snapshots!!) {
                    posts.add(Post.fromJSON(doc.data))
                }
                callback(posts)
            }
    }

    fun removePostListener() {
        postsListener?.remove()
    }

}