package com.example.calendarview

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class AddEventActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    private val JSON = "application/json; charset=utf-8".toMediaType()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)

        val etEventName = findViewById<EditText>(R.id.etEventName)
        val etEventType = findViewById<EditText>(R.id.etEventType)
        val btnSaveEvent = findViewById<Button>(R.id.btnSaveEvent)

        title = "Add New Event"

        btnSaveEvent.setOnClickListener {
            val className = etEventName.text.toString().trim()
            val examDate = etEventType.text.toString().trim()

            if (className.isEmpty() || examDate.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Disable button to prevent multiple submissions
            btnSaveEvent.isEnabled = false

            // Save event in background
            coroutineScope.launch {
                try {
                    val result = withContext(Dispatchers.IO) {
                        saveEvent(className, examDate)
                    }
                    
                    Toast.makeText(this@AddEventActivity, result, Toast.LENGTH_SHORT).show()
                    
                    if (result.startsWith("Event saved")) {
                        finish() // Close activity and return
                    } else {
                        // Re-enable button if there was an error
                        btnSaveEvent.isEnabled = true
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@AddEventActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    btnSaveEvent.isEnabled = true
                }
            }
        }
    }

    private fun saveEvent(className: String, examDate: String): String {
        val url = "http://192.168.1.107:5001/reminders"
        
        val jsonObject = JSONObject().apply {
            put("class", className)
            put("exam", examDate)
        }
        
        val requestBody = jsonObject.toString().toRequestBody(JSON)
        
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
            
        try {
            val response = client.newCall(request).execute()
            
            return if (response.isSuccessful) {
                "Event saved successfully"
            } else {
                "Failed to save event: ${response.code}"
            }
        } catch (e: IOException) {
            return "Network error: ${e.message}"
        } catch (e: Exception) {
            return "Error: ${e.message}"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
} 