package com.example.gymnastlink.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class User(
    @PrimaryKey val userId : String,
    val userName: String,
    val userTitle: String,
    val age: Double?,
    val weight: Double?,
    val gender: String,
    val height: Double?,
    var userImg: String
) {
    companion object {

        const val USER_ID_KEY = "userId"
        const val USER_NAME_KEY = "userName"
        const val USER_TITLE_KEY = "userTitle"
        const val AGE_KEY = "age"
        const val WEIGHT_KEY = "weight"
        const val GENDER_KEY = "gender"
        const val HEIGHT_KEY = "height"
        const val USER_IMG_KEY = "userImg"

        fun fromJSON(json: Map<String, Any>): User {
            val userId = json[USER_ID_KEY] as? String ?: ""
            val userName = json[USER_NAME_KEY] as? String ?: ""
            val userTitle = json[USER_TITLE_KEY] as? String ?: ""
            val age = json[AGE_KEY] as? Double ?: 0.0
            val weight = json[WEIGHT_KEY] as? Double ?: 0.0
            val gender = json[GENDER_KEY] as? String ?: ""
            val height = json[HEIGHT_KEY] as? Double ?: 0.0
            val userImg = json[USER_IMG_KEY] as? String ?: ""

            return User(
                userId = userId,
                userName = userName,
                userTitle = userTitle,
                age = age,
                weight = weight,
                gender = gender,
                height = height,
                userImg = userImg
            )
        }
    }

    val json: HashMap<String, Serializable?>
        get() = hashMapOf(
            USER_ID_KEY to userId,
            USER_NAME_KEY to userName,
            USER_TITLE_KEY to userTitle,
            AGE_KEY to age,
            WEIGHT_KEY to weight,
            GENDER_KEY to gender,
            HEIGHT_KEY to height,
            USER_IMG_KEY to userImg
        )
}
