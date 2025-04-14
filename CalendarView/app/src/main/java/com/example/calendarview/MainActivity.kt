package com.example.calendarview

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val btnAddEvent = findViewById<Button>(R.id.btnAddEvent)

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val intent = Intent(this, EventListActivity::class.java)
            intent.putExtra("date", "$dayOfMonth/${month + 1}/$year")
            startActivity(intent)
        }

        btnAddEvent.setOnClickListener {
            val intent = Intent(this, AddEventActivity::class.java)
            startActivity(intent)
        }
    }
}