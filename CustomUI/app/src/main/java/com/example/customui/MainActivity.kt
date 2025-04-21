package com.example.customui

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        val circleView = findViewById<CircleView>(R.id.circleView)
        val squareView = findViewById<SquareView>(R.id.squareView)
        val editText = findViewById<EditText>(R.id.editText)
        
        // Handle CircleView events
        circleView.onInnerCircleClick = {
            // Handle inner circle click
            Toast.makeText(this, "inner circle clicked", Toast.LENGTH_SHORT).show()
        }
        
        circleView.onQuadrantClick = { quadrant ->
            // Handle quadrant click
            Toast.makeText(this, "quadrant$quadrant is clicked", Toast.LENGTH_SHORT).show()
        }
        
        // Handle SquareView events
        squareView.onInnerSquareClick = {
            // Handle inner square click
            Toast.makeText(this, "inner square clicked", Toast.LENGTH_SHORT).show()
        }
        
        squareView.onQuadrantClick = { quadrant ->
            // Handle square quadrant click
            Toast.makeText(this, "square$quadrant is clicked", Toast.LENGTH_SHORT).show()
        }
    }
}