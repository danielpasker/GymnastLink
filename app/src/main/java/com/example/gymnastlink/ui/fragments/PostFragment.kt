package com.example.gymnastlink.ui.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gymnastlink.R
import com.example.gymnastlink.utils.Converters
import com.example.gymnastlink.model.Post
import com.example.gymnastlink.controller.PostController
import com.example.gymnastlink.ui.MainActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID


class PostFragment : Fragment() {

    private var post: Post? = null
    private lateinit var fragmentTitle: TextView
    private lateinit var postTitle: EditText
    private lateinit var postContent: EditText
    private lateinit var uploadImageButton: Button
    private lateinit var removeImageButton: Button
    private lateinit var deletePostButton: FloatingActionButton
    private lateinit var imageView: ImageView
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private var postImageUri: Uri? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_post, container, false)
        arguments?.let { post = it.getParcelable("post") }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.showReturnButtonOnToolbar(true)

        fragmentTitle = view.findViewById(R.id.new_post_title)
        postTitle = view.findViewById(R.id.editTextPostTitle)
        postContent = view.findViewById(R.id.editTextPostContent)
        imageView = view.findViewById(R.id.imgView)
        uploadImageButton = view.findViewById<Button>(R.id.upload_image_button).apply {
            setOnClickListener { openImagePicker() }
        }
        removeImageButton = view.findViewById<Button>(R.id.remove_image_button).apply {
            setOnClickListener { removeImage() }
        }
        deletePostButton = view.findViewById<FloatingActionButton?>(R.id.delete_post_fab).apply {
            setOnClickListener{ deletePost() }
        }

        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.data?.let { uri ->
                        handleImageSelection(uri)
                        postImageUri = uri
                    }
                }
            }

        view.findViewById<FloatingActionButton>(R.id.save_post_fab).apply {
            setOnClickListener {
                post?.let { updatePost() } ?: run { saveNewPost() }
            }
        }

        post?.let {
           setEditPostFragment(it)
        } ?: run {
            fragmentTitle.text = getString(R.string.new_post_text)
        }

    }

    private fun setEditPostFragment(post : Post) {
        fragmentTitle.text = getString(R.string.edit_post)
        postTitle.setText(post.title)
        postContent.setText(post.content)
        deletePostButton.visibility = FloatingActionButton.VISIBLE


        post.image?.let { image ->
            val imageByteArray = Converters.decodeImageFromBase64(image)
            val bitmap =
                BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap)
                imageView.visibility = ImageView.VISIBLE
                uploadImageButton.visibility = Button.GONE
                removeImageButton.visibility = Button.VISIBLE
            } else {
                removeImage()
            }
        } ?: run {
            removeImage()
        }
    }

    private fun updatePost() {
        var imageByteArray: ByteArray? = null
        if (imageView.isVisible) {
            imageByteArray = postImageUri.let { uri ->
                if (uri != null) {
                    requireContext().contentResolver.openInputStream(uri)?.readBytes()
                } else {
                    post?.image?.let { Converters.decodeImageFromBase64(it) }
                }
            }
        }

        val updatedData = mapOf(
            "title" to postTitle.text.toString(),
            "content" to postContent.text.toString(),
            "image" to imageByteArray?.let { Converters.encodeImageToBase64(it) },
            "date" to LocalDate.now()
        )

        GlobalScope.launch(Dispatchers.IO) {
            post?.let {
                PostController.shared.update(it.postId,updatedData) {
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun saveNewPost() {
        val imageByteArray = postImageUri.let { uri ->
            if (uri != null) {
                requireContext().contentResolver.openInputStream(uri)?.readBytes()
            } else null
        }

        val post = MainActivity.user?.let {
            Post (
                postId = UUID.randomUUID().toString(),
                userId = it.userId,
                userName = it.userName,
                userTitle = it.userTitle,
                title = postTitle.text.toString(),
                content = postContent.text.toString(),
                image = imageByteArray?.let { Converters.encodeImageToBase64(it) },
                date = LocalDate.now()
            )
        }

        if (post != null) {
            PostController.shared.addPost(post) {
                findNavController().navigateUp()
            }
        }
    }

    private fun deletePost() {
        post?.let {
            PostController.shared.deletePost(it) {
                findNavController().navigateUp()
                findNavController().navigateUp()
            }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        pickImageLauncher.launch(intent)
    }

    private fun handleImageSelection(uri: Uri) {
        imageView.setImageURI(uri)
        imageView.visibility = ImageView.VISIBLE
        uploadImageButton.visibility = Button.GONE
        removeImageButton.visibility = Button.VISIBLE
    }

    private fun removeImage() {
        postImageUri = null
        post?.image = null
        imageView.setImageResource(0)
        imageView.visibility = ImageView.GONE
        removeImageButton.visibility = Button.GONE
        uploadImageButton.visibility = Button.VISIBLE
    }
}