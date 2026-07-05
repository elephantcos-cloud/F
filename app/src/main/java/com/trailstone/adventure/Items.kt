package com.trailstone.adventure

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import kotlin.math.sin

class Item(context: Context, val type: String, var x: Float, var y: Float) {

    var collected = false
    val size = 64f

    private var floatTimer = (Math.random() * 100).toFloat()

    private val bmp = BitmapFactory.decodeResource(
        context.resources,
        when (type) {
            "coin" -> R.drawable.item_coin
            "key" -> R.drawable.item_key
            "heart" -> R.drawable.item_heart
            "potion" -> R.drawable.item_potion
            else -> R.drawable.item_coin
        }
    )

    fun update() {
        floatTimer += 1f
    }

    private fun floatY(): Float = y + sin(floatTimer * 0.05f) * 8f

    fun getBounds(): RectF {
        val fy = floatY()
        return RectF(x - size / 2, fy - size / 2, x + size / 2, fy + size / 2)
    }

    fun draw(canvas: Canvas, paint: Paint) {
        if (collected) return
        canvas.drawBitmap(bmp, null, getBounds(), paint)
    }
}
