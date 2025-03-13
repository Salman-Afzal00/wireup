package com.mani.wirup

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.lifecycle.ViewModelProvider
import java.text.SimpleDateFormat
import java.util.*

class FilterDialog(
    context: Context,
    private val onFilterApplied: (selectedDate: String?, selectedClientId: Int?) -> Unit
) {
    private val dialog: AlertDialog
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private lateinit var clientViewModel: ClientViewModel
    private var clients: List<Client> = emptyList()
    private var selectedClientId: Int? = null
    private var selectedClientName: String? = null

    init {
        // Inflate the dialog layout
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_filter, null)

        // Initialize UI components
        val btnSelectClient = view.findViewById<Button>(R.id.btnSelectClient)
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

        // Observe client data
        clientViewModel.allClients.observe(context) { clientList ->
            clients = clientList
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

        // Client selection dialog
        btnSelectClient.setOnClickListener {
            val clientDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_client_list, null)
            val clientListView = clientDialogView.findViewById<ListView>(R.id.clientListView)
            val clientNames = clients.map { it.name }
            val clientAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, clientNames)
            clientListView.adapter = clientAdapter

            val clientDialog = AlertDialog.Builder(context)
                .setView(clientDialogView)
                .setTitle("Select Client")
                .create()

            clientListView.setOnItemClickListener { _, _, position, _ ->
                selectedClientName = clientNames[position]
                selectedClientId = clients[position].id
                btnSelectClient.text = selectedClientName
                clientDialog.dismiss()
            }

            clientDialog.show()
        }

        // Create the main dialog
        dialog = AlertDialog.Builder(context)
            .setView(view)
            .create()

        // Apply filter
        btnApplyFilter.setOnClickListener {
            onFilterApplied(selectedDate, selectedClientId)
            dialog.dismiss()
        }

        // Clear filter
        btnClearFilter.setOnClickListener {
            selectedDate = null
            selectedClientId = null
            selectedClientName = null
            btnSelectDate.text = "Select Deadline"
            btnSelectClient.text = "Select Client"
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