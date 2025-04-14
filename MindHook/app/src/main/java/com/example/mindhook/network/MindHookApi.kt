package com.example.mindhook.network

import com.example.mindhook.model.ApiResponse
import com.example.mindhook.model.Reminder
import com.example.mindhook.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MindHookApi {
    @POST("/register")
    suspend fun register(@Body user: User): Response<ApiResponse>

    @POST("/reminders")
    suspend fun createReminder(@Body reminder: Reminder): Response<ApiResponse>

    @GET("/reminders")
    suspend fun getReminders(@Query("user_id") userId: Int): Response<List<Reminder>>

    @DELETE("/reminders")
    suspend fun deleteAllReminders(@Query("user_id") userId: Int): Response<ApiResponse>
}
