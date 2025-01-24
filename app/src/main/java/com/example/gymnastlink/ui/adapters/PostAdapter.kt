package com.example.gymnastlink.ui.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymnastlink.R
import com.example.gymnastlink.model.Post
import com.example.gymnastlink.utils.Converters

class PostAdapter(private var posts: List<Post>, private val onItemClick: (Post) -> Unit) :
    RecyclerView.Adapter<PostAdapter.BlogPostViewHolder>() {

    fun set(posts: List<Post>) {
        this.posts = posts
    }

    class BlogPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.user_name)
        val userTitle: TextView = itemView.findViewById(R.id.user_title)
        val userAvatar: TextView = itemView.findViewById(R.id.user_avatar)
        val title: TextView = itemView.findViewById(R.id.post_title)
        val content: TextView = itemView.findViewById(R.id.post_content)
        val postImage: ImageView = itemView.findViewById(R.id.post_image)
        val date: TextView = itemView.findViewById(R.id.post_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogPostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return BlogPostViewHolder(view)
    }

    override fun onBindViewHolder(holder: BlogPostViewHolder, position: Int) {
        val post = posts[position]

        holder.userName.text = post.userName
        holder.userTitle.text = post.userTitle
        holder.userAvatar.text = post.userName.split(' ').map { it.first() }.joinToString("")
        holder.title.text = post.title
        holder.content.text = post.content
        holder.date.text = Converters.formatDate(post.date)

        post.image?.let {
            val imageByteArray = Converters.decodeImageFromBase64(it)
            val bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
            if (bitmap != null) {
                holder.postImage.setImageBitmap(bitmap)
                holder.postImage.visibility = View.VISIBLE
            } else {
                holder.postImage.visibility = View.GONE
            }
        } ?: run {
            holder.postImage.visibility = View.GONE
        }

        holder.itemView.setOnClickListener { onItemClick(post) }
    }

    override fun getItemCount(): Int = posts.size
}