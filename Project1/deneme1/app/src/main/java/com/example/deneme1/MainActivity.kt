package com.example.deneme1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize EditTexts
        val editText1 = findViewById<EditText>(R.id.editText1)
        val editText2 = findViewById<EditText>(R.id.editText2)
        val editText3 = findViewById<EditText>(R.id.editText3)

        // Initialize Buttons and set click listeners
        val button1 = findViewById<Button>(R.id.button1)
        button1.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }

        val button2 = findViewById<Button>(R.id.button2)
        button2.setOnClickListener {
            showToast(editText2.text.toString())
        }

        val button3 = findViewById<Button>(R.id.button3)
        button3.setOnClickListener {
            showToast(editText3.text.toString())
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}
