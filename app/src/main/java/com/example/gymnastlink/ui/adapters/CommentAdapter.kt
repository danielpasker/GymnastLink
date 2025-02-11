package com.example.gymnastlink.ui.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymnastlink.R
import com.example.gymnastlink.controller.UserController
import com.example.gymnastlink.model.Comment
import com.example.gymnastlink.utils.Converters
import com.google.android.material.imageview.ShapeableImageView

class CommentAdapter(private var comments: List<Comment>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    fun set(comments: List<Comment>) {
        this.comments = comments
    }

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.userNameText)
        val userAvatar: ShapeableImageView = itemView.findViewById(R.id.comment_user_avatar)
        val commentText: TextView = itemView.findViewById(R.id.commentText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.commentText.text = comment.text

        UserController.shared.getUserById(comment.userId) { user ->
            user?.let {
                holder.userName.text = it.userName

                if (it.userImg.isNullOrEmpty()) {
                    holder.userAvatar.setImageResource(R.drawable.circle_background)
                } else {
                    val imageByteArray = Converters.decodeImageFromBase64(it.userImg)
                    val bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)

                    if (bitmap != null) {
                        holder.userAvatar.setImageBitmap(bitmap)
                    } else {
                        holder.userAvatar.setImageResource(R.drawable.circle_background)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = comments.size
}