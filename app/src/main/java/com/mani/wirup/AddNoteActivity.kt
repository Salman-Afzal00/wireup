package com.mani.wirup

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException

class AddNoteActivity : AppCompatActivity() {

    private lateinit var editTextContent: EditText
    private lateinit var buttonSaveNote: ImageButton
    private lateinit var buttonCopyNote: ImageButton
    private lateinit var buttonEmailNote: ImageButton
    private lateinit var buttonRecordVoice: Button
    private lateinit var edtTitle: EditText

    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var audioFile: File? = null

    private var noteId: Long? = null // To track if we're editing an existing note
    private lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)
        supportActionBar?.hide()

        // Initialize views
        editTextContent = findViewById(R.id.editTextContent)
        buttonSaveNote = findViewById(R.id.btnSave)
        buttonCopyNote = findViewById(R.id.btnCopy)
        buttonEmailNote = findViewById(R.id.btnSend)
        buttonRecordVoice = findViewById(R.id.recordButton)
        edtTitle = findViewById(R.id.edtTitle)

        // Initialize ViewModel
        noteViewModel = ViewModelProvider(
            this,
            TaskViewModelFactory(
                (application as MyApplication).taskRepository,
                (application as MyApplication).noteRepository,
                (application as MyApplication).clientRepository
            )
        ).get(NoteViewModel::class.java)

        // Check if we're editing an existing note
        noteId = intent.getLongExtra("NOTE_ID", -1).takeIf { it != -1L }

        if (noteId != null) {
            // Load the existing note
            noteViewModel.getNoteById(noteId!!).observe(this) { note ->
                if (note != null) {
                    edtTitle.setText(note.title)
                    editTextContent.setText(note.content)
                }
            }
        }

        // Handle save button click
        buttonSaveNote.setOnClickListener {
            saveNote()
        }

        // Handle copy button click
        buttonCopyNote.setOnClickListener {
            copyNoteToClipboard()
        }

        // Handle email button click
        buttonEmailNote.setOnClickListener {
            sendNoteViaEmail()
        }

        // Handle record button click
        buttonRecordVoice.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                startRecording()
            }
        }

        // Check for permissions
        checkPermissions()
    }

    private fun saveNote() {
        val title = edtTitle.text.toString()
        val content = editTextContent.text.toString()

        if (title.isNotEmpty() && content.isNotEmpty()) {
            if (noteId != null) {
                // Update existing note
                val updatedNote = Note(
                    id = noteId!!,
                    title = title,
                    content = content
                )
                noteViewModel.update(updatedNote)
                Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show()
            } else {
                // Insert new note
                val newNote = Note(
                    id = 0, // Auto-generated by Room
                    title = title,
                    content = content
                )
                noteViewModel.insert(newNote)
                Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show()
            }
            finish() // Close the activity after saving
        } else {
            Toast.makeText(this, "Please fill the title and content fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun copyNoteToClipboard() {
        val content = editTextContent.text.toString()
        if (content.isNotEmpty()) {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Note Content", content)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Note copied to clipboard", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Note is empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendNoteViaEmail() {
        val content = editTextContent.text.toString()

        if (content.isNotEmpty()) {
            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Note")
                putExtra(Intent.EXTRA_TEXT, content)
            }
            startActivity(Intent.createChooser(emailIntent, "Send Note via Email"))
        } else {
            Toast.makeText(this, "Note is empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_CODE
            )
        }
    }

    private fun startRecording() {
        mediaRecorder = MediaRecorder().apply {
            try {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

                val musicDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC)
                audioFile = File(musicDir, "audio_record.3gp")
                setOutputFile(audioFile?.absolutePath)

                prepare()
                start()
                isRecording = true
                buttonRecordVoice.text = "Stop Recording"
                Log.d("RECORDING", "Recording started. File saved to: ${audioFile?.absolutePath}")
            } catch (e: IOException) {
                e.printStackTrace()
                releaseMediaRecorder()
                Toast.makeText(this@AddNoteActivity, "Recording failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun stopRecording() {
        mediaRecorder?.apply {
            try {
                stop()
            } catch (e: IllegalStateException) {
                Log.e("MediaRecorder", "Stop failed: ${e.message}")
            }
            release()
        }
        mediaRecorder = null
        isRecording = false
        buttonRecordVoice.text = "Start Recording"

        // Upload the recorded file
        audioFile?.let { file ->
            if (file.exists()) {
                Log.d("RECORDING", "File exists: ${file.absolutePath}")
                uploadAudio(file)
            } else {
                Log.e("RECORDING", "File does not exist: ${file.absolutePath}")
                Toast.makeText(this, "Recording file not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun releaseMediaRecorder() {
        mediaRecorder?.release()
        mediaRecorder = null
    }

    private fun uploadAudio(file: File) {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    val requestFile = RequestBody.create(
                        "audio/3gpp".toMediaTypeOrNull(),
                        file
                    )
                    val body = MultipartBody.Part.createFormData(
                        "file",
                        file.name,
                        requestFile
                    )

                    RetrofitClient.instance.uploadAudio(body).execute()
                }

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        Log.d("UPLOAD", "Audio uploaded: ${responseBody.summary}")
                        Log.d("UPLOAD", "Suggestions: ${responseBody.suggestions}")

                        // Convert suggestions to Task objects
                        val suggestedTasks = responseBody.suggestions.map { suggestion ->
                            Task(
                                title = suggestion.title,
                                content = suggestion.description,
                                isPending = false,
                                isCompleted = false,
                                priority = "medium",
                                date = "",
                                duration = 0,
                                addToCalendar = false,
                                clientId = 0,
                                isSuggested = true
                            )
                        }

                        // Insert suggested tasks into the database
                        val taskViewModel = ViewModelProvider(this@AddNoteActivity, TaskViewModelFactory(
                            (application as MyApplication).taskRepository,
                            (application as MyApplication).noteRepository,
                            (application as MyApplication).clientRepository
                        )).get(TaskViewModel::class.java)

                        taskViewModel.insertSuggestedTasks(suggestedTasks)

                        if (!isFinishing && !isDestroyed) {
                            editTextContent.setText(responseBody.summary)
                            Toast.makeText(this@AddNoteActivity, "Audio uploaded successfully", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.e("UPLOAD", "Upload response is null")
                        if (!isFinishing && !isDestroyed) {
                            Toast.makeText(this@AddNoteActivity, "Upload response is null", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("UPLOAD", "Upload failed: $errorBody")
                    if (!isFinishing && !isDestroyed) {
                        Toast.makeText(this@AddNoteActivity, "Upload failed: $errorBody", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("UPLOAD", "Upload failed: ${e.message}")
                if (!isFinishing && !isDestroyed) {
                    Toast.makeText(this@AddNoteActivity, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE = 100
    }
}