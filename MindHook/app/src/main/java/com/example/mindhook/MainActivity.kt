package com.example.mindhook

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.mindhook.model.Reminder
import com.example.mindhook.viewmodels.ReminderViewModel
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: ReminderViewModel
    private lateinit var adapter: ReminderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup ViewModel
        viewModel = ViewModelProvider(this)[ReminderViewModel::class.java]

        // Setup ListView
        adapter = ReminderAdapter(this, emptyList())
        findViewById<ListView>(R.id.listReminders).adapter = adapter

        // Fetch reminders for user_id=1 (hardcoded for demo)
        viewModel.fetchReminders(1)

        // Observe LiveData
        viewModel.reminders.observe(this) { reminders: List<Reminder> ->
            adapter.updateReminders(reminders)
        }
        // Button click handler
        findViewById<Button>(R.id.btnAddReminder).setOnClickListener {
            viewModel.createReminder(
                Reminder(
                    userId = 1,
                    title = "Test Reminder",
                    dueTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        .format(Date(System.currentTimeMillis() + 3600000)) // 1 hour from now
                )
            )
        }
    }
}

// Adapter for ListView
class ReminderAdapter(
    private val context: Context,
    private var reminders: List<Reminder>
) : BaseAdapter() {
    override fun getCount(): Int = reminders.size
    override fun getItem(position: Int): Reminder = reminders[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_reminder, parent, false)

        val reminder = getItem(position)
        view.findViewById<TextView>(R.id.tvReminderTitle).text = reminder.title
        view.findViewById<TextView>(R.id.tvReminderTime).text = reminder.dueTime

        return view
    }

    fun updateReminders(newReminders: List<Reminder>) {
        this.reminders = newReminders
        notifyDataSetChanged()
    }
}