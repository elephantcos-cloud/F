package com.trailstone.adventure

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class ChapterSelectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ChapterSelectView(this))
    }

    override fun onResume() {
        super.onResume()
        // Recreate so newly unlocked chapters are reflected after returning from a game.
        setContentView(ChapterSelectView(this))
    }
}

class ChapterSelectView(context: Context) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val lockBmp = BitmapFactory.decodeResource(context.resources, R.drawable.ui_lock)
    private val logoBmp = BitmapFactory.decodeResource(context.resources, R.drawable.ui_logo)
    private val unlocked: Int
    private val chapterRects = mutableListOf<RectF>()

    init {
        val prefs = context.getSharedPreferences("trailstone", Context.MODE_PRIVATE)
        unlocked = prefs.getInt("unlocked", 1)
        setBackgroundColor(Color.parseColor("#1a1a2e"))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()

        val logoRect = RectF(w * 0.22f, 20f, w * 0.78f, 150f)
        canvas.drawBitmap(logoBmp, null, logoRect, paint)

        chapterRects.clear()
        val names = Levels.chapters.map { "${it.id}. ${it.name}" }
        val startY = 190f
        val bottomMargin = 30f
        val gap = 14f
        val boxH = (h - startY - bottomMargin - gap * 4) / 5f

        for (i in 0 until 5) {
            val top = startY + i * (boxH + gap)
            val rect = RectF(w * 0.08f, top, w * 0.92f, top + boxH)
            chapterRects.add(rect)

            val isUnlocked = (i + 1) <= unlocked
            paint.color = if (isUnlocked) Color.parseColor("#4a7c59") else Color.parseColor("#33334a")
            canvas.drawRoundRect(rect, 22f, 22f, paint)

            paint.color = Color.WHITE
            paint.textSize = boxH * 0.34f
            paint.textAlign = Paint.Align.LEFT
            canvas.drawText(names[i], rect.left + 36f, rect.centerY() + boxH * 0.12f, paint)

            if (!isUnlocked) {
                val lockSize = boxH * 0.55f
                val lockRect = RectF(
                    rect.right - lockSize - 30f, rect.centerY() - lockSize / 2,
                    rect.right - 30f, rect.centerY() + lockSize / 2
                )
                canvas.drawBitmap(lockBmp, null, lockRect, paint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            chapterRects.forEachIndexed { i, rect ->
                if (rect.contains(event.x, event.y) && (i + 1) <= unlocked) {
                    val intent = Intent(context, GameActivity::class.java)
                    intent.putExtra("chapter", i + 1)
                    context.startActivity(intent)
                }
            }
        }
        return true
    }
}
