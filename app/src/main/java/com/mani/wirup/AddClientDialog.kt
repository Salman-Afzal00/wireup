package com.mani.wirup

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class AddClientDialog(
    context: Context,
    private val onClientSaved: (Client) -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_add_client)

        // Find views in the layout
        val editTextId = findViewById<EditText>(R.id.editTextId)
        val editTextName = findViewById<EditText>(R.id.editTextName)
        val editTextContact = findViewById<EditText>(R.id.editTextContact)
        val editTextAlternativeContact = findViewById<EditText>(R.id.editTextAlternativeContact)
        val buttonSaveClient = findViewById<Button>(R.id.buttonSaveClient)

        // Handle the "Save Client" button click
        buttonSaveClient.setOnClickListener {
            val id = editTextId.text.toString().toIntOrNull()
            val name = editTextName.text.toString()
            val contact = editTextContact.text.toString()
            val alternativeContact = editTextAlternativeContact.text.toString()

            if (id != null && name.isNotEmpty() && contact.isNotEmpty() && alternativeContact.isNotEmpty()) {
                // Create a new Client object
                val client = Client(
                    id = id,
                    name = name,
                    contact = contact,
                    alternativeContact = alternativeContact
                )

                // Pass the client back to the fragment
                onClientSaved(client)
                dismiss() // Close the dialog
            } else {
                // Show an error message if any field is empty or ID is invalid
                Toast.makeText(context, "Please fill all fields and provide a valid ID", Toast.LENGTH_SHORT).show()
            }
        }
    }
}