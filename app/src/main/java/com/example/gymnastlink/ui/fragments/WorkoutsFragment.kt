package com.example.gymnastlink.ui.fragments

import ExerciseItem
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gymnastlink.R
import com.example.gymnastlink.utils.enums.BodyPart
import com.example.gymnastlink.utils.enums.Equipment
import com.example.gymnastlink.utils.enums.TargetMuscle
import com.example.gymnastlink.ui.MainActivity
import com.example.gymnastlink.ui.adapters.ExerciseAdapter
import com.example.gymnastlink.ui.components.RecyclerWithTitleView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WorkoutsFragment : Fragment() {

    private lateinit var workoutSearchEditText: EditText
    private lateinit var searchResultsView: RecyclerWithTitleView
    private lateinit var myPlanView: RecyclerWithTitleView
    private lateinit var searchResultAdapter: ExerciseAdapter
    private lateinit var myPlanAdapter: ExerciseAdapter

    companion object {
        val searchResults = mutableListOf<ExerciseItem>()
        val myPlan = mutableListOf<ExerciseItem>()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_workouts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.showReturnButtonOnToolbar(false)

        workoutSearchEditText = view.findViewById(R.id.workout_search)
        searchResultsView = view.findViewById(R.id.search_results_view)
        myPlanView = view.findViewById(R.id.my_plan_view)

        myPlanAdapter = ExerciseAdapter(myPlan)
        myPlanView.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        myPlanView.recyclerView.adapter = myPlanAdapter
        myPlanView.recyclerView.isNestedScrollingEnabled = false
        myPlanView.title.text = getString(R.string.my_plan_header)

        searchResultAdapter = ExerciseAdapter(searchResults)
        searchResultsView.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        searchResultsView.recyclerView.adapter = searchResultAdapter
        searchResultsView.recyclerView.isNestedScrollingEnabled = false
        searchResultsView.title.text = getString(R.string.search_results)

        workoutSearchEditText.addTextChangedListener(createTextWatcher())

        // Load data asynchronously
        lifecycleScope.launch {
            loadMyExercises()
        }
    }

    override fun onResume() {
        super.onResume()
        myPlanAdapter.notifyDataSetChanged()
        searchResultAdapter.notifyDataSetChanged()
    }

    private fun createTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    searchResultsView.visibility = View.GONE
                } else {
                    searchResultsView.visibility = View.VISIBLE
                    // Filter search results asynchronously
                    lifecycleScope.launch {
                        getSearchResults(s.toString())
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }
    }

    private suspend fun loadMyExercises() {
        withContext(Dispatchers.IO) {
            // Simulate data loading
            val dummyPlan = listOf(
                ExerciseItem("1","Dumbbells", Equipment.DUMBBELL, TargetMuscle.BICEPS,
                    BodyPart.UPPER_ARMS,emptyArray(), arrayOf(
                        "sit in a chair",
                        "hold the dumbbells",
                        "work on your biceps with the dumbbells"
                    ),""),
                ExerciseItem("2","Leg Extinction", Equipment.ELLIPTICAL_MACHINE, TargetMuscle.QUADS,
                    BodyPart.UPPER_ARMS,emptyArray(), arrayOf(
                        "get your legs up",
                        "get your legs down"
                    ),""),
                ExerciseItem("3","Pull Ups", Equipment.BODY_WEIGHT, TargetMuscle.SPINE,
                    BodyPart.UPPER_ARMS,emptyArray(), arrayOf(
                        "Get up and down with all of your body"
                    ),"")
            )
            myPlan.clear()
            myPlan.addAll(dummyPlan)
        }
        withContext(Dispatchers.Main) {
            myPlanAdapter.notifyDataSetChanged()
        }
    }

    private suspend fun getSearchResults(query: String) {
        val dummyResults = listOf(
            ExerciseItem("4","Dumbbells", Equipment.UNKNOWN, TargetMuscle.BICEPS,
                BodyPart.UPPER_ARMS,emptyArray(),emptyArray(),""),
            ExerciseItem("5","Dumbbells", Equipment.UNKNOWN, TargetMuscle.BICEPS,
                BodyPart.UPPER_ARMS,emptyArray(),emptyArray(),""),
            ExerciseItem("6","Dumbbells", Equipment.UNKNOWN, TargetMuscle.BICEPS,
                BodyPart.UPPER_ARMS,emptyArray(),emptyArray(),"")
        )

        withContext(Dispatchers.IO) {
            // Simulate filtering logic
            val filteredResults = dummyResults.filter {
                it.name.contains(query, ignoreCase = true)
            }
            searchResults.clear()
            searchResults.addAll(filteredResults)
        }
        withContext(Dispatchers.Main) {
            searchResultAdapter.notifyDataSetChanged()
        }
    }
}