package com.mani.wirup

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import java.text.SimpleDateFormat
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    private lateinit var clientViewModel: ClientViewModel
    private lateinit var spinnerClient: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        // Initialize views
        val editTextTitle = findViewById<EditText>(R.id.editTextTitle)
        val editTextDate = findViewById<EditText>(R.id.editTextDate)
        val editTextTime = findViewById<EditText>(R.id.editTextTime)
        val spinnerPriority = findViewById<Spinner>(R.id.spinnerPriority)
        spinnerClient = findViewById(R.id.spinnerClient)
        val editTextDuration = findViewById<EditText>(R.id.editTextDuration)
        val buttonAddTask = findViewById<Button>(R.id.buttonAddTask)

        // Set up priority spinner
        val priorityOptions = arrayOf("High", "Medium", "Low")
        val priorityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorityOptions)
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPriority.adapter = priorityAdapter

        // Set up client spinner
        clientViewModel = ViewModelProvider(this, TaskViewModelFactory(
            TaskRepository(AppDatabase.getDatabase(this).taskDao()),
            NoteRepository(AppDatabase.getDatabase(this).noteDao()),
            ClientRepository(AppDatabase.getDatabase(this).clientDao())
        )).get(ClientViewModel::class.java)

        val clientAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)
        clientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerClient.adapter = clientAdapter

        // Observe clients and populate the spinner
        clientViewModel.allClients.observe(this) { clients ->
            val clientNames = clients.map { it.name }
            clientAdapter.clear()
            clientAdapter.addAll(clientNames)
            clientAdapter.notifyDataSetChanged()
        }

        // Set up date and time pickers
        editTextDate.setOnClickListener { showDatePicker(editTextDate) }
        editTextTime.setOnClickListener { showTimePicker(editTextTime) }

        // Handle "Add Task" button click
        buttonAddTask.setOnClickListener {
            val title = editTextTitle.text.toString()
            val date = editTextDate.text.toString()
            val time = editTextTime.text.toString()
            val priority = spinnerPriority.selectedItem.toString().lowercase()
            val clientName = spinnerClient.selectedItem.toString()
            val duration = editTextDuration.text.toString().toLongOrNull() ?: 0

            if (title.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty() && duration > 0) {
                // Get the selected client's ID
                clientViewModel.allClients.value?.find { it.name == clientName }?.let { client ->
                    val task = Task(
                        title = title,
                        date = date,
                        time = time,
                        priority = priority,
                        clientId = client.id, // Associate task with client
                        duration = duration
                    )

                    // Return the task to the calling activity
                    val resultIntent = Intent().apply {
                        putExtra("TASK", task)
                    }
                    setResult(RESULT_OK, resultIntent)
                    finish()
                } ?: run {
                    Toast.makeText(this, "Please select a client", Toast.LENGTH_SHORT).show()
                }
            } else {
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

    // Show a time picker dialog
    private fun showTimePicker(editTextTime: EditText) {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                val selectedTime = formatTime(hourOfDay, minute)
                editTextTime.setText(selectedTime)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false // Set to true if you want 24-hour format
        )
        timePickerDialog.show()
    }

    // Helper function to format the date as "YYYY-MM-DD"
    private fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    // Helper function to format the time as "HH:MM"
    private fun formatTime(hourOfDay: Int, minute: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return timeFormat.format(calendar.time)
    }
}