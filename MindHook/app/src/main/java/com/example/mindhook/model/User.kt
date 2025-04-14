package com.example.mindhook.model

data class User(
    val id: Int? = null,
    val email: String,
    val passwordHash: String? = null
)