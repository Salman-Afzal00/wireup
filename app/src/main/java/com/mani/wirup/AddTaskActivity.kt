package com.mani.wirup

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class AddTaskActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        // Find views in the layout
        val editTextTitle = findViewById<EditText>(R.id.editTextTitle)
        val editTextDescription = findViewById<EditText>(R.id.editTextDescription)
        val editTextDate = findViewById<EditText>(R.id.editTextDate)
        val editTextTime = findViewById<EditText>(R.id.editTextTime)
        val buttonAddTask = findViewById<Button>(R.id.buttonAddTask)

        // Handle the "Add Task" button click
        buttonAddTask.setOnClickListener {
            val title = editTextTitle.text.toString()
            val description = editTextDescription.text.toString()
            val date = editTextDate.text.toString()
            val time = editTextTime.text.toString()

            // Validate input
            if (title.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty()) {
                // Pass the data back to the calling activity/fragment
                val task = Task(
                    title = title,
                    description = description,
                    date = date,
                    time = time
                )
                val resultIntent = Intent().apply {
                    putExtra("TASK", task)
                }
                setResult(RESULT_OK, resultIntent)
                finish() // Close the activity
            } else {
                // Show an error message if any field is empty
                editTextTitle.error = "Title is required"
                editTextDate.error = "Date is required"
                editTextTime.error = "Time is required"
            }
        }
    }
}