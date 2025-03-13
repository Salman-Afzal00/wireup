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

        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_task_start, null)

        val tvTaskDetails = view.findViewById<TextView>(R.id.tvTaskDetails)
        tvTaskDetails.text = "Task: ${task.title} "

        val btnSummarise = view.findViewById<Button>(R.id.btnSummarise)
        btnSummarise.setOnClickListener {

            navigateToNotesFragment()
            dismiss()
        }

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
    }

    private fun navigateToNotesFragment() {

        val fragmentManager: FragmentManager = parentFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, NotesFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }
}