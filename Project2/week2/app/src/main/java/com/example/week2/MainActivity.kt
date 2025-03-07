package com.example.week2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnRelative).setOnClickListener {
            startActivity(Intent(this, RelativeActivity::class.java))
        }

        findViewById<Button>(R.id.btnConstraint).setOnClickListener {
            startActivity(Intent(this, ConstraintActivity::class.java))
        }

        findViewById<Button>(R.id.btnFrame).setOnClickListener {
            startActivity(Intent(this, FrameActivity::class.java))
        }

        findViewById<Button>(R.id.btnTable).setOnClickListener {
            startActivity(Intent(this, TableActivity::class.java))
        }

        findViewById<Button>(R.id.btnGrid).setOnClickListener {
            startActivity(Intent(this, GridActivity::class.java))
        }

        findViewById<Button>(R.id.btnLinear).setOnClickListener {
            startActivity(Intent(this, LinearActivity::class.java))
        }
    }
}
