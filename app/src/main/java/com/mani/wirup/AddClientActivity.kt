package com.mani.wirup

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddClientActivity : AppCompatActivity() {

    private var client: Client? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_client)

        // Find views in the layout
        val editTextName = findViewById<EditText>(R.id.editTextName)
        val editTextContact = findViewById<EditText>(R.id.editTextContact)
        val editTextAlternativeContact = findViewById<EditText>(R.id.editTextAlternativeContact)
        val buttonSaveClient = findViewById<Button>(R.id.buttonSaveClient)

        // Check if a client was passed for editing
        client = intent.getParcelableExtra("CLIENT")
        if (client != null) {
            // Populate the fields with the client's data
            editTextName.setText(client?.name)
            editTextContact.setText(client?.contact)
            editTextAlternativeContact.setText(client?.alternativeContact)
        }

        // Handle the "Save Client" button click
        buttonSaveClient.setOnClickListener {
            val name = editTextName.text.toString()
            val contact = editTextContact.text.toString()
            val alternativeContact = editTextAlternativeContact.text.toString()

            if (name.isNotEmpty() && contact.isNotEmpty() && alternativeContact.isNotEmpty()) {
                // Create or update the Client object
                val updatedClient = client?.copy(
                    name = name,
                    contact = contact,
                    alternativeContact = alternativeContact
                ) ?: Client(
                    name = name,
                    contact = contact,
                    alternativeContact = alternativeContact
                )

                // Return the updated/created client to the calling activity
                val resultIntent = Intent().apply {
                    putExtra("CLIENT", updatedClient)
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