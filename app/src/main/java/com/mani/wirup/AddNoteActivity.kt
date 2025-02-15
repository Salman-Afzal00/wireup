package com.mani.wirup

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

@Suppress("DEPRECATION")
class AddNoteActivity : AppCompatActivity() {

    private var note: Note? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        // Find views in the layout
        val editTextTitle = findViewById<EditText>(R.id.editTextTitle)
        val editTextContent = findViewById<EditText>(R.id.editTextContent)
        val buttonSaveNote = findViewById<Button>(R.id.buttonSaveNote)

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
    }
}