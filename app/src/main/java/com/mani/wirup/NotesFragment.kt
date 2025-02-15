package com.mani.wirup

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
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

    // Register for activity result
    private val addNoteLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val note = result.data?.getParcelableExtra<Note>("NOTE")
            note?.let { updatedNote ->
                if (updatedNote.id == 0L) {
                    // Insert new note
                    noteViewModel.insert(updatedNote)
                } else {
                    // Update existing note
                    noteViewModel.update(updatedNote)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notes, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewNotes)
        val adapter = NoteAdapter(
            onNoteClicked = { note ->
                // Launch AddNoteActivity for editing the note
                val intent = Intent(requireContext(), AddNoteActivity::class.java).apply {
                    putExtra("NOTE", note)
                }
                addNoteLauncher.launch(intent)
            },
            onNoteDeleted = { note ->
                // Handle note deletion
                noteViewModel.delete(note.id)
            }
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observe notes from the ViewModel
        noteViewModel.allNotes.observe(viewLifecycleOwner, Observer { notes ->
            notes?.let { adapter.submitList(it) } // Use submitList for ListAdapter
        })

        // Floating Action Button (FAB) for adding a new note
        val fabAddNote = view.findViewById<FloatingActionButton>(R.id.fabAddNote)
        fabAddNote.setOnClickListener {
            // Launch AddNoteActivity for adding a new note
            val intent = Intent(requireContext(), AddNoteActivity::class.java)
            addNoteLauncher.launch(intent)
        }

        return view
    }
}