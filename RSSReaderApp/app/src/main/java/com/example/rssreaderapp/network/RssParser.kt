package com.example.rssreaderapp.network

import com.example.rssreaderapp.model.NewsItem
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class RssParser {
    private val TAG_ITEM = "item"
    private val TAG_TITLE = "title"
    private val TAG_DESCRIPTION = "description"

    fun fetchAndParse(urlString: String): List<NewsItem> {
        return try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.apply {
                readTimeout = 10000
                connectTimeout = 15000
                requestMethod = "GET"
                doInput = true
            }
            
            connection.connect()
            val inputStream = connection.inputStream
            val items = parseXml(inputStream)
            inputStream.close()
            connection.disconnect()
            items
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun parseXml(inputStream: InputStream): List<NewsItem> {
        val items = mutableListOf<NewsItem>()
        var title: String? = null
        var description: String? = null
        var insideItem = false

        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = false
            val parser = factory.newPullParser()
            parser.setInput(inputStream, null)

            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        when (parser.name) {
                            TAG_ITEM -> insideItem = true
                            TAG_TITLE -> if (insideItem) title = readText(parser)
                            TAG_DESCRIPTION -> if (insideItem) description = readText(parser)
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (parser.name == TAG_ITEM) {
                            title?.let { t ->
                                description?.let { d ->
                                    items.add(NewsItem(t, d))
                                }
                            }
                            title = null
                            description = null
                            insideItem = false
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return items
    }

    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }
}