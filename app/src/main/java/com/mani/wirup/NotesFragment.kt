package com.mani.wirup

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.speech.RecognizerIntent
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

class NotesFragment : Fragment() {

    private val noteViewModel: NoteViewModel by viewModels {
        TaskViewModelFactory(
            (requireActivity().application as MyApplication).taskRepository,
            (requireActivity().application as MyApplication).noteRepository,
            (requireActivity().application as MyApplication).clientRepository
        )
    }

    // Views for editing the note
    private lateinit var editTextContent: EditText
    private lateinit var buttonSaveNote: ImageButton
    private lateinit var buttonCopyNote: ImageButton
    private lateinit var buttonEmailNote: ImageButton
    private lateinit var buttonRecordVoice: Button

    // Voice recording variables
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var audioFile: File? = null

    // Register for voice input

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notes, container, false)

        // Initialize note edit view
        editTextContent = view.findViewById(R.id.editTextContent)
        buttonSaveNote = view.findViewById(R.id.btnSave)
        buttonCopyNote = view.findViewById(R.id.btnCopy)
        buttonEmailNote = view.findViewById(R.id.btnSend)
        buttonRecordVoice = view.findViewById(R.id.recordButton)

        // Load the existing note (if any)
        loadNote()

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

        return view
    }

    private fun loadNote() {
        // Observe the note from the ViewModel
        noteViewModel.note.observe(viewLifecycleOwner) { note ->
            if (note != null) {
                // Populate the content field with the note's data
                editTextContent.setText(note.content)
                boldSpecificWords()
            }
        }
    }

    private fun saveNote() {
        val content = editTextContent.text.toString()

        if (content.isNotEmpty()) {
            // Create or update the Note object with the fixed ID
            val note = Note(
                id = 1, // Fixed ID
                title = "", // Empty title
                content = content
            )

            // Save the note
            noteViewModel.insert(note)

            Toast.makeText(requireContext(), "Note saved", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Please fill the content field", Toast.LENGTH_SHORT).show()
        }
    }


    private fun copyNoteToClipboard() {
        val content = editTextContent.text.toString()
        if (content.isNotEmpty()) {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Note Content", content)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), "Note copied to clipboard", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Note is empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendNoteViaEmail() {
        val content = editTextContent.text.toString()

        if (content.isNotEmpty()) {
            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Note") // Default subject
                putExtra(Intent.EXTRA_TEXT, content)
            }
            startActivity(Intent.createChooser(emailIntent, "Send Note via Email"))
        } else {
            Toast.makeText(requireContext(), "Note is empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
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

                // Create the file in the app's external files directory
                val musicDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC)
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
                Toast.makeText(requireContext(), "Recording failed: ${e.message}", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(requireContext(), "Recording file not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun releaseMediaRecorder() {
        mediaRecorder?.release()
        mediaRecorder = null
    }

    private fun uploadAudio(file: File) {
        val requestFile = RequestBody.create(
            "audio/3gpp".toMediaTypeOrNull(),
            file
        )
        val body = MultipartBody.Part.createFormData(
            "file", // Parameter name (should match the API)
            file.name,
            requestFile
        )

        val call = RetrofitClient.instance.uploadAudio(body)
        call.enqueue(object : Callback<UploadResponse> {
            override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        Log.d("UPLOAD", "Audio uploaded: ${responseBody.summary}") // Use summary


                        if (isAdded) { // Check if the fragment is still attached
                            requireActivity().runOnUiThread {
                                editTextContent.setText(responseBody.summary) // Use summary
                                Toast.makeText(requireContext(), "Audio uploaded successfully", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Log.e("UPLOAD", "Fragment is not attached")
                        }
                    } else {
                        Log.e("UPLOAD", "Upload response is null")
                        if (isAdded) {
                            requireActivity().runOnUiThread {
                                Toast.makeText(requireContext(), "Upload response is null", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("UPLOAD", "Upload failed: $errorBody")
                    if (isAdded) {
                        requireActivity().runOnUiThread {
                            Toast.makeText(requireContext(), "Upload failed: $errorBody", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                Log.e("UPLOAD", "Upload failed: ${t.message}")
                if (isAdded) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Upload failed: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
    private fun boldSpecificWords() {
        val wordsToBold = listOf("Note", "Mood and Wellbeing","#", "upload","Sleep","Needs","Suggested Tasks","Summary")
        val originalText = editTextContent.text.toString()
        val spannableStringBuilder = SpannableStringBuilder(originalText)

        for (word in wordsToBold) {
            var startIndex = originalText.indexOf(word, 0)

            while (startIndex != -1) {
                val endIndex = startIndex + word.length
                spannableStringBuilder.setSpan(
                    StyleSpan(Typeface.BOLD),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                startIndex = originalText.indexOf(word, endIndex)
            }
        }
        editTextContent.setText(spannableStringBuilder)
    }

    companion object {
        private const val REQUEST_CODE = 100
    }
}