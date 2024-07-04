package com.mani.wirup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class signupok : AppCompatActivity() {
    private lateinit var btnLogin:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signupok)
        supportActionBar?.hide()
        btnLogin=findViewById(R.id.btnnext2)
        btnLogin.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }

    }
}