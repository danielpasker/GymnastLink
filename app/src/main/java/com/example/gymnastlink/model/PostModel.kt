package com.example.gymnastlink.model

class PostModel private constructor(){

    private val postsFirebaseModel = PostsFirebaseModel()

    companion object {
        val shared = PostModel()
    }

    fun getAllPosts(callback: (List<Post>) -> Unit) {
        postsFirebaseModel.getAllPosts(callback)
    }

    fun addPost(post: Post, callback: () -> Unit) {
        postsFirebaseModel.addPost(post, callback)
    }
}