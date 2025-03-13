package com.mani.wirup

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mani.wirup.DateUtils.addTaskToCalendar

class TaskAdapter(
    private val onTaskChecked: (Task) -> Unit,
    private val onTaskPending: (Task) -> Unit,
    private val onTaskDeleted: (Task) -> Unit,
    private val onTaskClicked: (Task) -> Unit,
    private val showButtons: Boolean = true,
    private val showCompleteButton: Boolean = true
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
        private val taskAddToCalendar: TextView = itemView.findViewById(R.id.taskAddToCalendar)
        private val tvDay: TextView = itemView.findViewById(R.id.taskDay)
        private val tvpriority: TextView = itemView.findViewById(R.id.tvpriority)
        private val taskCompleteButton: ImageButton = itemView.findViewById(R.id.taskCompleteButton)
        private val taskImageButton: ImageButton = itemView.findViewById(R.id.taskImageButton)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
        private val tvCompleted: TextView = itemView.findViewById(R.id.tvCompleted)

        fun bind(task: Task) {
            taskTitle.text = task.title
            tvpriority.text = task.priority

            // Handle empty or invalid date strings
            if (task.date.isNullOrEmpty()) {
                tvDay.text = "No Date" // Set a default value for empty dates
            } else {
                when {
                    DateUtils.isToday(task.date) -> tvDay.text = "Today"
                    DateUtils.isThisWeek(task.date) -> tvDay.text = "This week"
                    DateUtils.isPastDate(task.date) -> tvDay.text = "Past"
                    else -> tvDay.text = "Upcoming" // No label for other dates
                }
            }

            if (!task.addToCalendar) {
                taskAddToCalendar.text = "Add to calendar"
                taskAddToCalendar.visibility = View.VISIBLE
            } else {
                taskAddToCalendar.visibility = View.GONE
            }

            taskAddToCalendar.setOnClickListener {
                val success = DateUtils.addTaskToCalendar(itemView.context, task.title, task.date, task.duration)
                if (success) {
                    val updatedTask = task.copy(addToCalendar = true)
                    onTaskPending(updatedTask) // Update the task in the database
                } else {
                    Toast.makeText(itemView.context, "Failed to add task to calendar", Toast.LENGTH_SHORT).show()
                }
            }

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
                    tvDay.visibility= View.GONE
                    taskAddToCalendar.visibility=View.GONE
                    tvpriority.visibility = View.GONE
                    tvCompleted.visibility = View.GONE
                }
                task.isPending && !task.isCompleted -> {
                    taskImageButton.visibility = View.GONE
                    taskImageButton.isEnabled = false
                    deleteButton.visibility = View.GONE
                    deleteButton.isEnabled = false
                    tvpriority.visibility = View.VISIBLE
                    tvCompleted.visibility = View.GONE
                }
                task.isCompleted -> {
                    taskImageButton.visibility = View.GONE
                    taskImageButton.isEnabled = false
                    deleteButton.visibility = View.GONE
                    deleteButton.isEnabled = false
                    tvpriority.visibility = View.GONE
                    tvCompleted.visibility = View.VISIBLE
                    tvDay.visibility= View.GONE
                    taskAddToCalendar.visibility=View.GONE
                }
            }

            if (task.isCompleted) {
                taskCompleteButton.setImageResource(R.drawable.ico_complete)
            } else {
                taskCompleteButton.setImageResource(R.drawable.ic_complete)
            }

            taskCompleteButton.setOnClickListener {
                val updatedTask = task.copy(isCompleted = !task.isCompleted, isPending = false, isSuggested = false)
                onTaskChecked(updatedTask)
            }

            taskImageButton.setOnClickListener {
                val updatedTask = task.copy(isPending = true, isCompleted = false, isSuggested = false)
                onTaskPending(updatedTask)
            }

            deleteButton.setOnClickListener {
                onTaskDeleted(task)
            }

            if (!task.isSuggested && !task.isCompleted) {
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