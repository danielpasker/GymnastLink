package com.example.gymnastlink.firebase

import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.ktx.Firebase

open class FirebaseManager {
    protected val database = Firebase.firestore

    init {
        val settings = firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings { })
        }
        database.firestoreSettings = settings
    }
}