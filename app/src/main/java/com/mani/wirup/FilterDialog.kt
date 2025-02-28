package com.mani.wirup

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.lifecycle.ViewModelProvider
import java.text.SimpleDateFormat
import java.util.*

class FilterDialog(
    context: Context,
    private val onFilterApplied: (selectedDate: String?, selectedClientId: Int?) -> Unit // Change to Int?
) {
    private val dialog: AlertDialog
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private lateinit var clientViewModel: ClientViewModel
    private var clients: List<Client> = emptyList() // Store the list of clients

    init {
        // Inflate the dialog layout
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_filter, null)

        // Initialize UI components
        val spinnerClient = view.findViewById<Spinner>(R.id.spinnerClient)
        val btnSelectDate = view.findViewById<Button>(R.id.btnSelectDate)
        val btnApplyFilter = view.findViewById<Button>(R.id.btnApplyFilter)
        val btnClearFilter = view.findViewById<Button>(R.id.btnClearFilter)

        // Initialize ViewModel
        clientViewModel = ViewModelProvider(
            (context as androidx.fragment.app.FragmentActivity),
            TaskViewModelFactory(
                TaskRepository(AppDatabase.getDatabase(context).taskDao()),
                NoteRepository(AppDatabase.getDatabase(context).noteDao()),
                ClientRepository(AppDatabase.getDatabase(context).clientDao())
            )
        ).get(ClientViewModel::class.java)

        // Load clients into the spinner
        val clientAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item)
        clientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerClient.adapter = clientAdapter

        // Observe client data
        clientViewModel.allClients.observe(context as androidx.fragment.app.FragmentActivity) { clientList ->
            clients = clientList // Store the list of clients
            val clientNames = clientList.map { it.name }
            clientAdapter.clear()
            clientAdapter.addAll(clientNames)
            clientAdapter.notifyDataSetChanged()
        }

        // Date picker
        var selectedDate: String? = null
        btnSelectDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    val date = formatDate(year, month, dayOfMonth)
                    selectedDate = date
                    btnSelectDate.text = date
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        // Create the dialog
        dialog = AlertDialog.Builder(context)
            .setView(view)
            .create()

        // Apply filter
        btnApplyFilter.setOnClickListener {
            val selectedClientName = spinnerClient.selectedItem as? String
            val selectedClientId = clients.find { it.name == selectedClientName }?.id // Map name to ID
            onFilterApplied(selectedDate, selectedClientId)
            dialog.dismiss()
        }

        // Clear filter
        btnClearFilter.setOnClickListener {
            onFilterApplied(null, null)
            dialog.dismiss()
        }
    }

    fun show() {
        dialog.show()
    }

    private fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        return dateFormat.format(calendar.time)
    }
}