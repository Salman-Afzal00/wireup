package com.mani.wirup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SuggestedTasksAdapter(

) : RecyclerView.Adapter<SuggestedTasksAdapter.ViewHolder>() {

    private var suggestedTasks: List<SuggestedTask> = emptyList()

    // Function to update the list of suggested tasks
    fun updateSuggestedTasks(tasks: List<SuggestedTask>) {
        this.suggestedTasks = tasks
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.textViewTaskTitle)
        val descriptionTextView: TextView = itemView.findViewById(R.id.textViewTaskDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_suggested_task, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = suggestedTasks[position]
        holder.titleTextView.text = task.title
        holder.descriptionTextView.text = task.description
    }

    override fun getItemCount(): Int {
        return suggestedTasks.size
    }
}