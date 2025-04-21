package com.example.customui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

class CircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
  
    // Click listeners for inner and outer circles
    var onInnerCircleClick: (() -> Unit)? = null
    var onOuterCircleClick: (() -> Unit)? = null
    var onQuadrantClick: ((Int) -> Unit)? = null
      
    private val outerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
    }
     
    private val innerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 30f
        textAlign = Paint.Align.CENTER
    }

    // Text for each button
    private val innerText = "Center"
    private val quadrantTexts = arrayOf("Button 1", "Button 2", "Button 3", "Button 4")
  
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val x = event.x
            val y = event.y
            val centerX = width / 2f
            val centerY = height / 2f
            val dx = x - centerX
            val dy = y - centerY
            val distance = hypot(dx, dy)
             
            val outerRadius = width.coerceAtMost(height) / 2f
            val innerRadius = outerRadius * 0.5f
             
            if (distance <= innerRadius) {
                onInnerCircleClick?.invoke()
                performClick()
                return true
            } else if (distance <= outerRadius) {
                val angle = (Math.toDegrees(atan2(dy, dx).toDouble()) + 360) % 360
                val quadrant = when {
                    angle >= 0 && angle < 90 -> 1
                    angle >= 90 && angle < 180 -> 2
                    angle >= 180 && angle < 270 -> 3
                    else -> 4
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
         
        val centerX = width / 2f
        val centerY = height / 2f
        val outerRadius = width.coerceAtMost(height) / 2f
        val innerRadius = outerRadius * 0.5f
         
        // Define the bounding rectangle for the outer circle
        val rect = RectF(
            centerX - outerRadius,
            centerY - outerRadius,
            centerX + outerRadius,
            centerY + outerRadius
        )
         
        // Paint object for drawing
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
        }
         
        // Define colors for each quadrant
        val quadrantColors = listOf(
            Color.parseColor("#f498ad"), // Quadrant 1: Orange
            Color.parseColor("#db5a6b"), // Quadrant 2: Green
            Color.parseColor("#636363"), // Quadrant 3: Blue
            Color.parseColor("#ba160c")  // Quadrant 4: Yellow
        )
         
        // Draw each quadrant
        for (i in 0 until 4) {
            paint.color = quadrantColors[i]
            canvas.drawArc(rect, i * 90f, 90f, true, paint)
            
            // Calculate position for quadrant text
            val midAngle = Math.toRadians((i * 90 + 45).toDouble())
            val textX = centerX + (innerRadius + (outerRadius - innerRadius) / 2) * cos(midAngle).toFloat()
            val textY = centerY + (innerRadius + (outerRadius - innerRadius) / 2) * sin(midAngle).toFloat()
            
            // Adjust text position for vertical centering (text is drawn from baseline)
            val textVerticalOffset = (textPaint.descent() + textPaint.ascent()) / 2
            
            // Draw text in quadrant
            canvas.drawText(quadrantTexts[i], textX, textY - textVerticalOffset, textPaint)
        }
         
        // Draw inner circle
        paint.color = Color.WHITE
        canvas.drawCircle(centerX, centerY, innerRadius, paint)
        
        // Draw text in inner circle
        canvas.drawText(innerText, centerX, centerY - (textPaint.descent() + textPaint.ascent()) / 2, textPaint)
    }
} 