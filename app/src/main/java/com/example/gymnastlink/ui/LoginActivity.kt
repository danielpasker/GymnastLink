package com.example.gymnastlink.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gymnastlink.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth
        emailText = findViewById(R.id.loginMailTextEdit)
        passwordText = findViewById(R.id.loginPasswordTextEdit)

        findViewById<Button>(R.id.registerButton).setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.loginButton).setOnClickListener {
            login()
        }
    }

    private fun goToApp() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun login() {
        auth.signInWithEmailAndPassword(emailText.text.toString(), passwordText.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext,
                        getString(R.string.authentication_succeeded),
                        Toast.LENGTH_SHORT,).show()

                    goToApp()
                } else {
                    Toast.makeText(baseContext,
                        getString(R.string.authentication_failed),
                        Toast.LENGTH_SHORT,).show()
                }
            }
    }

    public override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            goToApp()
        }
    }
}