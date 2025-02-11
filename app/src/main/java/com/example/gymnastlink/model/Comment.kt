package com.example.gymnastlink.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Comment(
    @PrimaryKey val commentId : String,
    val postId: String,
    val userId: String,
    val text: String
){
    companion object {

        const val COMMENT_ID_KEY = "commentId"
        const val POST_ID_KEY = "postId"
        const val USER_ID_KEY = "userId"
        const val TEXT_KEY = "text"

        fun fromJSON(json: Map<String, Any>): Comment {
            val commentId = json[COMMENT_ID_KEY] as? String ?: ""
            val postId = json[POST_ID_KEY] as? String ?: ""
            val userId = json[USER_ID_KEY] as? String ?: ""
            val text = json[TEXT_KEY] as? String ?: ""

            return Comment(
                commentId = commentId,
                postId = postId,
                userId = userId,
                text = text
            )
        }
    }

    val json: HashMap<String, Serializable?>
        get() = hashMapOf(
            COMMENT_ID_KEY to commentId,
            POST_ID_KEY to postId,
            USER_ID_KEY to userId,
            TEXT_KEY to text
        )
}