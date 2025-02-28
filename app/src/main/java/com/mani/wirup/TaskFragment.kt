package com.mani.wirup

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class TaskFragment : Fragment() {

    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(
            (requireActivity().application as MyApplication).taskRepository,
            (requireActivity().application as MyApplication).noteRepository,
            (requireActivity().application as MyApplication).clientRepository
        )
    }

    private lateinit var pendingAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task, container, false)

        val suggestedRecyclerView = view.findViewById<RecyclerView>(R.id.suggestedRecyclerView)
        val pendingRecyclerView = view.findViewById<RecyclerView>(R.id.pendingRecyclerView)
        val completedRecyclerView = view.findViewById<RecyclerView>(R.id.completedRecyclerView)

        val suggestedAdapter = TaskAdapter(
            onTaskChecked = { task -> taskViewModel.update(task) },
            onTaskPending = { task -> taskViewModel.update(task) },
            onTaskDeleted = { task -> taskViewModel.delete(task.id) },
            onTaskClicked = { task ->
                val dialog = AddTaskDialog().apply {
                    arguments = Bundle().apply {
                        putLong("TASK_ID", task.id)
                    }
                }
                dialog.show(parentFragmentManager, "AddTaskDialog")
            },
            showButtons = true
        )

        pendingAdapter = TaskAdapter(
            onTaskChecked = { task -> taskViewModel.update(task) },
            onTaskPending = { task -> taskViewModel.update(task) },
            onTaskDeleted = { task -> taskViewModel.delete(task.id) },
            onTaskClicked = { task ->
                val dialog = AddTaskDialog().apply {
                    arguments = Bundle().apply {
                        putLong("TASK_ID", task.id)
                    }
                }
                dialog.show(parentFragmentManager, "AddTaskDialog")
            },
            showButtons = true
        )

        val completedAdapter = TaskAdapter(
            onTaskChecked = { task -> taskViewModel.update(task) },
            onTaskPending = { task -> taskViewModel.update(task) },
            onTaskDeleted = { task -> taskViewModel.delete(task.id) },
            onTaskClicked = { task ->
                val dialog = AddTaskDialog().apply {
                    arguments = Bundle().apply {
                        putLong("TASK_ID", task.id)
                    }
                }
                dialog.show(parentFragmentManager, "AddTaskDialog")
            },
            showButtons = true
        )

        suggestedRecyclerView.adapter = suggestedAdapter
        pendingRecyclerView.adapter = pendingAdapter
        completedRecyclerView.adapter = completedAdapter

        suggestedRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        pendingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        completedRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        taskViewModel.suggestedTasks.observe(viewLifecycleOwner, Observer { tasks ->
            tasks?.let { suggestedAdapter.submitList(it) }
        })

        taskViewModel.pendingTasks.observe(viewLifecycleOwner, Observer { tasks ->
            tasks?.let { pendingAdapter.submitList(it) }
        })

        taskViewModel.completedTasks.observe(viewLifecycleOwner, Observer { tasks ->
            tasks?.let { completedAdapter.submitList(it) }
        })

        val fabAddTask = view.findViewById<FloatingActionButton>(R.id.fabAddTask)
        fabAddTask.setOnClickListener {
            val dialog = AddTaskDialog()
            dialog.show(parentFragmentManager, "AddTaskDialog")
        }

        // Restore toggle visibility functionality
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

        // Restore delete all completed tasks functionality
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

        // Add sorting functionality
        val btnSortPending = view.findViewById<ImageButton>(R.id.btnSortPending)
        btnSortPending.setOnClickListener {
            sortPendingTasks()
        }

        // Add filtering functionality
        val btnFilter = view.findViewById<ImageButton>(R.id.btnFilter)
        btnFilter.setOnClickListener {
            showFilterDialog()
        }

        return view
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
            Log.d("FilterDialog", "Total tasks before filtering: ${tasks.size}")
            Log.d("FilterDialog", "Selected Date: $selectedDate, Selected Client ID: $selectedClientId")

            val filteredTasks = tasks.filter { task ->
                // Log task details for debugging
                Log.d("FilterDialog", "Task: ${task.title}, Date: ${task.date}, Client ID: ${task.clientId}")

                // Compare dates
                val dateMatches = selectedDate == null || task.date == selectedDate

                // Compare client IDs
                val clientMatches = selectedClientId == null || task.clientId == selectedClientId

                // Log filter results for debugging
                Log.d("FilterDialog", "Date Matches: $dateMatches, Client Matches: $clientMatches")

                // Both conditions must be true
                dateMatches && clientMatches
            }

            Log.d("FilterDialog", "Total tasks after filtering: ${filteredTasks.size}")
            pendingAdapter.submitList(filteredTasks)
        }
    }

    fun onTaskAdded(task: Task) {
        taskViewModel.insert(task)
    }
}