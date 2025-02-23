package com.mani.wirup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val onTaskChecked: (Task) -> Unit,
    private val onTaskPending: (Task) -> Unit,
    private val onTaskDeleted: (Task) -> Unit,
    private val showButtons: Boolean = true
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task)
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskTitle: TextView = itemView.findViewById(R.id.taskTitle)
        private val taskDate: TextView = itemView.findViewById(R.id.taskDate)
        private val taskTime: TextView = itemView.findViewById(R.id.taskTime)
        private val tvpriority: TextView = itemView.findViewById(R.id.tvpriority)
        private val taskCompleteButton: ImageButton = itemView.findViewById(R.id.taskCompleteButton)
        private val taskImageButton: ImageButton = itemView.findViewById(R.id.taskImageButton)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun bind(task: Task) {
            // Bind task data to views
            taskTitle.text = task.title
            taskDate.text = " " + task.date
            taskTime.text = " " + task.time
            tvpriority.text = task.priority

            // Handle visibility and enabled state of buttons and priority text based on task state
            when {
                // Suggested Section
                !task.isPending && !task.isCompleted -> {
                    // taskImageButton: Visible and enabled
                    taskImageButton.visibility = View.VISIBLE
                    taskImageButton.isEnabled = true

                    // taskCompleteButton: Visible and enabled
                    taskCompleteButton.visibility = View.VISIBLE
                    taskCompleteButton.isEnabled = true

                    // deleteButton: Visible and enabled
                    deleteButton.visibility = View.VISIBLE
                    deleteButton.isEnabled = true

                    // priority.tvpriority: Invisible
                    tvpriority.visibility = View.GONE
                }

                // Pending Section
                task.isPending && !task.isCompleted -> {
                    // taskImageButton: Disabled and invisible
                    taskImageButton.visibility = View.GONE
                    taskImageButton.isEnabled = false

                    // taskCompleteButton: Visible and enabled
                    taskCompleteButton.visibility = View.VISIBLE
                    taskCompleteButton.isEnabled = true

                    // deleteButton: Disabled and invisible
                    deleteButton.visibility = View.GONE
                    deleteButton.isEnabled = false

                    // priority.tvpriority: Visible
                    tvpriority.visibility = View.VISIBLE
                }

                // Completed Section
                task.isCompleted -> {
                    // taskImageButton: Disabled and invisible
                    taskImageButton.visibility = View.GONE
                    taskImageButton.isEnabled = false

                    // taskCompleteButton: Visible but disabled
                    taskCompleteButton.visibility = View.VISIBLE
                    taskCompleteButton.isEnabled = false

                    // deleteButton: Disabled and invisible
                    deleteButton.visibility = View.GONE
                    deleteButton.isEnabled = false

                    // priority.tvpriority: Invisible
                    tvpriority.visibility = View.GONE
                }
            }

            // Set the icon for the taskCompleteButton based on the task's completion status
            if (task.isCompleted) {
                taskCompleteButton.setImageResource(R.drawable.ico_complete) // Icon for completed tasks
            } else {
                taskCompleteButton.setImageResource(R.drawable.ic_complete) // Icon for incomplete tasks
            }

            // Mark the task as completed when the taskCompleteButton is clicked
            taskCompleteButton.setOnClickListener {
                val updatedTask = task.copy(isCompleted = !task.isCompleted, isPending = false)
                onTaskChecked(updatedTask)
            }

            // Move the task to the Pending section when the taskImageButton is clicked
            taskImageButton.setOnClickListener {
                val updatedTask = task.copy(isPending = true, isCompleted = false)
                onTaskPending(updatedTask)
            }

            // Delete the task when the deleteButton is clicked
            deleteButton.setOnClickListener {
                onTaskDeleted(task)
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}