package com.example.pingpong

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameView(context: Context) : SurfaceView(context), Runnable {
    private val thread = Thread(this)
    private var isPlaying = true
    private val paint = Paint()

    // Ball properties
    private var ballX = 100f
    private var ballY = 100f
    private var velocityX = 10f
    private var velocityY = 10f
    private val ballRadius = 20f

    // Paddle properties
    private var paddleX = 300f
    private var paddleY = 0f  // initialized later
    private val paddleWidth = 200f
    private val paddleHeight = 30f

    // Game state
    private var score = 0
    private var isGameOver = false
    private val maxSpeed = 35f

    init {
        paint.color = Color.RED
        thread.start()
    }

    override fun run() {
        while (isPlaying) {
            if (holder.surface.isValid) {
                val canvas = holder.lockCanvas()
                update()
                drawGame(canvas)
                holder.unlockCanvasAndPost(canvas)
            }
            Thread.sleep(16) // ~60 FPS
        }
    }

    private fun update() {
        if (!isGameOver) {
            ballX += velocityX
            ballY += velocityY

            // Bounce off walls
            if (ballX - ballRadius <= 0) {
                ballX = ballRadius
                velocityX *= -1
            }
            if (ballX + ballRadius >= width) {
                ballX = width - ballRadius
                velocityX *= -1
            }
            if (ballY - ballRadius <= 0) {
                ballY = ballRadius
                velocityY *= -1
            }

            // Check paddle collision
            if (ballY + ballRadius >= paddleY &&
                ballX >= paddleX && ballX <= paddleX + paddleWidth
            ) {
                velocityY *= -1
                ballY = paddleY - ballRadius
                score++
                // Increase speed, cap at maxSpeed
                velocityX = (if (velocityX > 0) (velocityX * 1.05f) else (velocityX * 1.05f)).coerceIn(-maxSpeed, maxSpeed)
                velocityY = (if (velocityY > 0) (velocityY * 1.1f) else (velocityY * 1.1f)).coerceIn(-maxSpeed, maxSpeed)
            }

            // Game Over logic
            if (ballY > height) {
                isGameOver = true
            }
        }
    }

    private fun drawGame(canvas: Canvas) {
        // Set paddle Y based on screen height
        if (paddleY == 0f) paddleY = height - 150f

        canvas.drawColor(Color.BLACK)

        // Draw ball
        paint.color = Color.RED
        canvas.drawCircle(ballX, ballY, ballRadius, paint)

        // Draw paddle
        paint.color = Color.WHITE
        canvas.drawRect(paddleX, paddleY, paddleX + paddleWidth, paddleY + paddleHeight, paint)

        // Draw score
        paint.color = Color.YELLOW
        paint.textSize = 60f
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("Score: $score", width / 2f, 80f, paint)
        paint.textAlign = Paint.Align.LEFT // Reset alignment for other text

        // Draw Game Over
        if (isGameOver) {
            paint.color = Color.WHITE
            paint.textSize = 100f
            canvas.drawText("Game Over", width / 4f, height / 2f, paint)
            paint.textSize = 60f
            canvas.drawText("Tap to Restart", width / 4f, height / 2f + 100f, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isGameOver && event.action == MotionEvent.ACTION_DOWN) {
            restartGame()
            return true
        }
        if (event.action == MotionEvent.ACTION_MOVE) {
            // Center paddle on touch X
            paddleX = event.x - paddleWidth / 2
            // Keep paddle within screen bounds
            if (paddleX < 0) paddleX = 0f
            if (paddleX + paddleWidth > width) paddleX = width - paddleWidth
        }
        return true
    }

    private fun restartGame() {
        ballX = width / 2f
        ballY = height / 2f
        velocityX = 10f
        velocityY = -10f
        score = 0
        isGameOver = false
    }
} 