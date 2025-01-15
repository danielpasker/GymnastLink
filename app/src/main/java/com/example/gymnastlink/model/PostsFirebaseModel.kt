package com.example.gymnastlink.model

import com.example.gymnastlink.base.Constants
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.ktx.Firebase

class PostsFirebaseModel {
    private val database = Firebase.firestore

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
}