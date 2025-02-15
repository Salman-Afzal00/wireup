package com.mani.wirup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class CalenderFragment : Fragment() {

    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(
            (requireActivity().application as MyApplication).taskRepository,
            (requireActivity().application as MyApplication).noteRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_calender, container, false)

        // Initialize CalendarView
        val calendarView = view.findViewById<CalendarView>(R.id.calendarView)
        val recyclerViewTasks = view.findViewById<RecyclerView>(R.id.recyclerViewTasks)
        val emptyView = view.findViewById<ImageView>(R.id.emptyView) // TextView to show "Empty" message

        val adapter = TaskAdapter(emptyList()) { task ->
            taskViewModel.update(task)
        }
        recyclerViewTasks.adapter = adapter
        recyclerViewTasks.layoutManager = LinearLayoutManager(requireContext())

        // Get the current date
        val currentDate = getCurrentDate()

        // Load tasks for the current date by default
        taskViewModel.getTasksByDate(currentDate).observe(viewLifecycleOwner, Observer { tasks ->
            tasks?.let {
                adapter.updateTasks(it)
                // Show "Empty" message if no tasks are available
                if (it.isEmpty()) {
                    recyclerViewTasks.visibility = View.GONE
                    emptyView.visibility = View.VISIBLE
                } else {
                    recyclerViewTasks.visibility = View.VISIBLE
                    emptyView.visibility = View.GONE
                }
            }
        })

        // Handle date selection
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = formatDate(year, month, dayOfMonth)
            taskViewModel.getTasksByDate(selectedDate).observe(viewLifecycleOwner, Observer { tasks ->
                tasks?.let {
                    adapter.updateTasks(it)
                    // Show "Empty" message if no tasks are available
                    if (it.isEmpty()) {
                        recyclerViewTasks.visibility = View.GONE
                        emptyView.visibility = View.VISIBLE
                    } else {
                        recyclerViewTasks.visibility = View.VISIBLE
                        emptyView.visibility = View.GONE
                    }
                }
            })
        }

        return view
    }

    // Helper function to format the date as "YYYY-MM-DD"
    private fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    // Helper function to get the current date in "YYYY-MM-DD" format
    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}