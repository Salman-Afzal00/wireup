package com.mani.wirup

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var tvforgot: TextView
    private lateinit var btnlogin: Button
    private lateinit var edtemaillogin: TextInputEditText
    private lateinit var edtpassword: TextInputEditText
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout:TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)
        enableEdgeToEdge()
        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()
        tvforgot = findViewById(R.id.tvforgot)
        edtpassword = findViewById(R.id.edtpassword)
        btnlogin = findViewById(R.id.btnlogin)
        edtemaillogin = findViewById(R.id.edtemaillogin)
        emailInputLayout = findViewById(R.id.emailInputLayout)
        passwordInputLayout = findViewById(R.id.passwordInputLayout)

        // Add focus listener to the email EditText
        edtemaillogin.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                emailInputLayout.elevation = 16f
            } else {
                emailInputLayout.elevation = 0f // Remove elevation when not focused
            }
        }
        edtpassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                passwordInputLayout.elevation = 16f
            } else {
                passwordInputLayout.elevation = 0f // Remove elevation when not focused
            }
        }

        tvforgot.setOnClickListener {
            startActivity(Intent(this, ForgotPassword::class.java))
            finish()
        }

        btnlogin.setOnClickListener {
            val email = edtemaillogin.text.toString()
            val password = edtpassword.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill the fields to login", Toast.LENGTH_SHORT).show()
            } else {
                login(email, password)
            }
        }
    }

    private fun login(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    if (user != null && user.isEmailVerified) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "This email is not verified",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(this, "Some error occurred", Toast.LENGTH_SHORT).show()
                }
            }
    }
}