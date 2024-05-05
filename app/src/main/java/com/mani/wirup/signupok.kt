package com.mani.wirup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class signupok : AppCompatActivity() {
    private lateinit var tvEmail:TextView
    private lateinit var btnLogin:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signupok)
        supportActionBar?.hide()
        tvEmail=findViewById(R.id.tvEmail)
        btnLogin=findViewById(R.id.btnLogin)
        val email=intent.getStringExtra("Email").toString()
        tvEmail.text=email
        btnLogin.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

    }
}