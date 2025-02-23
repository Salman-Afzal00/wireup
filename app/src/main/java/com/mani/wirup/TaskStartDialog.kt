package com.mani.wirup

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

class TaskStartDialog(
    private val task: Task
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Inflate the custom layout
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_task_start, null)

        // Set task details in the TextView
        val tvTaskDetails = view.findViewById<TextView>(R.id.tvTaskDetails)
        tvTaskDetails.text = "Task: ${task.title} "

        // Handle "Summarise" button click
        val btnSummarise = view.findViewById<Button>(R.id.btnSummarise)
        btnSummarise.setOnClickListener {
            // Handle the "Summarise" button click
            navigateToNotesFragment()
            dismiss() // Close the dialog
        }

        // Create the dialog
        return AlertDialog.Builder(requireContext())
            .setView(view) // Set the custom layout
            .create()
    }

    private fun navigateToNotesFragment() {
        // Replace the current fragment with NotesFragment
        val fragmentManager: FragmentManager = parentFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, NotesFragment()) // Replace `fragment_container` with your container ID
        transaction.addToBackStack(null) // Add to back stack for navigation
        transaction.commit()
    }
}