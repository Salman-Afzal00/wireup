package com.mani.wirup

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ClientDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_details)
        supportActionBar?.hide()

        // Find views in the layout
        val textViewClient = findViewById<TextView>(R.id.tvClient)
        val textViewId = findViewById<TextView>(R.id.textViewId)
        val textViewName = findViewById<TextView>(R.id.textViewName)
        val textViewContact = findViewById<TextView>(R.id.textViewContact)
        val textViewAlternativeContact = findViewById<TextView>(R.id.textViewAlternativeContact)

        // Get the client from the intent
        val client = intent.getParcelableExtra<Client>("CLIENT")
        if (client != null) {
            textViewId.text = "${client.id}"
            textViewName.text = "Name: ${client.name}"
            textViewContact.text = "${client.contact}"
            textViewAlternativeContact.text = "${client.alternativeContact}"
            textViewClient.text = "${client.name}"

        }
    }
}