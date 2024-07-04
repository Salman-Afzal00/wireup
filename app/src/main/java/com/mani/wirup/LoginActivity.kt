package com.mani.wirup

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var tvforgot:TextView
    private lateinit var btnlogin: Button
    private lateinit var edtemaillogin: EditText
    private lateinit var edtpassword:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)
        supportActionBar?.hide()
        mAuth= FirebaseAuth.getInstance()
        tvforgot=findViewById(R.id.tvforgot)
        edtpassword=findViewById(R.id.edtpassword)
        btnlogin=findViewById(R.id.btnlogin)
        edtemaillogin=findViewById(R.id.edtemaillogin)



        tvforgot.setOnClickListener {
            startActivity(Intent(this,ForgotPassword::class.java))
            finish()
        }



        btnlogin.setOnClickListener {
            val email=edtemaillogin.text.toString()
            val password=edtpassword.text.toString()
            if (email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "fill the feilds to login", Toast.LENGTH_SHORT).show()
            }
            else{
                login(email,password)
            }

        }
    }
    private fun login(email:String,password:String){
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    if(mAuth.currentUser!!.isEmailVerified()) {

                       startActivity(Intent(this,MainActivity::class.java))
                        finish()
                    }
                    else
                    {
                        Toast.makeText(this,"This email is not verified",
                            Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this,"some error occured",Toast.LENGTH_SHORT).show()
                }
            }
    }

}