package com.example.gymnastlink.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Comment(
    @PrimaryKey val commentId : String,
    val postId: String,
    val userName: String,
    val text: String
){
    companion object {

        const val COMMENT_ID_KEY = "commentId"
        const val POST_ID_KEY = "postId"
        const val USER_NAME_KEY = "userName"
        const val TEXT_KEY = "text"

        fun fromJSON(json: Map<String, Any>): Comment {
            val commentId = json[COMMENT_ID_KEY] as? String ?: ""
            val postId = json[POST_ID_KEY] as? String ?: ""
            val userName = json[USER_NAME_KEY] as? String ?: ""
            val text = json[TEXT_KEY] as? String ?: ""

            return Comment(
                commentId = commentId,
                postId = postId,
                userName = userName,
                text = text
            )
        }
    }

    val json: HashMap<String, Serializable?>
        get() = hashMapOf(
            COMMENT_ID_KEY to commentId,
            POST_ID_KEY to postId,
            USER_NAME_KEY to userName,
            TEXT_KEY to text
        )
}