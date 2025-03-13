package com.mani.wirup

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MeetingTaskAdapter(
    private val clientRepository: ClientRepository,
    private val context: Context,
    private val onStartNowClicked: (Task) -> Unit
) : ListAdapter<Task, MeetingTaskAdapter.MeetingTaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeetingTaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_meeting_task, parent, false)
        return MeetingTaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: MeetingTaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task)

        val colorResId = when (position % 4) {
            0 -> R.color.task_bg
            1 -> R.color.color2
            2 -> R.color.color3
            3 -> R.color.color4
            else -> R.color.color2
        }
        val backgroundColor = ContextCompat.getColor(context, colorResId)
        val drawable = AppCompatResources.getDrawable(context, R.drawable.meeting_item_bg)?.mutate()
        drawable?.setTint(backgroundColor) // Apply the color as a tint
        holder.itemView.background = drawable
    }

    inner class MeetingTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskTitle: TextView = itemView.findViewById(R.id.taskTitle)
        private val taskTime: TextView = itemView.findViewById(R.id.taskTime)
        private val tvClientName: TextView = itemView.findViewById(R.id.tvClientName)
        private val tvPriority: TextView = itemView.findViewById(R.id.tvPriority)
        private val btnStartNow: Button = itemView.findViewById(R.id.btnStartNow)

        fun bind(task: Task) {
            // Bind task data to views
            taskTitle.text = task.title
            tvPriority.text = task.priority
            taskTime.text = "Today"

            task.clientId?.let { clientId ->
                CoroutineScope(Dispatchers.Main).launch {
                    val client = clientRepository.getClientById(clientId)
                    tvClientName.text = client?.name ?: "Unknown Client"
                }
            }

            btnStartNow.setOnClickListener {
                onStartNowClicked(task)
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