package com.example.calendarview

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class EventListActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    private lateinit var listView: ListView
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_list)

        listView = findViewById(R.id.lvEvents)
        val date = intent.getStringExtra("date")
        
        // Set title with date
        title = "Events for $date"
        
        // Fetch events in the background
        coroutineScope.launch {
            try {
                val events = withContext(Dispatchers.IO) {
                    fetchEventsForDate(date)
                }
                
                if (events.isEmpty()) {
                    Toast.makeText(this@EventListActivity, "No events found for this date", Toast.LENGTH_SHORT).show()
                }
                
                // Update UI on main thread
                val adapter = ArrayAdapter<String>(this@EventListActivity, android.R.layout.simple_list_item_1, events)
                listView.adapter = adapter
            } catch (e: Exception) {
                Toast.makeText(this@EventListActivity, "Error loading events: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchEventsForDate(date: String?): List<String> {
        if (date == null) return emptyList()
        
        // Parse date to format expected by the server (from DD/MM/YYYY to YYYY-MM-DD)
        val parts = date.split("/")
        if (parts.size != 3) return emptyList()
        
        val day = parts[0].padStart(2, '0')
        val month = parts[1].padStart(2, '0')
        val year = parts[2]
        val formattedDate = "$year-$month-$day"
        
        val url = "http://192.168.1.107:5001/reminders?date=$formattedDate"
        val request = Request.Builder()
            .url(url)
            .build()
            
        try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            
            if (!response.isSuccessful || responseBody == null) {
                return listOf("Error: ${response.code}")
            }
            
            val jsonArray = JSONArray(responseBody)
            val events = mutableListOf<String>()
            
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                val className = item.getString("class")
                val examDate = item.getString("exam")
                events.add("Class: $className, Exam: $examDate")
            }
            
            return events
        } catch (e: IOException) {
            return listOf("Network error: ${e.message}")
        } catch (e: Exception) {
            return listOf("Error: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
} 