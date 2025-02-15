package com.mani.wirup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val onTaskChecked: (Task) -> Unit,
    private val onTaskDeleted: (Task) -> Unit,
    private val showButtons: Boolean = true // Add this parameter
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
        private val taskButton: Button = itemView.findViewById(R.id.taskButton)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun bind(task: Task) {
            // Bind task data to views
            taskTitle.text = task.title
            taskDate.text = task.date
            taskTime.text = task.time

            // Set the button visibility and enable/disable state based on showButtons
            if (showButtons) {
                taskButton.visibility = View.VISIBLE
                deleteButton.visibility = View.VISIBLE
                taskButton.isEnabled = true
                deleteButton.isEnabled = true
            } else {
                taskButton.visibility = View.GONE
                deleteButton.visibility = View.GONE
                taskButton.isEnabled = false
                deleteButton.isEnabled = false
            }

            if (task.isCompleted) {
                taskButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add, 0, 0, 0)
            } else {
                taskButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.remove, 0, 0, 0)
            }

            // Update the task's completion status when the Button is clicked
            taskButton.setOnClickListener {
                val updatedTask = task.copy(isCompleted = !task.isCompleted) // Toggle the state
                onTaskChecked(updatedTask)
            }

            // Handle the delete button click
            deleteButton.setOnClickListener {
                onTaskDeleted(task)
            }
        }
    }

    // DiffUtil callback to compare tasks
    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}