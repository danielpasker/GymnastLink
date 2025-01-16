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
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class PostAdapter(private val posts: List<Post>, private val onItemClick: (Post) -> Unit) :
    RecyclerView.Adapter<PostAdapter.BlogPostViewHolder>() {

    class BlogPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.user_name)
        val userTitle: TextView = itemView.findViewById(R.id.user_title)
        val userAvatar: TextView = itemView.findViewById(R.id.user_avatar)
        val title: TextView = itemView.findViewById(R.id.post_title)
        val content: TextView = itemView.findViewById(R.id.post_content)
        val postImage: ImageView = itemView.findViewById(R.id.post_image)
        val likeCount: TextView = itemView.findViewById(R.id.post_like_count)
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
        holder.likeCount.text = formatNumber(post.likeCount)
        holder.date.text = formatDate(post.date)

        post.image?.let {
            BitmapFactory.decodeByteArray(it, 0, it.size)?.let { bitmap ->
                holder.postImage.setImageBitmap(bitmap)
            }
        }

        holder.itemView.setOnClickListener { onItemClick(post) }
    }

    override fun getItemCount(): Int = posts.size

    private fun formatDate(date: LocalDate, pattern: String = "dd/MM/yyyy"): String {
        return try {
            val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
            date.format(formatter)
        } catch (e: Exception) {
            println("Error formatting date: ${e.message}")
            ""
        }
    }

    private fun formatNumber(number: Number, locale: Locale = Locale.getDefault()): String {
        val numberFormat = NumberFormat.getNumberInstance(locale)

        return numberFormat.format(number)
    }
}