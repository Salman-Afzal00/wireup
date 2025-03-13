package com.mani.wirup

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

@Suppress("DEPRECATION")
class  ClientFragment : Fragment() {

    private val clientViewModel: ClientViewModel by viewModels {
        TaskViewModelFactory(
            (requireActivity().application as MyApplication).taskRepository,
            (requireActivity().application as MyApplication).noteRepository,
            (requireActivity().application as MyApplication).clientRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_client, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewClients)
        val adapter = ClientAdapter(emptyList()) { client ->
            val intent = Intent(requireContext(), ClientDetailsActivity::class.java).apply {
                putExtra("CLIENT", client)
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        clientViewModel.allClients.observe(viewLifecycleOwner, Observer { clients ->
            clients?.let { adapter.updateClients(it)
            if (it.isEmpty()){
                view.findViewById<View>(R.id.emptyView).visibility = View.VISIBLE
            }else{
                view.findViewById<View>(R.id.emptyView).visibility = View.GONE
            }
            }
        })

        val fabAddClient = view.findViewById<FloatingActionButton>(R.id.fabAddClient)
        fabAddClient.setOnClickListener {
            showAddClientDialog()
        }

        return view
    }

    private fun showAddClientDialog() {
        val dialog = AddClientDialog(requireContext()) { client ->
            clientViewModel.insert(client)
        }
        dialog.show()
    }
}