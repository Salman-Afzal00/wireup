package com.mani.wirup

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class AddNoteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        // Find views in the layout
        val editTextTitle = findViewById<EditText>(R.id.editTextTitle)
        val editTextContent = findViewById<EditText>(R.id.editTextContent)
        val editTextDate = findViewById<EditText>(R.id.editTextDate)
        val buttonSaveNote = findViewById<Button>(R.id.buttonSaveNote)

        // Set up date picker for the date field
        editTextDate.setOnClickListener {
            showDatePicker(editTextDate)
        }

        // Handle the "Save Note" button click
        buttonSaveNote.setOnClickListener {
            val title = editTextTitle.text.toString()
            val content = editTextContent.text.toString()
            val date = editTextDate.text.toString()

            if (title.isNotEmpty() && content.isNotEmpty() && date.isNotEmpty()) {
                // Create a new Note object
                val note = Note(
                    title = title,
                    content = content,
                    date = date
                )

                // Return the note to the calling activity
                val resultIntent = Intent().apply {
                    putExtra("NOTE", note)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                // Show an error message if any field is empty
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Show a date picker dialog
    private fun showDatePicker(editTextDate: EditText) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = formatDate(year, month, dayOfMonth)
                editTextDate.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    // Helper function to format the date as "YYYY-MM-DD"
    private fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}