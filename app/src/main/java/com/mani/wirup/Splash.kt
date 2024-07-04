package com.mani.wirup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth


class Splash : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email)
        mAuth= FirebaseAuth.getInstance()

    }
    public override fun onStart() {
        super.onStart()
        val currentUser=mAuth.currentUser
        if (currentUser!=null){
            startActivity(Intent(this,MainActivity::class.java))
        }
        else{
            startActivity(Intent(this,Register::class.java))
        }
    }
}