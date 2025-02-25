package com.mani.wirup

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import java.text.SimpleDateFormat
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    private lateinit var clientViewModel: ClientViewModel
    private lateinit var spinnerClient: Spinner
    private lateinit var taskViewModel: TaskViewModel
    private var existingTask: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        val editTextTitle = findViewById<EditText>(R.id.editTextTitle)
        val editTextDate = findViewById<EditText>(R.id.editTextDate)
        val editTextTime = findViewById<EditText>(R.id.editTextTime)
        val spinnerPriority = findViewById<Spinner>(R.id.spinnerPriority)
        val detailContent = findViewById<EditText>(R.id.editTextDetail)
        spinnerClient = findViewById(R.id.spinnerClient)
        val editTextDuration = findViewById<EditText>(R.id.editTextDuration)
        val buttonAddTask = findViewById<Button>(R.id.buttonAddTask)

        val priorityOptions = arrayOf("High", "Medium", "Low")
        val priorityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorityOptions)
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPriority.adapter = priorityAdapter

        clientViewModel = ViewModelProvider(this, TaskViewModelFactory(
            TaskRepository(AppDatabase.getDatabase(this).taskDao()),
            NoteRepository(AppDatabase.getDatabase(this).noteDao()),
            ClientRepository(AppDatabase.getDatabase(this).clientDao())
        )).get(ClientViewModel::class.java)

        taskViewModel = ViewModelProvider(this, TaskViewModelFactory(
            TaskRepository(AppDatabase.getDatabase(this).taskDao()),
            NoteRepository(AppDatabase.getDatabase(this).noteDao()),
            ClientRepository(AppDatabase.getDatabase(this).clientDao())
        )).get(TaskViewModel::class.java)

        val clientAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)
        clientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerClient.adapter = clientAdapter

        clientViewModel.allClients.observe(this) { clients ->
            val clientNames = clients.map { it.name }
            clientAdapter.clear()
            clientAdapter.addAll(clientNames)
            clientAdapter.notifyDataSetChanged()
        }

        editTextDate.setOnClickListener { showDatePicker(editTextDate) }
        editTextTime.setOnClickListener { showTimePicker(editTextTime) }

        val taskId = intent.getLongExtra("TASK_ID", -1)
        if (taskId != -1L) {
            taskViewModel.getTaskById(taskId).observe(this) { task ->
                task?.let {
                    existingTask = it
                    editTextTitle.setText(it.title)
                    editTextDate.setText(it.date)
                    editTextTime.setText(it.time)
                    spinnerPriority.setSelection(priorityOptions.indexOf(it.priority.capitalize()))
                    detailContent.setText(it.content)
                    editTextDuration.setText(it.duration.toString())
                }
            }
        }

        buttonAddTask.setOnClickListener {
            val content  = detailContent.text.toString()
            val title = editTextTitle.text.toString()
            val date = editTextDate.text.toString()
            val time = editTextTime.text.toString()
            val priority = spinnerPriority.selectedItem.toString().lowercase()
            val clientName = spinnerClient.selectedItem.toString()
            val duration = editTextDuration.text.toString().toLongOrNull() ?: 0

            if (title.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty() && duration > 0) {
                clientViewModel.allClients.value?.find { it.name == clientName }?.let { client ->
                    val task = existingTask?.copy(
                        title = title,
                        date = date,
                        time = time,
                        content = content,
                        priority = priority,
                        clientId = client.id,
                        duration = duration
                    ) ?: Task(
                        title = title,
                        date = date,
                        time = time,
                        content = content,
                        priority = priority,
                        clientId = client.id,
                        duration = duration
                    )

                    addTaskToCalendar(title, date, time, duration)

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

    private fun addTaskToCalendar(title: String, date: String, time: String, duration: Long) {
        val calendar = Calendar.getInstance()
        val dateTimeString = "$date $time"
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        try {
            calendar.time = dateFormat.parse(dateTimeString)!!
            val startMillis = calendar.timeInMillis
            val endMillis = startMillis + (duration * 60 * 1000)

            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
                putExtra(CalendarContract.Events.TITLE, title)
                putExtra(CalendarContract.Events.DESCRIPTION, "Task scheduled via Wirup App")
                putExtra(CalendarContract.Events.EVENT_LOCATION, "No location specified")
                putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
            }

            startActivity(intent)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Invalid date/time format", Toast.LENGTH_SHORT).show()
        }
    }

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
            false
        )
        timePickerDialog.show()
    }

    private fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun formatTime(hourOfDay: Int, minute: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return timeFormat.format(calendar.time)
    }
}