package com.example.gymnastlink.firebase

import com.example.gymnastlink.utils.Constants
import kotlinx.coroutines.tasks.await

class FirebaseSecretsManager : FirebaseManager() {
    suspend fun getSecretValue(document: String, key: String): String? {
        return try {
            val doc =
                database.collection(Constants.Collections.SECRETS).document(document).get().await()
            doc.getString(key)
        } catch (e: Exception) {
            null
        }
    }

}