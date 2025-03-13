package com.mani.wirup

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class NoteAdapter(
    private val onNoteDeleted: (Note) -> Unit,
    private val onNoteClicked: (Note) -> Unit // Add this parameter for note click handling
) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(NoteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        holder.bind(note)
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNoteTitle: TextView = itemView.findViewById(R.id.noteTitle)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun bind(note: Note) {
            tvNoteTitle.text = note.title

            // Set click listener for the note item
            itemView.setOnClickListener {
                onNoteClicked(note) // Trigger the click handler
            }

            deleteButton.setOnClickListener {
                onNoteDeleted(note)
            }
        }
    }

    class NoteDiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }
}