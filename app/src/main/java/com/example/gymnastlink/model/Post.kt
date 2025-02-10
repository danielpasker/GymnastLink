package com.example.gymnastlink.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity
data class Post(
    @PrimaryKey val postId: String,
    val userId: String,
    val userName: String,
    val userTitle: String,
    var title: String,
    var content: String,
    var image: String?,
    var dateTime: LocalDateTime
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        LocalDateTime.parse(parcel.readString(), DateTimeFormatter.ISO_DATE_TIME)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(postId)
        parcel.writeString(userId)
        parcel.writeString(userName)
        parcel.writeString(userTitle)
        parcel.writeString(title)
        parcel.writeString(content)
        parcel.writeString(image)
        parcel.writeString(dateTime.toString())
    }

    override fun describeContents(): Int = 0

    companion object {

        const val POST_ID_KEY = "postId"
        const val USER_ID_KEY = "userId"
        const val USER_NAME_KEY = "userName"
        const val USER_TITLE_KEY = "userTitle"
        const val TITLE_KEY = "title"
        const val CONTENT_KEY = "content"
        const val IMAGE_KEY = "image"
        const val DATE_TIME_KEY = "dateTime"

        fun fromJSON(json: Map<String, Any>): Post {
            val postId = json[POST_ID_KEY] as? String ?: ""
            val userId = json[USER_ID_KEY] as? String ?: ""
            val userName = json[USER_NAME_KEY] as? String ?: ""
            val userTitle = json[USER_TITLE_KEY] as? String ?: ""
            val title = json[TITLE_KEY] as? String ?: ""
            val content = json[CONTENT_KEY] as? String ?: ""
            val image = json[IMAGE_KEY] as? String ?: ""
            val dateTimeString = json[DATE_TIME_KEY] as? String ?: LocalDateTime.now().toString()
            val dateTime = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)

            return Post(
                postId = postId,
                userId = userId,
                userName = userName,
                userTitle = userTitle,
                title = title,
                content = content,
                image = image,
                dateTime = dateTime
            )
        }

        @JvmField
        val CREATOR: Parcelable.Creator<Post> = object : Parcelable.Creator<Post> {
            override fun createFromParcel(parcel: Parcel): Post {
                return Post(parcel)
            }

            override fun newArray(size: Int): Array<Post?> {
                return arrayOfNulls(size)
            }
        }
    }

    val json: HashMap<String, Serializable?>
        get() = hashMapOf(
            POST_ID_KEY to postId,
            USER_ID_KEY to userId,
            USER_NAME_KEY to userName,
            USER_TITLE_KEY to userTitle,
            TITLE_KEY to title,
            CONTENT_KEY to content,
            IMAGE_KEY to image,
            DATE_TIME_KEY to dateTime.toString()
        )
}