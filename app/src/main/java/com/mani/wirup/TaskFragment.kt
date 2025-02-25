package com.mani.wirup

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TaskFragment : Fragment() {

    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(
            (requireActivity().application as MyApplication).taskRepository,
            (requireActivity().application as MyApplication).noteRepository,
            (requireActivity().application as MyApplication).clientRepository
        )
    }

    private val addTaskLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = result.data?.getParcelableExtra<Task>("TASK")
            task?.let { taskViewModel.insert(it) }
        }
    }

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
                val intent = Intent(requireContext(), AddTaskActivity::class.java).apply {
                    putExtra("TASK_ID", task.id)
                }
                addTaskLauncher.launch(intent)
            },
            showButtons = true
        )

        val pendingAdapter = TaskAdapter(
            onTaskChecked = { task -> taskViewModel.update(task) },
            onTaskPending = { task -> taskViewModel.update(task) },
            onTaskDeleted = { task -> taskViewModel.delete(task.id) },
            onTaskClicked = { task ->
                val intent = Intent(requireContext(), AddTaskActivity::class.java)
                intent.putExtra("TASK_ID", task.id)
                addTaskLauncher.launch(intent)
            },
            showButtons = true
        )

        val completedAdapter = TaskAdapter(
            onTaskChecked = { task -> taskViewModel.update(task) },
            onTaskPending = { task -> taskViewModel.update(task) },
            onTaskDeleted = { task -> taskViewModel.delete(task.id) },
            onTaskClicked = { task ->
                val intent = Intent(requireContext(), AddTaskActivity::class.java)
                intent.putExtra("TASK_ID", task.id)
                addTaskLauncher.launch(intent)
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
            val intent = Intent(requireContext(), AddTaskActivity::class.java)
            addTaskLauncher.launch(intent)
        }

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

        val btnDeleteAllCompleted = view.findViewById<ImageButton>(R.id.btnDeleteAllCompleted)
        val tvDeleteAll = view.findViewById<TextView>(R.id.tvDeleteAll)
        btnDeleteAllCompleted.setOnClickListener {
            if(tvDeleteAll.visibility == View.GONE){
                tvDeleteAll.visibility = View.VISIBLE
            }else{
                tvDeleteAll.visibility = View.GONE
            }
        }

        tvDeleteAll.setOnClickListener {
            deleteAllCompletedTasks()
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
}