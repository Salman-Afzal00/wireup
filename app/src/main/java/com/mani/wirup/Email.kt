package com.mani.wirup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

@Suppress("DEPRECATION")
class Email : AppCompatActivity() {

    private lateinit var tvsigninn:TextView
    private lateinit var btnnext:Button
    private lateinit var edtName:TextInputEditText
    private lateinit var edtEmail:TextInputEditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email)

        supportActionBar?.hide()


        btnnext=findViewById(R.id.btnnext)

        edtName=findViewById(R.id.edtName)
        edtName.setOnFocusChangeListener { _, hasFocus ->

        }
        edtEmail=findViewById(R.id.edtEmail)
        edtEmail.setOnFocusChangeListener { v, hasFocus ->

        }
        tvsigninn=findViewById(R.id.tvsigninn)
        tvsigninn.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }
        btnnext.setOnClickListener {
         val email=edtEmail.text.toString()
            val name=edtName.text.toString()
            if (email.isNotEmpty() && name.isNotEmpty()){
             intent=Intent(this,Password::class.java)
                intent.putExtra("EMAIL",email)
                intent.putExtra("NAME",name)
                startActivity(intent)

            }
            else{
                Toast.makeText(this, "Fill the Fields!", Toast.LENGTH_SHORT).show()
            }
        }


    }
}