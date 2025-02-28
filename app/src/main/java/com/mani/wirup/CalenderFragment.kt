package com.mani.wirup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.view.ViewContainer
import com.kizitonwose.calendar.view.WeekCalendarView
import com.kizitonwose.calendar.view.WeekDayBinder
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.*

class CalenderFragment : Fragment() {

    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(
            (requireActivity().application as MyApplication).taskRepository,
            (requireActivity().application as MyApplication).noteRepository,
            (requireActivity().application as MyApplication).clientRepository
        )
    }

    private lateinit var weekCalendarView: WeekCalendarView
    private lateinit var recyclerViewTasks: RecyclerView
    private lateinit var emptyView: ImageView
    private var selectedDate: LocalDate? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_calender, container, false)

        // Initialize views
        weekCalendarView = view.findViewById(R.id.weekly)
        recyclerViewTasks = view.findViewById(R.id.recyclerViewTasks)
        emptyView = view.findViewById(R.id.emptyView)
        val addTaskButton = view.findViewById<View>(R.id.addTaskButton)

        // Set click listener to open AddTaskActivity
        addTaskButton.setOnClickListener {
            val dialog = AddTaskDialog()
            dialog.show(parentFragmentManager, "AddTaskDialog")
        }

        // Initialize RecyclerView
        val adapter = TaskAdapter(
            onTaskChecked = { task ->
                val updatedTask = task.copy(isCompleted = !task.isCompleted, isPending = false)
                taskViewModel.update(updatedTask)
            },
            onTaskPending = { task ->
                val updatedTask = task.copy(isPending = true, isCompleted = false)
                taskViewModel.update(updatedTask)
            },
            onTaskDeleted = { task ->
                taskViewModel.delete(task.id)
            },
            onTaskClicked = {},
            showButtons = false,
            showCompleteButton = false // Disable and hide taskCompleteButton
        )

        recyclerViewTasks.adapter = adapter
        recyclerViewTasks.layoutManager = LinearLayoutManager(requireContext())

        // Setup weekly calendar
        val currentDate = LocalDate.now()
        selectedDate = currentDate // Select current date by default

        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        val startDate = currentDate.minusWeeks(1) // Start from a week ago
        val endDate = currentDate.plusWeeks(1)   // End a week ahead

        weekCalendarView.setup(startDate, endDate, firstDayOfWeek)
        weekCalendarView.scrollToDate(currentDate) // Scroll to today's date

        // Bind week days
        weekCalendarView.dayBinder = object : WeekDayBinder<DayViewContainer> {
            override fun create(view: View): DayViewContainer = DayViewContainer(view)

            override fun bind(container: DayViewContainer, day: WeekDay) {
                container.bind(day)
            }
        }

        // Load tasks for the current date initially
        loadTasksForDate(currentDate, adapter)

        return view
    }

    private fun loadTasksForDate(date: LocalDate, adapter: TaskAdapter) {
        val formattedDate = date.toString() // Format as "YYYY-MM-DD"
        taskViewModel.getTasksByDate(formattedDate).observe(viewLifecycleOwner, Observer { tasks ->
            tasks?.let {
                adapter.submitList(it)
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

    inner class DayViewContainer(view: View) : ViewContainer(view) {
        private val layout: LinearLayout = view as LinearLayout
        private val dayText: TextView = view.findViewById(R.id.dayText)
        private val dayNameText: TextView = view.findViewById(R.id.dayNameText)

        fun bind(day: WeekDay) {
            dayText.text = day.date.dayOfMonth.toString()
            dayNameText.text = day.date.dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault())

            // Change text color based on selection
            if (day.date == selectedDate) {
                dayText.setTextColor(resources.getColor(R.color.white, null)) // Selected color
                dayNameText.setTextColor(resources.getColor(R.color.white, null)) // Selected color
                layout.setBackgroundResource(R.drawable.selected_day_bg) // Apply selection background
            } else {
                dayText.setTextColor(resources.getColor(R.color.black, null)) // Default color
                dayNameText.setTextColor(resources.getColor(R.color.black, null)) // Default color
                layout.setBackgroundResource(0) // Remove background
            }

            layout.setOnClickListener {
                selectedDate = day.date
                weekCalendarView.notifyCalendarChanged()
                loadTasksForDate(day.date, recyclerViewTasks.adapter as TaskAdapter)
            }
        }
    }
}