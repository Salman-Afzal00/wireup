package com.mani.wirup

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

@Suppress("DEPRECATION")
class ClientFragment : Fragment() {

    private val clientViewModel: ClientViewModel by viewModels {
        TaskViewModelFactory(
            (requireActivity().application as MyApplication).taskRepository,
            (requireActivity().application as MyApplication).noteRepository,
            (requireActivity().application as MyApplication).clientRepository
        )
    }

    // Register for activity result
    private val addClientLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val client = result.data?.getParcelableExtra<Client>("CLIENT")
            client?.let { updatedClient ->
                if (updatedClient.id.toInt() == 0) {
                    // Insert new client
                    clientViewModel.insert(updatedClient)
                } else {
                    // Update existing client
                    // Assuming you have an update function in your ViewModel
                    clientViewModel.update(updatedClient)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_client, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewClients)
        val adapter = ClientAdapter(emptyList()) { client ->
            val intent = Intent(requireContext(), AddClientActivity::class.java).apply {
                putExtra("CLIENT", client)
            }
            addClientLauncher.launch(intent)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        clientViewModel.allClients.observe(viewLifecycleOwner, Observer { clients ->
            clients?.let { adapter.updateClients(it) }
        })

        val fabAddClient = view.findViewById<FloatingActionButton>(R.id.fabAddClient)
        fabAddClient.setOnClickListener {
            val intent = Intent(requireContext(), AddClientActivity::class.java)
            addClientLauncher.launch(intent)
        }

        return view
    }
}