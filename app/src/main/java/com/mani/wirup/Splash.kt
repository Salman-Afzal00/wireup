package com.mani.wirup

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth

class Splash : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email)

        mAuth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
    }

    public override fun onStart() {
        super.onStart()

        val isFirstLaunch = sharedPreferences.getBoolean("is_first_launch", true)

        if (isFirstLaunch) {
            // If it's the first launch, navigate to RegisterActivity
            startActivity(Intent(this, Register::class.java))

            // Update SharedPreferences to indicate that the app has been launched before
            sharedPreferences.edit().putBoolean("is_first_launch", false).apply()
        } else {
            // If it's not the first launch, follow the existing logic
            val currentUser = mAuth.currentUser
            if (currentUser != null) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, Register::class.java))
            }
        }

        finish()
    }
}