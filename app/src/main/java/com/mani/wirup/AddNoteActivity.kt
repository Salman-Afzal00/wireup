package com.mani.wirup

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

@Suppress("DEPRECATION")
class AddNoteActivity : AppCompatActivity() {

    private var note: Note? = null

    // Register a launcher for the speech recognition activity
    private val voiceInputLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                val recognizedText = matches[0]
                val editTextContent = findViewById<EditText>(R.id.editTextContent)
                editTextContent.setText(recognizedText)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        // Find views in the layout
        val editTextTitle = findViewById<EditText>(R.id.editTextTitle)
        val editTextContent = findViewById<EditText>(R.id.editTextContent)
        val buttonSaveNote = findViewById<ImageButton>(R.id.btnSave)
        val buttonVoiceInput = findViewById<ImageButton>(R.id.buttonVoiceInput)
        val buttonCopyNote = findViewById<ImageButton>(R.id.btnCopy)
        val buttonEmailNote = findViewById<ImageButton>(R.id.btnSend)

        // Check if a note was passed for editing
        note = intent.getParcelableExtra("NOTE")
        if (note != null) {
            // Populate the fields with the note's data
            editTextTitle.setText(note?.title)
            editTextContent.setText(note?.content)
        }

        // Handle the "Save Note" button click
        buttonSaveNote.setOnClickListener {
            val title = editTextTitle.text.toString()
            val content = editTextContent.text.toString()

            if (title.isNotEmpty() && content.isNotEmpty()) {
                // Create or update the Note object
                val updatedNote = note?.copy(
                    title = title,
                    content = content
                ) ?: Note(
                    title = title,
                    content = content
                )

                // Return the updated/created note to the calling activity
                val resultIntent = Intent().apply {
                    putExtra("NOTE", updatedNote)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                // Show an error message if any field is empty
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle the "Voice Input" button click
        buttonVoiceInput.setOnClickListener {
            startVoiceInput()
        }

        // Handle the "Copy Note" button click
        buttonCopyNote.setOnClickListener {
            val content = editTextContent.text.toString()
            if (content.isNotEmpty()) {
                // Copy the note content to the clipboard
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Note Content", content)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this, "Note copied to clipboard", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Note is empty", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle the "Email Note" button click
        buttonEmailNote.setOnClickListener {
            val title = editTextTitle.text.toString()
            val content = editTextContent.text.toString()

            if (content.isNotEmpty()) {
                // Create an email intent
                val emailIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, title)
                    putExtra(Intent.EXTRA_TEXT, content)
                }
                // Start the email activity
                startActivity(Intent.createChooser(emailIntent, "Send Note via Email"))
            } else {
                Toast.makeText(this, "Note is empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your note")
        }
        voiceInputLauncher.launch(intent)
    }
}