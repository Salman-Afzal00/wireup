package com.mani.wirup

import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var dbuser: DatabaseReference
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        mAuth = FirebaseAuth.getInstance()
        dbuser = FirebaseDatabase.getInstance().getReference()
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Set the default fragment
        replaceFragment(MeetingFragment(), "MeetingFragment")
        bottomNavigationView.selectedItemId = R.id.meeting

        // Set up bottom navigation item selection listener
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.meeting -> {
                    replaceFragment(MeetingFragment(), "MeetingFragment")
                    true
                }
                R.id.calender -> {
                    replaceFragment(EmailFragment(), "CalenderFragment")
                    true
                }
                R.id.client -> {
                    replaceFragment(ClientFragment(), "ClientFragment")
                    true
                }
                R.id.task -> {
                    replaceFragment(TaskFragment(), "TaskFragment")
                    true
                }
                R.id.note -> {
                    replaceFragment(NotesFragment(), "NotesFragment")
                    true
                }
                else -> false
            }
        }

        // Handle back press using OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d("MainActivity", "onBackPressed called")
                showExitConfirmationDialog()
            }
        })
    }

    private fun replaceFragment(fragment: Fragment, tag: String) {
        Log.d("MainActivity", "Replacing fragment with: $tag")
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment, tag)
            .commit()
    }

    private fun showExitConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Exit App")
        builder.setMessage("Are you sure you want to exit the app?")
        builder.setPositiveButton("Yes") { _, _ ->
            finishAffinity() // Close the app completely
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
}
