package com.example.gymnastlink.firebase

import com.example.gymnastlink.model.User
import com.example.gymnastlink.utils.Constants
import java.io.Serializable

class FirebaseUserManager: FirebaseManager() {

    fun insert(user: User, callback: () -> Unit) {
        database.collection(Constants.Collections.USERS).document(user.userId)
            .set(user.json)
            .addOnCompleteListener {
                callback()
            }
    }

    fun update(userId: String, updatedData: Map<String, Serializable?>, callback: () -> Unit) {
        database.collection(Constants.Collections.USERS).document(userId)
            .update(updatedData)
            .addOnCompleteListener{
                callback()
            }
    }

    fun getUserById(userId: String, callback: (User?) -> Unit) {
        database.collection(Constants.Collections.USERS).whereEqualTo("userId", userId)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documents = task.result?.documents
                    val user = documents?.firstOrNull()?.data?.let { User.fromJSON(it) }
                    callback(user)
                } else {
                    callback(null)
                }
            }
    }
}