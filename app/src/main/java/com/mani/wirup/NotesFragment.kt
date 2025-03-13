package com.mani.wirup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NotesFragment : Fragment() {

    private val noteViewModel: NoteViewModel by viewModels {
        TaskViewModelFactory(
            (requireActivity().application as MyApplication).taskRepository,
            (requireActivity().application as MyApplication).noteRepository,
            (requireActivity().application as MyApplication).clientRepository
        )
    }

    private lateinit var noteAdapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notes, container, false)

        // Initialize RecyclerView
        val recyclerViewNotes = view.findViewById<RecyclerView>(R.id.recyclerViewNotes)
        noteAdapter = NoteAdapter(
            onNoteDeleted = { note -> noteViewModel.delete(note.id) },
            onNoteClicked = { note -> navigateToAddNoteActivity(note) } // Handle note clicks
        )
        recyclerViewNotes.adapter = noteAdapter
        recyclerViewNotes.layoutManager = LinearLayoutManager(requireContext())

        // Observe notes from the ViewModel
        noteViewModel.allNotes.observe(viewLifecycleOwner) { notes ->
            notes?.let {
                noteAdapter.submitList(it)
            }
        }

        // Add Note FAB
        val fabAddNote = view.findViewById<FloatingActionButton>(R.id.fabAddNote)
        fabAddNote.setOnClickListener {
            navigateToAddNoteActivity(null) // Pass null to indicate a new note
        }

        return view
    }

    private fun navigateToAddNoteActivity(note: Note?) {
        val intent = Intent(requireContext(), AddNoteActivity::class.java).apply {
            if (note != null) {
                // Pass the note ID to edit the note
                putExtra("NOTE_ID", note.id)
            }
        }
        startActivity(intent)
    }
}