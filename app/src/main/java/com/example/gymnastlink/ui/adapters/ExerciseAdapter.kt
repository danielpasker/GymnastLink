package com.example.gymnastlink.ui.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.gymnastlink.R
import com.example.gymnastlink.model.ExerciseItem
import com.example.gymnastlink.ui.fragments.WorkoutsFragmentDirections
import com.example.gymnastlink.utils.StringUtils.Companion.capitalizeWords

class ExerciseAdapter(private val exercises: List<ExerciseItem>) :
    RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val exerciseName: TextView = itemView.findViewById(R.id.exercise_name)
        val exerciseImage: ImageView = itemView.findViewById(R.id.exercise_image)
        val exerciseMainMuscle: TextView = itemView.findViewById(R.id.exercise_main_muscle)
        val progressBar: ProgressBar = itemView.findViewById(R.id.image_loading_progress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.exercise_item, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.exerciseName.text = exercise.name.capitalizeWords()
        holder.exerciseMainMuscle.text = exercise.target.capitalizeWords()

        holder.progressBar.visibility = View.VISIBLE

        Glide.with(holder.itemView.context)
            .load(exercise.gifUrl)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.progressBar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.progressBar.visibility = View.GONE
                    return false
                }
            })
            .into(holder.exerciseImage)

        holder.itemView.setOnClickListener {
            val action: NavDirections = WorkoutsFragmentDirections
                .actionWorkoutsFragmentToExerciseDetailsFragment(exercise)
            it.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int = exercises.size
}