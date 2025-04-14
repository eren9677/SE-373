package com.example.mindhook.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindhook.model.Reminder
import com.example.mindhook.network.ApiClient
import kotlinx.coroutines.launch

class ReminderViewModel : ViewModel() {
    private val _reminders = MutableLiveData<List<Reminder>>()
    val reminders: LiveData<List<Reminder>> = _reminders

    fun createReminder(reminder: Reminder) {
        viewModelScope.launch {
            try {
                val response = ApiClient.instance.createReminder(reminder)
                if (response.isSuccessful) {
                    fetchReminders(reminder.userId)
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Create failed", e)
            }
        }
    }

    fun fetchReminders(userId: Int) {
        viewModelScope.launch {
            try {
                val response = ApiClient.instance.getReminders(userId)
                if (response.isSuccessful) {
                    _reminders.postValue(response.body())
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Fetch failed", e)
            }
        }
    }
}