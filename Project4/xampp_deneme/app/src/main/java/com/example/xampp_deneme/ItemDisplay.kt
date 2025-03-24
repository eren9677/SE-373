package com.example.xampp_deneme

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class ItemDisplay : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_display)

        val btnRetrieveItems = findViewById<Button>(R.id.btnRetrieveItems)
        val btnAddItem = findViewById<Button>(R.id.btnAddItem)
        val etItemInput = findViewById<EditText>(R.id.etItemInput)
        val layoutItemsContainer = findViewById<LinearLayout>(R.id.layoutItemsContainer)

        btnRetrieveItems.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val items = retrieveItemsFromServer()
                withContext(Dispatchers.Main) {
                    layoutItemsContainer.removeAllViews()
                    items?.forEach { item ->
                        val tvItem = TextView(this@ItemDisplay).apply {
                            text = "UID: ${item.UID}, Item: ${item.item}"
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            setPadding(8, 8, 8, 8)
                        }
                        layoutItemsContainer.addView(tvItem)
                    }
                }
            }
        }

        btnAddItem.setOnClickListener {
            val itemText = etItemInput.text.toString()
            if (itemText.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    val success = addItemToServer(itemText)
                    withContext(Dispatchers.Main) {
                        if (success) {
                            Toast.makeText(this@ItemDisplay, "Item added successfully", Toast.LENGTH_SHORT).show()
                            etItemInput.text.clear()
                        } else {
                            Toast.makeText(this@ItemDisplay, "Failed to add item", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Please enter an item", Toast.LENGTH_SHORT).show()
            }
        }
    }

    suspend fun retrieveItemsFromServer(): List<ItemData>? {
        val url = URL("http://192.168.1.107:5001/items")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 10000
        connection.readTimeout = 10000

        return if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            parseResponseList(response)
        } else {
            null
        }
    }
    suspend fun addItemToServer(item: String): Boolean {
        var connection: HttpURLConnection? = null

        return try {
            // Setup connection
            val url = URL("http://192.168.1.107:5001/add_item")
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            connection.doInput = true
            connection.doOutput = true

            // Create JSON payload
            val jsonInput = JSONObject().apply {
                put("Item", item)
            }.toString()

            // Set content type and send data
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            connection.outputStream.use { os ->
                val inputBytes = jsonInput.toByteArray(Charsets.UTF_8)
                os.write(inputBytes, 0, inputBytes.size)  // Changed 'length' to 'size'
                os.flush()
            }

            // Check response code
            val responseCode = connection.responseCode
            println("POST Response Code: $responseCode")

            if (responseCode in 200..299) {
                // Success
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                println("Server response: $response")
                true
            } else {
                // Error
                val errorResponse = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "No error details"
                println("Error response code: $responseCode")
                println("Error response: $errorResponse")
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("Exception while adding item: ${e.message}")
            false
        } finally {
            connection?.disconnect()
        }
    }


    fun parseResponseList(response: String?): List<ItemData>? {
        return response?.let {
            try {
                val jsonArray = org.json.JSONArray(it)
                List(jsonArray.length()) { i ->
                    val itemObject = jsonArray.getJSONObject(i)
                    ItemData(
                        UID = itemObject.getInt("UID"),
                        item = itemObject.getString("Item")
                    )
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}

data class ItemData(val UID: Int, val item: String)
