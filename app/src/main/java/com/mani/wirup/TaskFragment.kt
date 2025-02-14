package com.mani.wirup
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class TaskFragment : Fragment() {

    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory((requireActivity().application as MyApplication).repository)
    }

    // Register for activity result
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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_task, container, false)

        // Initialize RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = TaskAdapter(emptyList()) { task ->
            taskViewModel.update(task)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observe tasks from the ViewModel
        taskViewModel.allTasks.observe(viewLifecycleOwner, Observer { tasks ->
            tasks?.let { adapter.updateTasks(it) }
        })

        // Initialize Floating Action Button (FAB)
        val fabAddTask = view.findViewById<FloatingActionButton>(R.id.fabAddTask)
        fabAddTask.setOnClickListener {
            // Launch the AddTaskActivity
            val intent = Intent(requireContext(), AddTaskActivity::class.java)
            addTaskLauncher.launch(intent)
        }

        return view
    }
}