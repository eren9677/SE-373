package com.example.rssreaderapp.network

import com.example.rssreaderapp.model.NewsItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class ApiService {
    private val baseUrl = "http://10.0.2.2:5005"  // localhost from Android emulator

    suspend fun registerNews(title: String) = withContext(Dispatchers.IO) {
        val encodedTitle = URLEncoder.encode(title, "UTF-8")
        makeRequest("$baseUrl/register_news/$encodedTitle", "POST")
    }

    suspend fun getStats(title: String) = withContext(Dispatchers.IO) {
        val encodedTitle = URLEncoder.encode(title, "UTF-8")
        val response = makeRequest("$baseUrl/get_stats/$encodedTitle", "GET")
        val json = JSONObject(response)
        Pair(json.getInt("pos"), json.getInt("neg"))
    }

    suspend fun thumbUp(title: String) = withContext(Dispatchers.IO) {
        val encodedTitle = URLEncoder.encode(title, "UTF-8")
        val response = makeRequest("$baseUrl/thumb_up/$encodedTitle", "POST")
        val json = JSONObject(response)
        Pair(json.getInt("pos"), json.getInt("neg"))
    }

    suspend fun thumbDown(title: String) = withContext(Dispatchers.IO) {
        val encodedTitle = URLEncoder.encode(title, "UTF-8")
        val response = makeRequest("$baseUrl/thumb_down/$encodedTitle", "POST")
        val json = JSONObject(response)
        Pair(json.getInt("pos"), json.getInt("neg"))
    }

    private fun makeRequest(urlString: String, method: String): String {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = method
        
        try {
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return connection.inputStream.bufferedReader().use { it.readText() }
            }
            throw Exception("API request failed with code: $responseCode")
        } finally {
            connection.disconnect()
        }
    }
}