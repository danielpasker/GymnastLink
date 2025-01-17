package com.example.gymnastlink.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.gymnastlink.R
import com.example.gymnastlink.ui.MainActivity
import com.google.android.material.card.MaterialCardView

class ExerciseDetailsFragment : Fragment() {

    private lateinit var exerciseName: TextView
    private lateinit var muscleName: TextView
    private lateinit var equipmentName: TextView
    private lateinit var description: TextView
    private lateinit var setsNumber: TextView
    private lateinit var repsNumber: TextView
    private lateinit var img: ImageView
    private var position: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_exercise_details, container, false)
        position = arguments?.let { ExerciseDetailsFragmentArgs.fromBundle(requireArguments()).position }!!

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.showReturnButtonOnToolbar(true)

        view.findViewById<MaterialCardView>(R.id.exercise_details_card).setBackgroundResource(
            R.drawable.gradient_background_blue)

        exerciseName = view.findViewById(R.id.exercuse_details_name_textview)
        muscleName = view.findViewById(R.id.exercise_details_muscle_name)
        equipmentName = view.findViewById(R.id.exercise_details_equipment_name)
        description = view.findViewById(R.id.exercise_details_description)
        setsNumber = view.findViewById(R.id.exercise_details_sets_number)
        repsNumber = view.findViewById(R.id.exercise_details_reps_number)
        img = view.findViewById(R.id.exercise_details_img_view)

        displayExercise()
    }

//    TODO: uncomment this function when there will be db data
    private fun displayExercise(){
//        exerciseName.text = exerciseItem.name
//        muscleName.setText("Muscle: ${exerciseItem.target.toString()}")
//        equipmentName.setText("Equipment: ${exerciseItem.equipment.toString()}")
//        description.setText(exerciseItem.instructions.joinToString(separator = "\n"))
//        setsNumber.setText("Sets: 3")
//        repsNumber.setText("Reps: 12")
//
//        Glide.with(requireContext())
//            .load(exerciseItem.gifUrl)
//            .placeholder(R.drawable.placeholder_image)
//            .into(img)
    }
}