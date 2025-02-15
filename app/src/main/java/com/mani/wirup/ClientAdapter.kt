package com.mani.wirup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ClientAdapter(
    private var clients: List<Client>,
    private val onClientClicked: (Client) -> Unit
) : RecyclerView.Adapter<ClientAdapter.ClientViewHolder>() {

    // Update the list of clients
    fun updateClients(newClients: List<Client>) {
        clients = newClients
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_client, parent, false)
        return ClientViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        val client = clients[position]
        holder.bind(client)
    }

    override fun getItemCount(): Int = clients.size

    inner class ClientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val clientName: TextView = itemView.findViewById(R.id.clientTitle)
        //private val clientContact: TextView = itemView.findViewById(R.id.textViewContact)
        //private val clientAlternativeContact: TextView = itemView.findViewById(R.id.textViewAlternativeContact)

        fun bind(client: Client) {
            clientName.text = client.name
            //clientContact.text = client.contact
            //clientAlternativeContact.text = client.alternativeContact

            // Handle client click
            itemView.setOnClickListener {
                onClientClicked(client)
            }
        }
    }
}