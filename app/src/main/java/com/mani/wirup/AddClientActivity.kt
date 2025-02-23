package com.mani.wirup

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

class AddClientActivity : AppCompatActivity() {
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var audioFile: File? = null
    private lateinit var recordButton: Button

    companion object {
        private const val REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_client)

        // Initialize the record button
        recordButton = findViewById(R.id.recordButton)

        // Check for permissions
        checkPermissions()

        // Set up the record button
        recordButton.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                startRecording()
            }
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted
                Log.d("PERMISSIONS", "Permissions granted")
            } else {
                // Permissions denied
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startRecording() {
        mediaRecorder = MediaRecorder().apply {
            try {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

                // Create the file in the app's external files directory
                val musicDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC)
                audioFile = File(musicDir, "audio_record.3gp")
                setOutputFile(audioFile?.absolutePath)

                prepare()
                start()
                isRecording = true
                recordButton.text = "Stop Recording"
                Log.d("RECORDING", "Recording started. File saved to: ${audioFile?.absolutePath}")
            } catch (e: IOException) {
                e.printStackTrace()
                releaseMediaRecorder()
                Toast.makeText(this@AddClientActivity, "Recording failed: ${e.message}", Toast.LENGTH_SHORT).show()
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
        recordButton.text = "Start Recording"

        // Upload the recorded file
        audioFile?.let { file ->
            if (file.exists()) {
                Log.d("RECORDING", "File exists: ${file.absolutePath}")
                uploadAudio(file)
            } else {
                Log.e("RECORDING", "File does not exist: ${file.absolutePath}")
                Toast.makeText(this@AddClientActivity, "Recording file not found", Toast.LENGTH_SHORT).show()
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
                        Log.d("UPLOAD", "Audio uploaded: ${responseBody.message}")
                        Toast.makeText(this@AddClientActivity, "Audio uploaded successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("UPLOAD", "Upload response is null")
                        Toast.makeText(this@AddClientActivity, "Upload response is null", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("UPLOAD", "Upload failed: $errorBody")
                    Toast.makeText(this@AddClientActivity, "Upload failed: $errorBody", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                Log.e("UPLOAD", "Upload failed: ${t.message}")
                Toast.makeText(this@AddClientActivity, "Upload failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}