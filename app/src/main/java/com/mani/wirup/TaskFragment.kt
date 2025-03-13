package com.mani.wirup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.view.ViewContainer
import com.kizitonwose.calendar.view.WeekCalendarView
import com.kizitonwose.calendar.view.WeekDayBinder
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.*

class TaskFragment : Fragment() {

    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(
            (requireActivity().application as MyApplication).taskRepository,
            (requireActivity().application as MyApplication).noteRepository,
            (requireActivity().application as MyApplication).clientRepository
        )
    }

    private lateinit var suggestedAdapter: TaskAdapter
    private lateinit var pendingAdapter: TaskAdapter
    private lateinit var completedAdapter: TaskAdapter
    private lateinit var weekCalendarView: WeekCalendarView
    private lateinit var emptyView: ImageView
    private var selectedDate: LocalDate? = null
    private var isDayView: Boolean = true // Set Day View as default

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("TaskFragment", "onCreateView: Loading tasks for current date")
        val view = inflater.inflate(R.layout.fragment_task, container, false)

        // Initialize views
        weekCalendarView = view.findViewById(R.id.weekly)
        val switchView = view.findViewById<androidx.appcompat.widget.SwitchCompat>(R.id.switchView)
        val fabAddTask = view.findViewById<FloatingActionButton>(R.id.fabAddTask)
        emptyView = view.findViewById(R.id.emptyView)

        // Set the switch to "off" by default (Day View)
        switchView.isChecked = false // Switch is off for Day View

        // Set up adapters
        suggestedAdapter = TaskAdapter(
            onTaskChecked = { task -> taskViewModel.update(task) },
            onTaskPending = { task -> taskViewModel.update(task) },
            onTaskDeleted = { task -> taskViewModel.delete(task.id) },
            onTaskClicked = { task -> showTaskDialog(task) },
            showButtons = true,
            showCompleteButton = true
        )

        pendingAdapter = TaskAdapter(
            onTaskChecked = { task -> taskViewModel.update(task) },
            onTaskPending = { task -> taskViewModel.update(task) },
            onTaskDeleted = { task -> taskViewModel.delete(task.id) },
            onTaskClicked = { task -> showTaskDialog(task) },
            showButtons = true
        )

        completedAdapter = TaskAdapter(
            onTaskChecked = { task -> taskViewModel.update(task) },
            onTaskPending = { task -> taskViewModel.update(task) },
            onTaskDeleted = { task -> taskViewModel.delete(task.id) },
            onTaskClicked = { task -> showTaskDialog(task) },
            showButtons = true
        )

        // Set up RecyclerViews
        val suggestedRecyclerView = view.findViewById<RecyclerView>(R.id.suggestedRecyclerView)
        val pendingRecyclerView = view.findViewById<RecyclerView>(R.id.pendingRecyclerView)
        val completedRecyclerView = view.findViewById<RecyclerView>(R.id.completedRecyclerView)

        suggestedRecyclerView.adapter = suggestedAdapter
        pendingRecyclerView.adapter = pendingAdapter
        completedRecyclerView.adapter = completedAdapter

        suggestedRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        pendingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        completedRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observe data
        taskViewModel.suggestedTasks.observe(viewLifecycleOwner, Observer { tasks ->
            tasks?.let { suggestedAdapter.submitList(it) }
        })

        taskViewModel.completedTasks.observe(viewLifecycleOwner, Observer { tasks ->
            tasks?.let {
                if (isDayView) {
                    // Filter completed tasks for the selected date
                    val filteredCompletedTasks = it.filter { task -> task.date == selectedDate.toString() && task.isCompleted }
                    completedAdapter.submitList(filteredCompletedTasks)
                } else {
                    // Show all completed tasks
                    completedAdapter.submitList(it)
                }
            }
        })

        // Set up calendar
        val currentDate = LocalDate.now()
        selectedDate = currentDate
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        val startDate = currentDate.minusWeeks(1)
        val endDate = currentDate.plusWeeks(1)
        weekCalendarView.setup(startDate, endDate, firstDayOfWeek)
        weekCalendarView.scrollToDate(currentDate)

        weekCalendarView.dayBinder = object : WeekDayBinder<DayViewContainer> {
            override fun create(view: View): DayViewContainer = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: WeekDay) {
                container.bind(day)
            }
        }

        // Load tasks for the current date on app startup
        loadTasksForDate(currentDate)

        // Handle Switch state changes
        switchView.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            isDayView = !isChecked // Invert the logic: off = Day View, on = All Tasks
            updateView()
        }

        // Add task FAB
        fabAddTask.setOnClickListener {
            val dialog = AddTaskDialog()
            dialog.show(parentFragmentManager, "AddTaskDialog")
        }

        // Existing functionalities (sort, filter, etc.)
        val btnSortPending = view.findViewById<ImageButton>(R.id.btnSortPending)
        btnSortPending.setOnClickListener {
            sortPendingTasks()
        }

        val btnFilter = view.findViewById<ImageButton>(R.id.btnFilter)
        btnFilter.setOnClickListener {
            showFilterDialog()
        }

        // Toggle visibility of RecyclerViews
        val tvSuggestedTasks = view.findViewById<TextView>(R.id.tvSuggestedTasks)
        val tvPendingTasks = view.findViewById<TextView>(R.id.tvPendingTasks)
        val tvCompletedTasks = view.findViewById<TextView>(R.id.tvCompletedTasks)

        tvSuggestedTasks.setOnClickListener {
            toggleVisibility(suggestedRecyclerView, tvSuggestedTasks)
        }

        tvPendingTasks.setOnClickListener {
            toggleVisibility(pendingRecyclerView, tvPendingTasks)
        }

        tvCompletedTasks.setOnClickListener {
            toggleVisibility(completedRecyclerView, tvCompletedTasks)
        }

        // Delete All Completed Tasks
        val btnDeleteAllCompleted = view.findViewById<ImageButton>(R.id.btnDeleteAllCompleted)
        val tvDeleteAll = view.findViewById<TextView>(R.id.tvDeleteAll)
        btnDeleteAllCompleted.setOnClickListener {
            if (tvDeleteAll.visibility == View.GONE) {
                tvDeleteAll.visibility = View.VISIBLE
            } else {
                tvDeleteAll.visibility = View.GONE
            }
        }

        tvDeleteAll.setOnClickListener {
            deleteAllCompletedTasks()
        }

        return view
    }

    private fun updateView() {
        if (isDayView) {
            // Show Day View (Calendar + Tasks for selected date)
            weekCalendarView.visibility = View.VISIBLE
            loadTasksForDate(selectedDate ?: LocalDate.now())
        } else {
            // Show All Tasks (Existing TaskFragment layout)
            weekCalendarView.visibility = View.GONE
            loadAllPendingTasks()
            loadAllCompletedTasks()
        }
    }

    private fun loadTasksForDate(date: LocalDate) {
        Log.d("TaskFragment", "Loading tasks for date: $date")
        val formattedDate = date.toString()
        taskViewModel.getTasksByDate(formattedDate).observe(viewLifecycleOwner, Observer { tasks ->
            Log.d("TaskFragment", "Tasks loaded for date $formattedDate: ${tasks?.size} tasks")
            tasks?.let {
                // Filter tasks to show only pending tasks for the current date
                val pendingTasks = it.filter { task -> task.isPending }
                pendingAdapter.submitList(pendingTasks)

                // Filter tasks to show only completed tasks for the current date
                val completedTasks = it.filter { task -> task.isCompleted }
                completedAdapter.submitList(completedTasks)

                if (pendingTasks.isEmpty() && completedTasks.isEmpty()) {
                    emptyView.visibility = View.VISIBLE
                } else {
                    emptyView.visibility = View.GONE
                }
            }
        })
    }

    private fun loadAllPendingTasks() {
        taskViewModel.pendingTasks.observe(viewLifecycleOwner, Observer { tasks ->
            tasks?.let {
                pendingAdapter.submitList(it)
                if (it.isEmpty()) {
                    emptyView.visibility = View.VISIBLE
                } else {
                    emptyView.visibility = View.GONE
                }
            }
        })
    }

    private fun loadAllCompletedTasks() {
        taskViewModel.completedTasks.observe(viewLifecycleOwner, Observer { tasks ->
            tasks?.let {
                completedAdapter.submitList(it)
            }
        })
    }

    private fun showTaskDialog(task: Task) {
        val dialog = AddTaskDialog().apply {
            arguments = Bundle().apply {
                putLong("TASK_ID", task.id)
            }
        }
        dialog.show(parentFragmentManager, "AddTaskDialog")
    }

    private fun sortPendingTasks() {
        taskViewModel.pendingTasks.value?.let { tasks ->
            val sortedTasks = tasks.sortedWith(compareBy<Task> { it.priority }.thenBy { it.date })
            pendingAdapter.submitList(sortedTasks)
        }
    }

    private fun showFilterDialog() {
        val filterDialog = FilterDialog(requireContext()) { selectedDate, selectedClient ->
            filterTasks(selectedDate, selectedClient?.toInt())
        }
        filterDialog.show()
    }

    private fun filterTasks(selectedDate: String?, selectedClientId: Int?) {
        taskViewModel.pendingTasks.value?.let { tasks ->
            val filteredTasks = tasks.filter { task ->
                val dateMatches = selectedDate == null || task.date == selectedDate
                val clientMatches = selectedClientId == null || task.clientId == selectedClientId
                dateMatches && clientMatches
            }
            pendingAdapter.submitList(filteredTasks)
        }
    }

    private fun toggleVisibility(recyclerView: RecyclerView, textView: TextView) {
        if (recyclerView.visibility == View.VISIBLE) {
            recyclerView.visibility = View.GONE
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_down, 0, 0, 0)
        } else {
            recyclerView.visibility = View.VISIBLE
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_up, 0, 0, 0)
        }
    }

    private fun deleteAllCompletedTasks() {
        taskViewModel.deleteAllCompletedTasks()
        Toast.makeText(requireContext(), "All completed tasks deleted", Toast.LENGTH_SHORT).show()
    }

    inner class DayViewContainer(view: View) : ViewContainer(view) {
        private val layout: LinearLayout = view as LinearLayout
        private val dayText: TextView = view.findViewById(R.id.dayText)
        private val dayNameText: TextView = view.findViewById(R.id.dayNameText)

        fun bind(day: WeekDay) {
            dayText.text = day.date.dayOfMonth.toString()
            dayNameText.text = day.date.dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault())

            if (day.date == selectedDate) {
                dayText.setTextColor(resources.getColor(R.color.white, null))
                dayNameText.setTextColor(resources.getColor(R.color.white, null))
                layout.setBackgroundResource(R.drawable.selected_day_bg)
            } else {
                dayText.setTextColor(resources.getColor(R.color.black, null))
                dayNameText.setTextColor(resources.getColor(R.color.black, null))
                layout.setBackgroundResource(0)
            }

            layout.setOnClickListener {
                selectedDate = day.date
                weekCalendarView.notifyCalendarChanged()
                loadTasksForDate(day.date)
            }
        }
    }

    fun onTaskAdded(task: Task) {
        taskViewModel.saveTask(task)
        if (isDayView) {
            loadTasksForDate(selectedDate ?: LocalDate.now())
        } else {
            loadAllPendingTasks()
        }
        Toast.makeText(requireContext(), "Task saved", Toast.LENGTH_SHORT).show()
    }
}