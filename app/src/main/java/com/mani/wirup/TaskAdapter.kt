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
    private val onTaskClicked: (Task) -> Unit,
    private val showButtons: Boolean = true,
    private val showCompleteButton: Boolean = true // Add this parameter
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
            taskTitle.text = task.title
            taskDate.text = " " + task.date
            taskTime.text = " " + task.time
            tvpriority.text = task.priority

            // Control visibility and enabled state of taskCompleteButton
            if (showCompleteButton) {
                taskCompleteButton.visibility = View.VISIBLE
                taskCompleteButton.isEnabled = true
            } else {
                taskCompleteButton.visibility = View.GONE
                taskCompleteButton.isEnabled = false
            }

            when {
                !task.isPending && !task.isCompleted -> {
                    taskImageButton.visibility = View.VISIBLE
                    taskImageButton.isEnabled = true
                    deleteButton.visibility = View.VISIBLE
                    deleteButton.isEnabled = true
                    tvpriority.visibility = View.GONE
                }
                task.isPending && !task.isCompleted -> {
                    taskImageButton.visibility = View.GONE
                    taskImageButton.isEnabled = false
                    deleteButton.visibility = View.GONE
                    deleteButton.isEnabled = false
                    tvpriority.visibility = View.VISIBLE
                }
                task.isCompleted -> {
                    taskImageButton.visibility = View.GONE
                    taskImageButton.isEnabled = false
                    deleteButton.visibility = View.GONE
                    deleteButton.isEnabled = false
                    tvpriority.visibility = View.GONE
                }
            }

            if (task.isCompleted) {
                taskCompleteButton.setImageResource(R.drawable.ico_complete)
            } else {
                taskCompleteButton.setImageResource(R.drawable.ic_complete)
            }

            taskCompleteButton.setOnClickListener {
                val updatedTask = task.copy(isCompleted = !task.isCompleted, isPending = false)
                onTaskChecked(updatedTask)
            }

            taskImageButton.setOnClickListener {
                val updatedTask = task.copy(isPending = true, isCompleted = false)
                onTaskPending(updatedTask)
            }

            deleteButton.setOnClickListener {
                onTaskDeleted(task)
            }

            if (!task.isPending && !task.isCompleted) {
                itemView.setOnClickListener {
                    onTaskClicked(task)
                }
            } else {
                itemView.setOnClickListener(null)
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