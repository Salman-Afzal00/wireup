package com.mani.wirup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class ForgotPassword : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var edtEmailfor:EditText
    private lateinit var btnSubmit:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        supportActionBar?.hide()
        edtEmailfor=findViewById(R.id.edtEmailfor)


        mAuth= FirebaseAuth.getInstance()
        btnSubmit=findViewById(R.id.btnSubmit)
        btnSubmit.setOnClickListener {
            val email=edtEmailfor.text.toString()
            if (email.isNotEmpty()) {
                mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this,
                                "Password Recovery Link is successfully sent to provided email",
                                Toast.LENGTH_SHORT)
                                .show()
                            startActivity(Intent(this,LoginActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                task.exception!!.message.toString(),
                                Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }
            else{
                Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show()
                    }
            }
        }

}