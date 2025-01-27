package com.example.gymnastlink.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gymnastlink.model.ExerciseItem

@Dao
interface WorkoutDao {

    @Query("SELECT * FROM ExerciseItem WHERE userId =:userId")
    fun getWorkoutsByUserId(userId: String): List<ExerciseItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg exerciseItem: ExerciseItem)

    @Delete
    fun deleteWorkout(exerciseItem: ExerciseItem)

}