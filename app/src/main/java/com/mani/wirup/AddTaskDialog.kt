package com.mani.wirup

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import java.text.SimpleDateFormat
import java.util.*

class AddTaskDialog : DialogFragment() {

    private lateinit var clientViewModel: ClientViewModel
    private lateinit var spinnerClient: Spinner
    private lateinit var taskViewModel: TaskViewModel
    private var existingTask: Task? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_add_task)

        val editTextTitle = dialog.findViewById<EditText>(R.id.editTextTitle)
        val editTextDate = dialog.findViewById<EditText>(R.id.editTextDate)
        val addToCalendar = dialog.findViewById<CheckBox>(R.id.addtoCalendar)
        val spinnerPriority = dialog.findViewById<Spinner>(R.id.spinnerPriority)
        val detailContent = dialog.findViewById<EditText>(R.id.editTextDetail)
        spinnerClient = dialog.findViewById(R.id.spinnerClient)
        val editTextDuration = dialog.findViewById<EditText>(R.id.editTextDuration)
        val buttonAddTask = dialog.findViewById<Button>(R.id.buttonAddTask)

        val priorityOptions = arrayOf("High", "Medium", "Low")
        val priorityAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, priorityOptions)
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPriority.adapter = priorityAdapter

        clientViewModel = ViewModelProvider(this, TaskViewModelFactory(
            TaskRepository(AppDatabase.getDatabase(requireContext()).taskDao()),
            NoteRepository(AppDatabase.getDatabase(requireContext()).noteDao()),
            ClientRepository(AppDatabase.getDatabase(requireContext()).clientDao())
        )).get(ClientViewModel::class.java)

        taskViewModel = ViewModelProvider(this, TaskViewModelFactory(
            TaskRepository(AppDatabase.getDatabase(requireContext()).taskDao()),
            NoteRepository(AppDatabase.getDatabase(requireContext()).noteDao()),
            ClientRepository(AppDatabase.getDatabase(requireContext()).clientDao())
        )).get(TaskViewModel::class.java)

        val clientAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item)
        clientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerClient.adapter = clientAdapter

        clientViewModel.allClients.observe(this) { clients ->
            val clientNames = clients.map { it.name }
            clientAdapter.clear()
            clientAdapter.addAll(clientNames)
            clientAdapter.notifyDataSetChanged()
        }

        editTextDate.setOnClickListener { showDatePicker(editTextDate) }

        val taskId = arguments?.getLong("TASK_ID", -1) ?: -1
        if (taskId != -1L) {
            taskViewModel.getTaskById(taskId).observe(this) { task ->
                task?.let {
                    existingTask = it
                    editTextTitle.setText(it.title)
                    editTextDate.setText(it.date)
                    spinnerPriority.setSelection(priorityOptions.indexOf(it.priority.capitalize()))
                    detailContent.setText(it.content)
                    editTextDuration.setText(it.duration.toString())
                    addToCalendar.isChecked = it.addToCalendar // Set the checkbox state
                }
            }
        }

        buttonAddTask.setOnClickListener {
            val content = detailContent.text.toString()
            val title = editTextTitle.text.toString()
            val date = editTextDate.text.toString()
            val priority = spinnerPriority.selectedItem.toString().lowercase()
            val clientName = spinnerClient.selectedItem.toString()
            val duration = editTextDuration.text.toString().toLongOrNull() ?: 0
            val addToCalendarChecked = addToCalendar.isChecked // Get the checkbox state

            if (title.isNotEmpty() && date.isNotEmpty() && duration > 0) {
                clientViewModel.allClients.value?.find { it.name == clientName }?.let { client ->
                    val task = existingTask?.copy(
                        title = title,
                        date = date,
                        content = content,
                        priority = priority,
                        clientId = client.id,
                        duration = duration,
                        isPending = true, // New tasks go to Pending section
                        addToCalendar = addToCalendarChecked // Include the checkbox state
                    ) ?: Task(
                        title = title,
                        date = date,
                        content = content,
                        priority = priority,
                        clientId = client.id,
                        duration = duration,
                        isPending = true, // New tasks go to Pending section
                        addToCalendar = addToCalendarChecked // Include the checkbox state
                    )

                    // Add the task to the database
                    taskViewModel.insert(task)

                    // Add the task to the calendar if the checkbox is checked
                    if (addToCalendarChecked) {
                        addTaskToCalendar(title, date, duration)
                    }

                    // Notify the listener (TaskFragment) about the new task
                    (parentFragment as? TaskFragment)?.onTaskAdded(task)
                    dismiss()
                } ?: run {
                    Toast.makeText(requireContext(), "Please select a client", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        return dialog
    }

    private fun addTaskToCalendar(title: String, date: String, duration: Long) {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        try {
            calendar.time = dateFormat.parse(date)!!
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
            Toast.makeText(requireContext(), "Invalid date format", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDatePicker(editTextDate: EditText) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
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

    private fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}