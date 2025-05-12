package com.example.rssreaderapp

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rssreaderapp.model.NewsItem
import com.example.rssreaderapp.network.ApiService
import com.example.rssreaderapp.network.RssParser
import com.example.rssreaderapp.ui.NewsAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var tts: TextToSpeech
    private var adapter: NewsAdapter? = null
    private val rssUrl = "https://www.sozcu.com.tr/feeds-rss-category-sozcu"
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView
    private val apiService = ApiService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViews()
        initializeTTS()
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        errorText = findViewById(R.id.errorText)
        
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun initializeTTS() {
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.getDefault()
                loadRss()
            } else {
                showError("Text-to-Speech initialization failed")
            }
        }
    }

    private fun loadRss() {
        showLoading()
        lifecycleScope.launch {
            try {
                val items = withContext(Dispatchers.IO) {
                    RssParser().fetchAndParse(rssUrl)
                }
                if (items.isEmpty()) {
                    showError("No news items found")
                } else {
                    showContent(items)
                }
            } catch (e: Exception) {
                showError("Error loading news: ${e.localizedMessage}")
            }
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        errorText.visibility = View.GONE
    }

    private fun showError(message: String) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        errorText.visibility = View.VISIBLE
        errorText.text = message
    }

    private fun showContent(items: List<NewsItem>) {
        progressBar.visibility = View.GONE
        errorText.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        
        adapter = NewsAdapter(items, tts, lifecycleScope, apiService)
        recyclerView.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter?.stopPlayback()
        tts.stop()
        tts.shutdown()
    }
}