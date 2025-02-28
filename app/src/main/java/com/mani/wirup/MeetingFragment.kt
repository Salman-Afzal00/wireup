package com.mani.wirup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MeetingFragment : Fragment() {

    private lateinit var meetingTaskAdapter: MeetingTaskAdapter
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var clientRepository: ClientRepository
    private lateinit var dbUser: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var textViewUserName: TextView  // TextView to show user name
    private lateinit var tvUserImage: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_meeting, container, false)
        val gotoCalender = view.findViewById<ImageButton>(R.id.goToCalender)
        gotoCalender.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CalenderFragment()) // Make sure `fragment_container` is correct
                .addToBackStack(null)  // Allows back navigation
                .commit()
        }
        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        dbUser = FirebaseDatabase.getInstance().getReference("user")  // Point to "user" node

        // Initialize UI components
        textViewUserName = view.findViewById(R.id.textViewUserName)
        tvUserImage = view.findViewById(R.id.userImg)

        // Fetch and display user name
        loadUserName()

        // Initialize repositories and ViewModel
        val taskDao = AppDatabase.getDatabase(requireContext()).taskDao()
        val clientDao = AppDatabase.getDatabase(requireContext()).clientDao()
        clientRepository = ClientRepository(clientDao)
        taskViewModel = ViewModelProvider(this, TaskViewModelFactory(
            TaskRepository(taskDao),
            NoteRepository(AppDatabase.getDatabase(requireContext()).noteDao()),
            clientRepository
        )).get(TaskViewModel::class.java)

        // Initialize adapter
        meetingTaskAdapter = MeetingTaskAdapter(clientRepository, requireContext()) { task ->
            showDialog(task)
        }

        // Set up RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewMeeting)
        recyclerView.adapter = meetingTaskAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observe tasks for the current date
        taskViewModel.getTasksForCurrentDate().observe(viewLifecycleOwner) { tasks ->
            meetingTaskAdapter.submitList(tasks)
            if (tasks.isEmpty()) {
                view.findViewById<View>(R.id.emptyView).visibility = View.VISIBLE
            } else {
                view.findViewById<View>(R.id.emptyView).visibility = View.GONE
            }
        }

        return view
    }

    private fun loadUserName() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid  // Get the currently logged-in user's UID

            dbUser.child(userId).child("name").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userName = snapshot.getValue(String::class.java)
                    if (!userName.isNullOrEmpty()) {
                        textViewUserName.text = "$userName"
                        tvUserImage.text = userName.substring(0, 2)
                        // Display name
                    } else {
                        textViewUserName.text = "Welcome"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Failed to load user name", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun showDialog(task: Task) {
        val dialog = TaskStartDialog(task)
        dialog.show(parentFragmentManager, "TaskStartDialog")
    }
}