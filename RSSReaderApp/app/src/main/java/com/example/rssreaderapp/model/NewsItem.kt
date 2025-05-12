package com.example.rssreaderapp.model

data class NewsItem(
    val title: String,
    val description: String,
    var likes: Int = 0,
    var dislikes: Int = 0
)