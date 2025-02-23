package com.mani.wirup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MeetingFragment : Fragment() {

    private lateinit var meetingTaskAdapter: MeetingTaskAdapter
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var clientRepository: ClientRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_meeting, container, false)

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
            // Handle "Start Now" button click
            showDialog(task)
        }

        // Set up RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewMeeting)
        recyclerView.adapter = meetingTaskAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observe tasks for the meeting fragment
        taskViewModel.getTasksForMeeting().observe(viewLifecycleOwner) { tasks ->
            meetingTaskAdapter.submitList(tasks)
        }

        return view
    }

    private fun showDialog(task: Task) {
        val dialog = TaskStartDialog(task)
        dialog.show(parentFragmentManager, "TaskStartDialog")
    }
}