package com.example.gymnastlink.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gymnastlink.R
import com.example.gymnastlink.controller.CommentController
import com.example.gymnastlink.model.Comment
import com.example.gymnastlink.model.Post
import com.example.gymnastlink.ui.MainActivity
import com.example.gymnastlink.ui.adapters.CommentAdapter
import com.example.gymnastlink.ui.components.RecyclerWithTitleView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import java.util.UUID

class PostCommentsFragment : Fragment() {

    private lateinit var post: Post
    private lateinit var commentsView: RecyclerWithTitleView
    private lateinit var adapter: CommentAdapter
    private lateinit var commentText: TextInputEditText
    private lateinit var commentsProgressBar: ProgressBar
    private lateinit var editPostButton: ExtendedFloatingActionButton
    private var commentList = mutableListOf<Comment>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_post_comments, container, false)
        post = arguments?.let { PostCommentsFragmentArgs.fromBundle(it).post }!!

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = activity as? MainActivity
        mainActivity?.showReturnButtonOnToolbar(true)

        commentText = view.findViewById(R.id.addCommentEditText)
        commentsView = view.findViewById(R.id.comments_view)
        commentsProgressBar = view.findViewById(R.id.commentsProgressBar)
        editPostButton = view.findViewById(R.id.edit_post_fab)

        adapter = CommentAdapter(commentList)
        commentsView.title.text = getString(R.string.comments)
        commentsView.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        commentsView.recyclerView.adapter = adapter

        if (post.userId == MainActivity.user?.userId) {
            editPostButton.visibility = ExtendedFloatingActionButton.VISIBLE
        }

        editPostButton.apply { setOnClickListener {
                val action = PostCommentsFragmentDirections
                    .actionPostCommentsFragmentToPostFragment(post)
                view.findNavController().navigate(action)
            }
        }

        view.findViewById<TextInputLayout>(R.id.textInputLayout).setEndIconOnClickListener {
            addComment()
        }

        lifecycleScope.launch {
            getComments()
        }
    }

    private fun getComments() {
        commentsProgressBar.visibility = View.VISIBLE

        CommentController.shared.getCommentsByPost(post.postId) {
            commentList = it.toMutableList()
            adapter.set(it)
            adapter.notifyDataSetChanged()
            commentsProgressBar.visibility = View.GONE
        }

        startListeningForCommentChanges()
    }

    private fun addComment() {
        val comment = MainActivity.user?.let {
            Comment (
                commentId = UUID.randomUUID().toString(),
                userId = it.userId,
                postId = post.postId,
                text = commentText.text.toString()
            )
        }

        if (comment != null) {
            CommentController.shared.addComment(comment) {
                commentText.text?.clear()
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun startListeningForCommentChanges() {
        CommentController.shared.listenForCommentChanges(post.postId) { comments ->
            commentList = comments.toMutableList()
            adapter.set(comments)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        getComments()
        startListeningForCommentChanges()
    }

    override fun onPause() {
        super.onPause()
        CommentController.shared.removeCommentListener()
    }

}