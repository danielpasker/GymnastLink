package com.example.gymnastlink.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.gymnastlink.R
import com.example.gymnastlink.controller.WorkoutController
import com.example.gymnastlink.model.ExerciseItem
import com.example.gymnastlink.ui.MainActivity
import com.example.gymnastlink.utils.StringUtils.Companion.capitalizeWords
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView

class ExerciseDetailsFragment : Fragment() {
    private lateinit var exerciseItem: ExerciseItem

    private lateinit var exerciseName: TextView
    private lateinit var muscleName: TextView
    private lateinit var equipmentName: TextView
    private lateinit var description: TextView
    private lateinit var img: ShapeableImageView
    private lateinit var addExerciseButton: FloatingActionButton
    private lateinit var deleteExerciseButton: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_exercise_details, container, false)
        exerciseItem = arguments?.let { ExerciseDetailsFragmentArgs.fromBundle(it).exerciseItem }!!

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.showReturnButtonOnToolbar(true)

        exerciseName = view.findViewById(R.id.exercise_details_name_textview)
        muscleName = view.findViewById(R.id.exercise_details_muscle_name)
        equipmentName = view.findViewById(R.id.exercise_details_equipment_name)
        description = view.findViewById(R.id.exercise_details_description)
        img = view.findViewById(R.id.exercise_details_img_view)
        addExerciseButton = view.findViewById(R.id.exercise_details_save_fab)
        deleteExerciseButton = view.findViewById(R.id.delete_exercise_fab)

        addExerciseButton.setOnClickListener {
            exerciseItem.userId = MainActivity.user?.userId
            WorkoutController.shared.addWorkout(exerciseItem) {
                addExerciseButton.visibility = FloatingActionButton.GONE
                deleteExerciseButton.visibility = FloatingActionButton.VISIBLE
            }
        }

        deleteExerciseButton.setOnClickListener {
            exerciseItem.userId = null
            WorkoutController.shared.deleteWorkout(exerciseItem) {
                addExerciseButton.visibility = FloatingActionButton.VISIBLE
                deleteExerciseButton.visibility = FloatingActionButton.GONE
            }
        }

        if (exerciseItem.userId == MainActivity.user?.userId) {
                addExerciseButton.visibility = FloatingActionButton.GONE
                deleteExerciseButton.visibility = FloatingActionButton.VISIBLE
        }

        displayExercise()
    }

    private fun displayExercise() {
        exerciseName.text = exerciseItem.name.capitalizeWords()
        muscleName.text = getString(R.string.muscle_format, exerciseItem.target.capitalizeWords())
        equipmentName.text =
            getString(R.string.equipment_format, exerciseItem.equipment.capitalizeWords())
        description.text = exerciseItem.instructions.joinToString(separator = "\n")

        Glide.with(requireContext())
            .load(exerciseItem.gifUrl)
            .into(img)
    }
}