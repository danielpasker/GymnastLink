package com.example.gymnastlink.ui.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gymnastlink.R
import com.example.gymnastlink.controller.PostController
import com.example.gymnastlink.model.Post
import com.example.gymnastlink.ui.MainActivity
import com.example.gymnastlink.ui.adapters.PostAdapter
import com.example.gymnastlink.ui.components.RecyclerWithTitleView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.launch

class UpdatesFragment : Fragment() {
    private lateinit var postsView: RecyclerWithTitleView
    private lateinit var adapter: PostAdapter
    private lateinit var postsActivityLauncher: ActivityResultLauncher<Intent>
    private lateinit var progressBar: ProgressBar

    companion object {
        var postList = mutableListOf<Post>()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_updates, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = activity as? MainActivity
        mainActivity?.showBottomNavigation(true)
        mainActivity?.showReturnButtonOnToolbar(false)

        progressBar = view.findViewById(R.id.progressBar)

        postsView = view.findViewById(R.id.posts_view)
        postsView.title.text = getString(R.string.updates)
        postsView.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        view.findViewById<ExtendedFloatingActionButton>(R.id.new_post_fab).apply {
            setOnClickListener {
                findNavController().navigate(R.id.action_updatesFragment_to_postFragment)
            }
        }

        postsActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    adapter.notifyDataSetChanged()
                }
            }

        adapter = PostAdapter(postList, onItemClick = { post ->
            val action = UpdatesFragmentDirections.actionUpdatesFragmentToFragmentPostComment(post)
            view.findNavController().navigate(action)
        })
        postsView.recyclerView.adapter = adapter

        lifecycleScope.launch {
            getAllPosts()
        }
    }

    private fun getAllPosts() {
        progressBar.visibility = View.VISIBLE

        PostController.shared.getAllPosts {
            postList = it.sortedByDescending { it.dateTime }.toMutableList()
            adapter.set(it)
            adapter.notifyDataSetChanged()
            progressBar.visibility = View.GONE
        }

        startListeningForPostChanges()
    }

    private fun startListeningForPostChanges() {
        PostController.shared.listenForPostChanges { posts ->
            postList = posts.sortedByDescending { it.dateTime }.toMutableList()
            adapter.set(posts)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        getAllPosts()
    }

    override fun onPause() {
        super.onPause()
        PostController.shared.removePostListener()
    }
}