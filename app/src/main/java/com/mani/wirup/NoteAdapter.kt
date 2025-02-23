package com.mani.wirup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class NoteAdapter(
    private val onNoteClicked: (Note) -> Unit,
    private val onNoteDeleted: (Note) -> Unit // Add this callback
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
        private val noteTitle: TextView = itemView.findViewById(R.id.noteTitle)
        //private val noteContent: TextView = itemView.findViewById(R.id.noteContent)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton) // Add this

        fun bind(note: Note) {
            noteTitle.text = note.title
            //noteContent.text = note.content

            // Handle note click (for editing)
            itemView.setOnClickListener {
                onNoteClicked(note)
            }

            // Handle delete button click
            deleteButton.setOnClickListener {
                onNoteDeleted(note)
            }
        }
    }

    // DiffUtil callback to compare notes
    class NoteDiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }
}