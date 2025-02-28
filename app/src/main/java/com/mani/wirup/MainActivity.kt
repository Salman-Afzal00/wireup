package com.mani.wirup

import android.content.Intent
import android.os.Bundle
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

        // Set the initial fragment
        replaceFragment(MeetingFragment(), "MeetingFragment")
        bottomNavigationView.selectedItemId = R.id.meeting

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.meeting -> {
                    replaceFragment(MeetingFragment(), "MeetingFragment")
                    true
                }
                R.id.calender -> {
                    replaceFragment(CalenderFragment(), "CalenderFragment")
                    true
                }
                R.id.client -> {
                    replaceFragment(ClientFragment(), "ClientFragment")
                    true
                }
                R.id.task -> {
                    replaceFragment(TaskFragment(), "TaskFragment") // Add TaskFragment with a tag
                    true
                }
                R.id.note -> {
                    replaceFragment(NotesFragment(), "NotesFragment")
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment, tag) // Use the tag
            .commit()
    }
}