package com.mani.wirup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private var tasks: List<Task>,
    private val onTaskChecked: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    // Update the tasks list
    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.bind(task)
    }

    override fun getItemCount(): Int = tasks.size

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskTitle: TextView = itemView.findViewById(R.id.taskTitle)
        //private val taskDescription: TextView = itemView.findViewById(R.id.taskDescription)
        private val taskDate: TextView = itemView.findViewById(R.id.taskDate)
        private val taskTime: TextView = itemView.findViewById(R.id.taskTime)
        private val taskCheckBox: CheckBox = itemView.findViewById(R.id.taskCheckBox)

        fun bind(task: Task) {
            taskTitle.text = task.title
            //taskDescription.text = task.description
            taskDate.text = task.date
            taskTime.text = task.time
            taskCheckBox.isChecked = task.isCompleted

            taskCheckBox.setOnCheckedChangeListener { _, isChecked ->
                task.isCompleted = isChecked
                onTaskChecked(task)
            }
        }
    }
}