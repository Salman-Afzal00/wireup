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
            (requireActivity().application as MyApplication).noteRepository
        )
    }

    // Register for activity result
    private val addNoteLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val note = result.data?.getParcelableExtra<Note>("NOTE")
            note?.let { noteViewModel.insert(it) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notes, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewNotes)
        val adapter = NoteAdapter(emptyList()) { note ->
            noteViewModel.update(note)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        noteViewModel.allNotes.observe(viewLifecycleOwner, Observer { notes ->
            notes?.let { adapter.updateNotes(it) }
        })

        val fabAddNote = view.findViewById<FloatingActionButton>(R.id.fabAddNote)
        fabAddNote.setOnClickListener {
            // Launch the AddNoteActivity
            val intent = Intent(requireContext(), AddNoteActivity::class.java)
            addNoteLauncher.launch(intent)
        }

        return view
    }
}