package com.example.rssreaderapp.ui

import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.example.rssreaderapp.R
import com.example.rssreaderapp.model.NewsItem
import com.example.rssreaderapp.network.ApiService
import kotlinx.coroutines.launch

class NewsAdapter(
    private val items: List<NewsItem>,
    private val tts: TextToSpeech,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val apiService: ApiService
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {
    private var currentlyPlayingPosition: Int? = null

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val playPauseButton: ImageButton = itemView.findViewById(R.id.playPauseButton)
        val likeButton: ImageButton = itemView.findViewById(R.id.likeButton)
        val dislikeButton: ImageButton = itemView.findViewById(R.id.dislikeButton)
        val likesCount: TextView = itemView.findViewById(R.id.likesCount)
        val dislikesCount: TextView = itemView.findViewById(R.id.dislikesCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val item = items[position]
        holder.titleTextView.text = item.title
        holder.likesCount.text = item.likes.toString()
        holder.dislikesCount.text = item.dislikes.toString()

        val isPlaying = currentlyPlayingPosition == position
        holder.playPauseButton.setImageResource(
            if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
        )

        // Register news item with server and get initial stats
        lifecycleScope.launch {
            try {
                apiService.registerNews(item.title)
                val (likes, dislikes) = apiService.getStats(item.title)
                item.likes = likes
                item.dislikes = dislikes
                holder.likesCount.text = likes.toString()
                holder.dislikesCount.text = dislikes.toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        holder.playPauseButton.setOnClickListener {
            if (isPlaying) {
                tts.stop()
                currentlyPlayingPosition = null
                notifyItemChanged(position)
            } else {
                tts.stop()
                tts.speak(item.title, TextToSpeech.QUEUE_FLUSH, null, position.toString())
                val prev = currentlyPlayingPosition
                currentlyPlayingPosition = position
                notifyItemChanged(position)
                prev?.let { notifyItemChanged(it) }
            }
        }

        holder.likeButton.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val (likes, dislikes) = apiService.thumbUp(item.title)
                    item.likes = likes
                    item.dislikes = dislikes
                    holder.likesCount.text = likes.toString()
                    holder.dislikesCount.text = dislikes.toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        holder.dislikeButton.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val (likes, dislikes) = apiService.thumbDown(item.title)
                    item.likes = likes
                    item.dislikes = dislikes
                    holder.likesCount.text = likes.toString()
                    holder.dislikesCount.text = dislikes.toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun getItemCount() = items.size

    fun stopPlayback() {
        val prev = currentlyPlayingPosition
        currentlyPlayingPosition = null
        prev?.let { notifyItemChanged(it) }
        tts.stop()
    }
}