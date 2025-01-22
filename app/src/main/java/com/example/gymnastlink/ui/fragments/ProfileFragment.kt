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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gymnastlink.R
import com.example.gymnastlink.controller.UserController
import com.example.gymnastlink.model.Post
import com.example.gymnastlink.ui.LoginActivity
import com.example.gymnastlink.ui.MainActivity
import com.example.gymnastlink.ui.adapters.PostAdapter
import com.example.gymnastlink.ui.components.RecyclerWithTitleView
import com.example.gymnastlink.utils.Converters
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var profileImage: ShapeableImageView
    private lateinit var userNameEditText: EditText
    private lateinit var userTitleEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var genderEditText: EditText
    private lateinit var weightEditText: EditText
    private lateinit var heightEditText: EditText

    private lateinit var editFab: ExtendedFloatingActionButton
    private lateinit var saveFab: ExtendedFloatingActionButton

    private lateinit var userPostsView: RecyclerWithTitleView
    private lateinit var postAdapter: PostAdapter
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    companion object {
        var userPosts = mutableListOf<Post>()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.showReturnButtonOnToolbar(false)

        userNameEditText = view.findViewById(R.id.profile_user_name)
        userTitleEditText = view.findViewById(R.id.profile_user_title)
        ageEditText = view.findViewById(R.id.age_edit_text)
        genderEditText = view.findViewById(R.id.gender_edit_text)
        weightEditText = view.findViewById(R.id.weight_edit_text)
        heightEditText = view.findViewById(R.id.height_edit_text)
        userPostsView = view.findViewById(R.id.user_posts_view)
        userPostsView.title.text = getString(R.string.user_posts)
        profileImage = view.findViewById(R.id.profile_user_avatar)

        displayUserData()

        // TODO: Replace with actual user data
        postAdapter = PostAdapter(userPosts, onItemClick = {
            view.findNavController().navigate(R.id.action_profileFragment_to_fragmentPostComment)
        })
        userPostsView.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        userPostsView.recyclerView.adapter = postAdapter

        profileImage.setOnClickListener {
            openImagePicker()
        }
        editFab = view.findViewById<ExtendedFloatingActionButton>(R.id.edit_profile_fab).apply {
            setOnClickListener {
                setIsEditing(true)
            }
        }
        saveFab = view.findViewById<ExtendedFloatingActionButton>(R.id.save_profile_fab).apply {
            setOnClickListener {
                setIsEditing(false)
                saveUpdatedData()
            }
        }
        view.findViewById<Button>(R.id.logout_button).apply {
            setOnClickListener{
                Firebase.auth.signOut()
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
        }

        setIsEditing(false)

        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.data?.let { uri ->
                        handleImageSelection(uri)
                    }
                }
            }
    }

    private fun displayUserData() {
        userNameEditText.setText(MainActivity.user?.userName)
        userTitleEditText.setText(MainActivity.user?.userTitle)
        ageEditText.setText(MainActivity.user?.age.toString())
        genderEditText.setText(MainActivity.user?.gender)
        weightEditText.setText(MainActivity.user?.weight.toString())
        heightEditText.setText(MainActivity.user?.height.toString())

        val imageByteArray = MainActivity.user?.userImg?.let { Converters.decodeImageFromBase64(it) }
        if (imageByteArray != null) {
            BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
                ?.let { bitmap -> profileImage.setImageBitmap(bitmap) }
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
        profileImage.setImageURI(uri)

        val imageByteArray = requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.readBytes()
        }

        val base64Image = imageByteArray?.let { Converters.encodeImageToBase64(it) }
        if (base64Image != null) {
            MainActivity.user?.userImg = base64Image
        }

        saveUpdatedData()
    }

    private fun setIsEditing(isEditing: Boolean) {
        setEditTextEditable(userNameEditText, isEditing)
        setEditTextEditable(userTitleEditText, isEditing)
        setEditTextEditable(ageEditText, isEditing)
        setEditTextEditable(genderEditText, isEditing)
        setEditTextEditable(weightEditText, isEditing)
        setEditTextEditable(heightEditText, isEditing)

        saveFab.visibility = if (isEditing) View.VISIBLE else View.GONE
        editFab.visibility = if (isEditing) View.GONE else View.VISIBLE

    }

    private fun setEditTextEditable(editText: EditText, isEditable: Boolean) {
        editText.apply {
            isEnabled = isEditable
            isFocusable = isEditable
            isFocusableInTouchMode = isEditable
            isCursorVisible = isEditable
            isClickable = isEditable
            background =
                if (isEditable) {
                    getDrawable(context, R.drawable.edit_input_background)
                } else {
                    null
                }
        }
    }

    private fun saveUpdatedData() {
        val userId = MainActivity.user?.userId ?: return

        val updatedData = mapOf(
            "userName" to userNameEditText.text.toString(),
            "userTitle" to userTitleEditText.text.toString(),
            "age" to ageEditText.text.toString().toDoubleOrNull(),
            "weight" to weightEditText.text.toString().toDoubleOrNull(),
            "gender" to genderEditText.text.toString(),
            "height" to heightEditText.text.toString().toDoubleOrNull(),
            "userImg" to MainActivity.user?.userImg
        )

        GlobalScope.launch(Dispatchers.IO) {
            UserController.shared.update(userId, updatedData) {}
        }
    }

}