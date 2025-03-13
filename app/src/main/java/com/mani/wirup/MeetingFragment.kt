package com.mani.wirup

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.time.LocalDate
import java.util.*

class MeetingFragment : Fragment() {
    override fun onResume() {
        super.onResume()
        // Reset status bar color
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.voice_layout_bg)
    }
    override fun onPause() {
        super.onPause()
        // Reset status bar color
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
    }
    private lateinit var meetingTaskAdapter: MeetingTaskAdapter
    private lateinit var currentDateTaskAdapter: TaskAdapter // Adapter for current date tasks
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var clientRepository: ClientRepository
    private lateinit var dbUser: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var textViewUserName: TextView
    private lateinit var tvUserImage: TextView
    private lateinit var tvIntro: TextView
    private lateinit var tvMessage: TextView
    private lateinit var btnRecord: Button
    private lateinit var layoutChat: ConstraintLayout
    private lateinit var layoutElda: ConstraintLayout
    private lateinit var btnClose: ImageButton
    private lateinit var tvMessageSend: TextView
    private lateinit var editTextElda: EditText
    private lateinit var micElda: ImageButton
    private lateinit var sendElda: ImageButton
    private lateinit var recyclerViewMeeting: RecyclerView
    private lateinit var recyclerViewCurrentDateTasks: RecyclerView

    // Request code for speech recognition
    private val REQUEST_CODE_SPEECH_INPUT = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_meeting, container, false)
        val gotoCalender = view.findViewById<ImageButton>(R.id.goToCalender)
        gotoCalender.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, TaskFragment())
                .addToBackStack(null)
                .commit()
        }


        auth = FirebaseAuth.getInstance()
        dbUser = FirebaseDatabase.getInstance().getReference("user")

        textViewUserName = view.findViewById(R.id.textViewUserName)
        tvUserImage = view.findViewById(R.id.userImg)
        tvIntro = view.findViewById(R.id.tvIntro)
        tvMessage = view.findViewById(R.id.tvMessage)
        btnRecord = view.findViewById(R.id.btnRecord)
        layoutChat = view.findViewById(R.id.layoutChat)
        layoutElda = view.findViewById(R.id.layoutElda)
        btnClose = view.findViewById(R.id.btnClose)
        tvMessageSend = view.findViewById(R.id.tvMessageSend)
        editTextElda = view.findViewById(R.id.editTextElda)
        micElda = view.findViewById(R.id.micElda)
        sendElda = view.findViewById(R.id.sendElda)
        recyclerViewMeeting = view.findViewById(R.id.recyclerViewMeeting)
        recyclerViewCurrentDateTasks = view.findViewById(R.id.recyclerViewCurrentDateTasks)

        btnRecord.setOnClickListener {
            layoutChat.visibility = View.VISIBLE
            layoutElda.visibility = View.GONE
        }
        btnClose.setOnClickListener {
            layoutChat.visibility = View.GONE
            layoutElda.visibility = View.VISIBLE
        }

        // Handle micElda click to start speech recognition
        micElda.setOnClickListener {
            startSpeechToText()
        }

        // Handle sendElda click to send message
        sendElda.setOnClickListener {
            val message = editTextElda.text.toString().trim()
            if (message.isNotEmpty()) {
                tvMessageSend.text = message
                editTextElda.text.clear()

                // Update tvMessage and RecyclerView visibility based on the message
                if (message.equals("hello", ignoreCase = true)) {
                    tvMessageSend.visibility = View.VISIBLE
                    Handler(Looper.getMainLooper()).postDelayed({
                        tvMessage.visibility = View.VISIBLE
                        recyclerViewCurrentDateTasks.visibility = View.VISIBLE
                    }, 1000)
                } else {
                    Toast.makeText(requireContext(), "Elda only recognizes 'hello'", Toast.LENGTH_SHORT).show()
                    tvMessageSend.visibility = View.GONE
                    tvMessage.visibility = View.GONE
                    recyclerViewCurrentDateTasks.visibility = View.GONE
                }
            } else {
                Toast.makeText(requireContext(), "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        }

        loadUserName()

        val taskDao = AppDatabase.getDatabase(requireContext()).taskDao()
        val clientDao = AppDatabase.getDatabase(requireContext()).clientDao()
        clientRepository = ClientRepository(clientDao)
        taskViewModel = ViewModelProvider(this, TaskViewModelFactory(
            TaskRepository(taskDao),
            NoteRepository(AppDatabase.getDatabase(requireContext()).noteDao()),
            clientRepository
        )).get(TaskViewModel::class.java)

        // Adapter for meeting tasks
        meetingTaskAdapter = MeetingTaskAdapter(clientRepository, requireContext()) { task ->
            showDialog(task)
        }

        // Adapter for current date tasks
        currentDateTaskAdapter = TaskAdapter(
            onTaskChecked = { task -> taskViewModel.update(task) },
            onTaskPending = { task -> taskViewModel.update(task) },
            onTaskDeleted = { task -> taskViewModel.delete(task.id) },
            onTaskClicked = { task -> showDialog(task) },
            showButtons = true,
            showCompleteButton = true
        )

        // Set up RecyclerView for meeting tasks
        recyclerViewMeeting.adapter = meetingTaskAdapter
        recyclerViewMeeting.layoutManager = LinearLayoutManager(requireContext())

        // Set up RecyclerView for current date tasks
        recyclerViewCurrentDateTasks.adapter = currentDateTaskAdapter
        recyclerViewCurrentDateTasks.layoutManager = LinearLayoutManager(requireContext())

        // Observe meeting tasks
        taskViewModel.getTasksForCurrentDate().observe(viewLifecycleOwner) { tasks ->
            meetingTaskAdapter.submitList(tasks)
            if (tasks.isEmpty()) {
                view.findViewById<View>(R.id.emptyView).visibility = View.VISIBLE
            } else {
                view.findViewById<View>(R.id.emptyView).visibility = View.GONE
            }
        }

        // Fetch and observe tasks for the current date
        val currentDate = LocalDate.now().toString()
        taskViewModel.getTasksByDate(currentDate).observe(viewLifecycleOwner) { tasks ->
            tasks?.let {
//                currentDateTaskAdapter.submitList(it)
                val pendingTasks = it.filter { task -> task.isPending }
                currentDateTaskAdapter.submitList(pendingTasks)
            }
        }

        return view
    }

    private fun loadUserName() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            dbUser.child(userId).child("name").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userName = snapshot.getValue(String::class.java)
                    if (!userName.isNullOrEmpty()) {
                        textViewUserName.text = "$userName"
                        tvUserImage.text = userName.substring(0, 2)
                        tvIntro.text = "Hy $userName, how can I help?"
                        tvMessage.text = "Hy $userName, how are you? I've selected these main tasks for you today based on risk and deadline. I'll help you complete them when you're ready."
                    } else {
                        textViewUserName.text = "Welcome"
                        tvMessage.text = "Hy, how are you? I've selected these main tasks for you today based on risk and deadline. I'll help you complete them when you're ready."
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Failed to load user name", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun showDialog(task: Task) {
        val dialog = TaskStartDialog(task)
        dialog.show(parentFragmentManager, "TaskStartDialog")
    }

    // Start speech-to-text recognition
    private fun startSpeechToText() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Speech recognition not supported on this device", Toast.LENGTH_SHORT).show()
        }
    }

    // Handle the result of speech recognition
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == androidx.fragment.app.FragmentActivity.RESULT_OK && data != null) {
                val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (!result.isNullOrEmpty()) {
                    val recognizedText = result[0]
                    editTextElda.setText(recognizedText)
                }
            }
        }
    }
}