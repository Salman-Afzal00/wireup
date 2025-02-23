package com.mani.wirup

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

class NotesFragment : Fragment() {

    private val noteViewModel: NoteViewModel by viewModels {
        TaskViewModelFactory(
            (requireActivity().application as MyApplication).taskRepository,
            (requireActivity().application as MyApplication).noteRepository,
            (requireActivity().application as MyApplication).clientRepository
        )
    }

    // Views for editing the note
    private lateinit var editTextTitle: EditText
    private lateinit var editTextContent: EditText
    private lateinit var buttonSaveNote: ImageButton
    private lateinit var buttonVoiceInput: ImageButton
    private lateinit var buttonCopyNote: ImageButton
    private lateinit var buttonEmailNote: ImageButton

    // Register for voice input
    private val voiceInputLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                val recognizedText = matches[0]
                editTextContent.setText(recognizedText)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notes, container, false)

        // Initialize note edit view
        editTextTitle = view.findViewById(R.id.editTextTitle)
        editTextContent = view.findViewById(R.id.editTextContent)
        buttonSaveNote = view.findViewById(R.id.btnSave)
        buttonVoiceInput = view.findViewById(R.id.buttonVoiceInput)
        buttonCopyNote = view.findViewById(R.id.btnCopy)
        buttonEmailNote = view.findViewById(R.id.btnSend)

        // Load the existing note (if any)
        loadNote()

        // Handle save button click
        buttonSaveNote.setOnClickListener {
            saveNote()
        }

        // Handle voice input button click
        buttonVoiceInput.setOnClickListener {
            startVoiceInput()
        }

        // Handle copy button click
        buttonCopyNote.setOnClickListener {
            copyNoteToClipboard()
        }

        // Handle email button click
        buttonEmailNote.setOnClickListener {
            sendNoteViaEmail()
        }

        return view
    }

    private fun loadNote() {
        // Observe the note from the ViewModel
        noteViewModel.note.observe(viewLifecycleOwner) { note ->
            if (note != null) {
                // Populate the fields with the note's data
                editTextTitle.setText(note.title)
                editTextContent.setText(note.content)
            }
        }
    }

    private fun saveNote() {
        val title = editTextTitle.text.toString()
        val content = editTextContent.text.toString()

        if (title.isNotEmpty() && content.isNotEmpty()) {
            // Create or update the Note object with the fixed ID
            val note = Note(
                id = 1, // Fixed ID
                title = title,
                content = content
            )

            // Save the note
            noteViewModel.insert(note)

            Toast.makeText(requireContext(), "Note saved", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your note")
        }
        voiceInputLauncher.launch(intent)
    }

    private fun copyNoteToClipboard() {
        val content = editTextContent.text.toString()
        if (content.isNotEmpty()) {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Note Content", content)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), "Note copied to clipboard", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Note is empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendNoteViaEmail() {
        val title = editTextTitle.text.toString()
        val content = editTextContent.text.toString()

        if (content.isNotEmpty()) {
            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, content)
            }
            startActivity(Intent.createChooser(emailIntent, "Send Note via Email"))
        } else {
            Toast.makeText(requireContext(), "Note is empty", Toast.LENGTH_SHORT).show()
        }
    }
}