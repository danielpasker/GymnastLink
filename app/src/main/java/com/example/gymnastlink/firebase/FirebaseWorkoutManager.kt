package com.example.gymnastlink.firebase

import com.example.gymnastlink.model.ExerciseItem
import com.example.gymnastlink.utils.Constants

class FirebaseWorkoutManager: FirebaseManager() {

    fun getWorkoutsByUserId(userId: String, callback: (List<ExerciseItem>) -> Unit){
        database.collection(Constants.Collections.WORKOUTS).whereEqualTo("userId", userId)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val workouts: MutableList<ExerciseItem> = mutableListOf()
                    for (doc in task.result.documents) {
                        doc.data?.let { ExerciseItem.fromJSON(it) }?.let { workouts.add(it) }
                    }
                    callback(workouts)
                } else {
                    callback(emptyList())
                }
            }
    }

    fun addWorkout(exercise: ExerciseItem, callback: () -> Unit) {
        database.collection(Constants.Collections.WORKOUTS).document(exercise.id).set(exercise.json)
            .addOnCompleteListener {
                callback()
            }
    }

    fun deleteWorkout(exercise: ExerciseItem, callback: () -> Unit) {
        database.collection(Constants.Collections.WORKOUTS).document(exercise.id).delete()
            .addOnCompleteListener{
                callback()
            }
    }
}