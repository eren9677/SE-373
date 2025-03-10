package com.example.recylcle

import org.junit.Assert.*
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.recyclerview.widget.RecyclerView
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertEquals

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.recylcle", appContext.packageName)
    }
    @Test
    fun dataIntegrity_isMaintained() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        activityScenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.recyclerView)
            val adapter = recyclerView.adapter as RecyclerAdapter
            val expectedTitle = "Chapter Three"
            val actualTitle = adapter.titles[2] // Accessing directly from the adapter's data array
            assertEquals("Data mismatch in adapter", expectedTitle, actualTitle)
        }
    }
}