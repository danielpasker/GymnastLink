package com.example.gymnastlink.controller

import android.os.Looper
import androidx.core.os.HandlerCompat
import com.example.gymnastlink.dao.LocalDataBase
import com.example.gymnastlink.dao.LocalDataBaseRepository
import com.example.gymnastlink.firebase.FirebaseWorkoutManager
import com.example.gymnastlink.model.ExerciseItem
import java.util.concurrent.Executors

class WorkoutController {
    private val firebaseWorkoutManager = FirebaseWorkoutManager()
    private val localdatabase: LocalDataBaseRepository = LocalDataBase.database
    private val executer = Executors.newSingleThreadExecutor()
    private var mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())

    companion object {
        val shared = WorkoutController()
    }

    fun getWorkoutsByUserId(userId: String, callback: (List<ExerciseItem>) -> Unit) {
        executer.execute {
            val localWorkouts = localdatabase.workoutDao().getWorkoutsByUserId(userId)
            if (localWorkouts.isNotEmpty()) {
                mainHandler.post { callback(localWorkouts) }
            } else {
                firebaseWorkoutManager.getWorkoutsByUserId (userId) { firebaseWorkouts ->
                    executer.execute {
                        localdatabase.workoutDao().insertAll(*firebaseWorkouts.toTypedArray())
                        mainHandler.post { callback(firebaseWorkouts) }
                    }
                }
            }
        }
    }

    fun addWorkout(exercise: ExerciseItem, callback: () -> Unit) {
        executer.execute {
            localdatabase.workoutDao().insertAll(exercise)

            firebaseWorkoutManager.addWorkout(exercise) {
                mainHandler.post { callback() }
            }
        }
    }

    fun deleteWorkout(exercise: ExerciseItem, callback: () -> Unit) {
        executer.execute {
            localdatabase.workoutDao().deleteWorkout(exercise)

            firebaseWorkoutManager.deleteWorkout(exercise) {
                mainHandler.post { callback() }
            }
        }
    }
}