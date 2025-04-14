package com.example.mindhook.model
import com.google.gson.annotations.SerializedName

data class Reminder(
    @SerializedName("id") val id: Long? = null, // Nullable for new entries
    @SerializedName("user_id") val userId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("due_time") val dueTime: String,
    @SerializedName("priority") val priority: String = "medium", // Default value
    @SerializedName("is_completed") val isCompleted: Boolean = false
)
