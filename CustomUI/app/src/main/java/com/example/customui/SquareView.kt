package com.example.customui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.hypot

class SquareView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
  
    // Click listeners for inner and outer squares
    var onInnerSquareClick: (() -> Unit)? = null
    var onQuadrantClick: ((Int) -> Unit)? = null
    
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 30f
        textAlign = Paint.Align.CENTER
    }

    // Text for each button
    private val innerText = "Square"
    private val quadrantTexts = arrayOf("Sq 1", "Sq 2", "Sq 3", "Sq 4")
  
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val x = event.x
            val y = event.y
            val centerX = width / 2f
            val centerY = height / 2f
            
            val outerSize = width.coerceAtMost(height).toFloat()
            val innerSize = outerSize * 0.5f
            
            val innerLeft = centerX - innerSize / 2
            val innerTop = centerY - innerSize / 2
            val innerRight = centerX + innerSize / 2
            val innerBottom = centerY + innerSize / 2
            
            // Check if click is within inner square
            if (x >= innerLeft && x <= innerRight && y >= innerTop && y <= innerBottom) {
                onInnerSquareClick?.invoke()
                performClick()
                return true
            } 
            // Check which quadrant was clicked
            else if (x >= 0 && x <= width && y >= 0 && y <= height) {
                val quadrant = when {
                    x >= centerX && y < centerY -> 1 // Top Right
                    x >= centerX && y >= centerY -> 2 // Bottom Right
                    x < centerX && y >= centerY -> 3 // Bottom Left
                    else -> 4 // Top Left
                }
                onQuadrantClick?.invoke(quadrant)
                performClick()
                return true
            }
        }
        return super.onTouchEvent(event)
    }
  
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
         
        val width = width.toFloat()
        val height = height.toFloat()
        val centerX = width / 2
        val centerY = height / 2
        
        // Paint object for drawing
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
        }
         
        // Define colors for each quadrant
        val quadrantColors = listOf(
            Color.parseColor("#4CAF50"), // Quadrant 1: Green
            Color.parseColor("#FFC107"), // Quadrant 2: Amber
            Color.parseColor("#9C27B0"), // Quadrant 3: Purple
            Color.parseColor("#FF5722")  // Quadrant 4: Deep Orange
        )
        
        // Draw the 4 quadrants
        // Top-left quadrant
        paint.color = quadrantColors[3]
        canvas.drawRect(0f, 0f, centerX, centerY, paint)
        
        // Top-right quadrant
        paint.color = quadrantColors[0]
        canvas.drawRect(centerX, 0f, width, centerY, paint)
        
        // Bottom-left quadrant
        paint.color = quadrantColors[2]
        canvas.drawRect(0f, centerY, centerX, height, paint)
        
        // Bottom-right quadrant
        paint.color = quadrantColors[1]
        canvas.drawRect(centerX, centerY, width, height, paint)
        
        // Draw inner square
        val innerSize = width.coerceAtMost(height) * 0.5f
        val innerLeft = centerX - innerSize / 2
        val innerTop = centerY - innerSize / 2
        val innerRight = centerX + innerSize / 2
        val innerBottom = centerY + innerSize / 2
        
        paint.color = Color.WHITE
        canvas.drawRect(innerLeft, innerTop, innerRight, innerBottom, paint)
        
        // Draw texts in each quadrant
        // Top-left (Q4)
        canvas.drawText(quadrantTexts[3], centerX / 2, centerY / 2 - (textPaint.descent() + textPaint.ascent()) / 2, textPaint)
        
        // Top-right (Q1)
        canvas.drawText(quadrantTexts[0], centerX + centerX / 2, centerY / 2 - (textPaint.descent() + textPaint.ascent()) / 2, textPaint)
        
        // Bottom-left (Q3)
        canvas.drawText(quadrantTexts[2], centerX / 2, centerY + centerY / 2 - (textPaint.descent() + textPaint.ascent()) / 2, textPaint)
        
        // Bottom-right (Q2)
        canvas.drawText(quadrantTexts[1], centerX + centerX / 2, centerY + centerY / 2 - (textPaint.descent() + textPaint.ascent()) / 2, textPaint)
        
        // Draw text in inner square
        canvas.drawText(innerText, centerX, centerY - (textPaint.descent() + textPaint.ascent()) / 2, textPaint)
    }
} 