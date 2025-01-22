package com.example.gymnastlink.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gymnastlink.R
import com.example.gymnastlink.controller.CommentController
import com.example.gymnastlink.model.Comment
import com.example.gymnastlink.ui.MainActivity
import com.example.gymnastlink.ui.adapters.CommentAdapter
import com.example.gymnastlink.ui.components.RecyclerWithTitleView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import java.util.UUID

class PostCommentsFragment : Fragment() {

    private lateinit var commentsView: RecyclerWithTitleView
    private lateinit var adapter: CommentAdapter
    private lateinit var commentText: TextInputEditText
    private lateinit var postId: String
    private lateinit var commentsProgressBar: ProgressBar
    private var commentList = mutableListOf<Comment>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_post_comments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = activity as? MainActivity
        mainActivity?.showReturnButtonOnToolbar(true)

        postId = arguments?.let{
            PostCommentsFragmentArgs.fromBundle(requireArguments()).postId
        }.toString()

        commentText = view.findViewById(R.id.addCommentEditText)
        commentsProgressBar = view.findViewById(R.id.commentsProgressBar)
        commentsView = view.findViewById(R.id.comments_view)

        view.findViewById<TextInputLayout>(R.id.textInputLayout).setEndIconOnClickListener {
            addComment()
        }

        commentsView.title.text = getString(R.string.comments)
        commentsView.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = CommentAdapter(commentList)
        commentsView.recyclerView.adapter = adapter

        lifecycleScope.launch {
            getComments()
        }
    }

    private fun getComments() {
        commentsProgressBar.visibility = View.VISIBLE

        CommentController.shared.getCommentsByPost( {
            commentList = it.toMutableList()
            adapter.set(it)
            adapter.notifyDataSetChanged()
            commentsProgressBar.visibility = View.GONE
        }, postId)

        startListeningForCommentChanges()
    }

    private fun addComment() {
        val comment = MainActivity.user?.let {
            Comment (
                commentId = UUID.randomUUID().toString(),
                userName = it.userName,
                postId = postId,
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
        CommentController.shared.listenForCommentChanges({ comments ->
            commentList = comments.toMutableList()
            adapter.set(comments)
            adapter.notifyDataSetChanged()
        }, postId)
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