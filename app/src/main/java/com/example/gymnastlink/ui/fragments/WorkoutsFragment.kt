package com.example.gymnastlink.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gymnastlink.R
import com.example.gymnastlink.controller.WorkoutController
import com.example.gymnastlink.firebase.FirebaseSecretsManager
import com.example.gymnastlink.model.CacheEntry
import com.example.gymnastlink.model.ExerciseItem
import com.example.gymnastlink.ui.MainActivity
import com.example.gymnastlink.ui.adapters.ExerciseAdapter
import com.example.gymnastlink.ui.components.RecyclerWithTitleView
import com.example.gymnastlink.utils.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

private const val API_KEY_HEADER = "x-rapidapi-key"
private const val HOST_HEADER = "x-rapidapi-host"

class WorkoutsFragment : Fragment() {
    private val httpClient = OkHttpClient()
    private val cache = mutableMapOf<String, CacheEntry<List<ExerciseItem>>>()
    private val cacheDuration = Constants.CACHE_DURATION
    private val firebaseSecretsManager = FirebaseSecretsManager()

    private lateinit var workoutSearchEditText: EditText
    private lateinit var searchResultsView: RecyclerWithTitleView
    private lateinit var myPlanView: RecyclerWithTitleView
    private lateinit var searchResultAdapter: ExerciseAdapter
    private lateinit var myPlanAdapter: ExerciseAdapter
    private lateinit var exerciseSearchProgressBar: ProgressBar
    private lateinit var myPlanProgressBar: ProgressBar

    companion object {
        val searchResults = mutableListOf<ExerciseItem>()
        var myPlan = mutableListOf<ExerciseItem>()
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
        exerciseSearchProgressBar = view.findViewById(R.id.exercise_search_progressBar)
        myPlanProgressBar = view.findViewById(R.id.my_plan_progressBar)
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

        lifecycleScope.launch {
            getMyPlan()
        }
    }

    override fun onResume() {
        super.onResume()
        getMyPlan()
        myPlanAdapter.notifyDataSetChanged()
        searchResultAdapter.notifyDataSetChanged()
    }

    private fun getMyPlan() {
        myPlanProgressBar.visibility = View.VISIBLE

        MainActivity.user?.let {
            WorkoutController.shared.getWorkoutsByUserId(it.userId) {
                myPlan = it.toMutableList()
                myPlanAdapter.set(it)
                myPlanAdapter.notifyDataSetChanged()
                myPlanProgressBar.visibility = View.GONE
            }
        }
    }

    private fun createTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    searchResultsView.visibility = View.GONE
                } else {
                    searchResultsView.visibility = View.VISIBLE
                    exerciseSearchProgressBar.visibility = View.VISIBLE
                    // Fetch search results from API asynchronously
                    lifecycleScope.launch {
                        getSearchResults(s.toString())
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }
    }

    private suspend fun fetchWorkouts(query: String): List<ExerciseItem> {
        val currentTime = System.currentTimeMillis()
        val cachedEntry = cache[query]

        if (cachedEntry != null && (currentTime - cachedEntry.timestamp) < cacheDuration) {
            return cachedEntry.data
        }

        val exerciseDBUrl = withContext(Dispatchers.IO) {
            firebaseSecretsManager.getSecretValue(
                Constants.Secrets.EXERCISE_DB,
                Constants.Secrets.URL
            )
        } ?: ""
        val exerciseDBApiKey = withContext(Dispatchers.IO) {
            firebaseSecretsManager.getSecretValue(
                Constants.Secrets.EXERCISE_DB,
                Constants.Secrets.API_KEY
            )
        } ?: ""

        if (exerciseDBUrl.isEmpty() || exerciseDBApiKey.isEmpty()) {
            return emptyList()
        }

        val request = withContext(Dispatchers.IO) {
            Request.Builder()
                .url(Constants.URLS.GET_EXERCISE_BY_NAME_FORMAT.format(exerciseDBUrl, query))
                .get()
                .addHeader(API_KEY_HEADER, exerciseDBApiKey)
                .addHeader(HOST_HEADER, exerciseDBUrl)
                .build()
        }

        return withContext(Dispatchers.IO) {
            val response: Response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val type = object : TypeToken<List<ExerciseItem>>() {}.type
                val result: List<ExerciseItem> = Gson().fromJson(responseBody, type)
                cache[query] = CacheEntry(result, currentTime)

                result
            } else {
                emptyList()
            }
        }
    }

    private suspend fun getSearchResults(query: String) {
        val results = fetchWorkouts(query)
        exerciseSearchProgressBar.visibility = View.GONE

        withContext(Dispatchers.Main) {
            searchResults.clear()
            searchResults.addAll(results)
            searchResultAdapter.notifyDataSetChanged()
        }
    }
}