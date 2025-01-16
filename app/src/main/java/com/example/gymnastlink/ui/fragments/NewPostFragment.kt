package com.example.gymnastlink.ui.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gymnastlink.R
import com.example.gymnastlink.model.Converters
import com.example.gymnastlink.model.Post
import com.example.gymnastlink.model.PostModel
import com.example.gymnastlink.ui.MainActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate
import java.util.UUID


class NewPostFragment : Fragment() {

    private lateinit var postTitle: EditText
    private lateinit var postContent: EditText
    private lateinit var uploadImageButton: Button
    private lateinit var removeImageButton: Button
    private lateinit var imageView: ImageView
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var postImageUri: Uri


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.showReturnButtonOnToolbar(true)

        view.findViewById<TextView>(R.id.new_post_title).apply {
            text = getString(R.string.new_post_text)
        }

        postTitle = view.findViewById(R.id.editTextPostTitle)
        postContent = view.findViewById(R.id.editTextPostContent)
        imageView = view.findViewById(R.id.imgView)
        uploadImageButton = view.findViewById<Button>(R.id.upload_image_button).apply {
            setOnClickListener { openImagePicker() }
        }
        removeImageButton = view.findViewById<Button>(R.id.remove_image_button).apply {
            setOnClickListener { removeImage() }
        }

        postImageUri = Uri.parse("android.resource://" + requireContext().packageName + "/drawable/placeholder_image")
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
                val imageByteArray = postImageUri.let {
                    uri -> requireContext().contentResolver.openInputStream(uri)?.readBytes()
                }

                val post = Post (
//                    TODO: Need to get the data from future user implementation
                    userName = "Shay",
                    userTitle = "ShayTitle",
                    postId = UUID.randomUUID().toString(),
                    title = postTitle.text.toString(),
                    content = postContent.text.toString(),
                    image = imageByteArray?.let { Converters.encodeImageToBase64(it) },
                    likeCount = 3,
                    date = LocalDate.now()
                )

                PostModel.shared.addPost(post) {
                    findNavController().navigateUp()
                }
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
        postImageUri = Uri.parse("android.resource://" + requireContext().packageName + "/drawable/placeholder_image")
        imageView.setImageResource(0)
        imageView.visibility = ImageView.GONE
        removeImageButton.visibility = Button.GONE
        uploadImageButton.visibility = Button.VISIBLE
    }
}