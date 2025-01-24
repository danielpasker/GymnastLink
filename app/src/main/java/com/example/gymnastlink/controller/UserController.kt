package com.example.gymnastlink.controller

import android.os.Looper
import androidx.core.os.HandlerCompat
import com.example.gymnastlink.dao.LocalDataBase
import com.example.gymnastlink.dao.LocalDataBaseRepository
import com.example.gymnastlink.firebase.FirebaseUserManager
import com.example.gymnastlink.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.Serializable

class UserController {
    private val firebaseUserManager = FirebaseUserManager()
    private val localdatabase: LocalDataBaseRepository = LocalDataBase.database
    private var mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())

    companion object {
        val shared = UserController()
    }

     fun insert(user: User, callback: () -> Unit) {
         runBlocking {
             launch(Dispatchers.IO) {
                 localdatabase.userDao().insert(user)

                 firebaseUserManager.insert(user) {
                     mainHandler.post {
                         callback()
                     }
                 }
             }
         }
    }

     fun update(userId: String, updatedData: Map<String, Serializable?>, callback: () -> Unit) {
         runBlocking {
             launch(Dispatchers.IO) {
                 val user = localdatabase.userDao().getUserById(userId)

                 user?.let {
                     val updatedUser = it.copy(
                         userName = updatedData["userName"] as? String ?: it.userName,
                         userTitle = updatedData["userTitle"] as? String ?: it.userTitle,
                         age = updatedData["age"] as? Double ?: it.age,
                         weight = updatedData["weight"] as? Double ?: it.weight,
                         gender = updatedData["gender"] as? String ?: it.gender,
                         height = updatedData["height"] as? Double ?: it.height,
                         userImg = updatedData["userImg"] as? String ?: it.userImg
                     )
                     localdatabase.userDao().update(updatedUser)
                 }

                 firebaseUserManager.update(userId, updatedData, callback)
             }
         }
    }

     fun getUserById(userId: String, callback: (User?) -> Unit) {
         runBlocking {
             launch(Dispatchers.IO) {
                 val user = localdatabase.userDao().getUserById(userId)
                 if (user != null) {
                     mainHandler.post {
                         callback(user)
                     }
                 } else {
                     firebaseUserManager.getUserById(userId) { userFromFirebase ->
                         userFromFirebase?.let {
                             runBlocking { localdatabase.userDao().insert(it) }
                         }
                         mainHandler.post {
                             callback(userFromFirebase)
                         }
                     }
                 }
             }
        }
    }
}