package com.trailstone.adventure

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import kotlin.math.abs
import kotlin.math.sin

class Enemy(
    context: Context,
    val type: String,
    var x: Float,
    private val groundY: Float
) {
    var alive = true
    var y = groundY
    var facingRight = false

    var hp = when (type) {
        "goblin" -> 2
        "bat" -> 2
        "golem" -> 6
        else -> 1
    }

    val width = if (type == "golem") 150f else 90f
    val height = when (type) {
        "golem" -> 170f
        "bat" -> 60f
        else -> 100f
    }

    private val startX = x
    private var dir = 1f
    private val patrolRange = 130f
    private var timer = 0

    private val speed = when (type) {
        "goblin" -> 2.4f
        "bat" -> 2.2f
        "golem" -> 1.3f
        else -> 2f
    }

    private val bmp = BitmapFactory.decodeResource(
        context.resources,
        when (type) {
            "goblin" -> R.drawable.enemy_goblin
            "bat" -> R.drawable.enemy_bat
            "golem" -> R.drawable.enemy_golem
            else -> R.drawable.enemy_goblin
        }
    )

    fun update(playerX: Float) {
        timer++
        when (type) {
            "goblin" -> {
                x += speed * dir
                if (x > startX + patrolRange) dir = -1f
                if (x < startX - patrolRange) dir = 1f
                facingRight = playerX > x
            }
            "bat" -> {
                y = groundY - 210f + sin(timer * 0.05f) * 35f
                x += if (playerX > x) speed else -speed
                facingRight = playerX > x
            }
            "golem" -> {
                if (abs(playerX - x) > 25f) x += if (playerX > x) speed else -speed
                facingRight = playerX > x
            }
        }
    }

    fun takeDamage() {
        hp--
        if (hp <= 0) alive = false
    }

    fun getBounds(): RectF = RectF(x - width / 2, y - height, x + width / 2, y)

    fun draw(canvas: Canvas, paint: Paint) {
        val scale = height / bmp.height
        val drawW = bmp.width * scale
        val matrix = Matrix()
        // Source art faces right by default; flip when the enemy should face left.
        if (facingRight) {
            matrix.postScale(scale, scale)
            matrix.postTranslate(x - drawW / 2, y - height)
        } else {
            matrix.postScale(-scale, scale)
            matrix.postTranslate(x + drawW / 2, y - height)
        }
        canvas.drawBitmap(bmp, matrix, paint)
    }
}
