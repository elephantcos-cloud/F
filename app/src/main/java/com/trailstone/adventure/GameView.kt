package com.trailstone.adventure

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.concurrent.thread

/**
 * onFinish is called with true if the chapter was cleared, false if the player quit/died.
 */
class GameView(
    context: Context,
    private val chapterId: Int,
    private val onFinish: (Boolean) -> Unit
) : SurfaceView(context), SurfaceHolder.Callback {

    private var running = false
    private var thread: Thread? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val chapter = Levels.getChapter(chapterId)
    private val bgBitmap = BitmapFactory.decodeResource(context.resources, chapter.background)
    private val pauseBmp = BitmapFactory.decodeResource(context.resources, R.drawable.ui_pause)

    private var screenW = 0
    private var screenH = 0
    private var groundY = 0f

    private lateinit var player: Player
    private val enemies = mutableListOf<Enemy>()
    private val items = mutableListOf<Item>()
    private var coinsCollected = 0

    private var gameOver = false
    private var victory = false
    private var victoryTimer = 0
    private var paused = false

    private var movingLeft = false
    private var movingRight = false

    private lateinit var btnLeft: RectF
    private lateinit var btnRight: RectF
    private lateinit var btnJump: RectF
    private lateinit var btnAttack: RectF
    private lateinit var btnPause: RectF

    init {
        holder.addCallback(this)
        isFocusable = true
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        screenW = width
        screenH = height
        groundY = screenH * 0.82f

        player = Player(context, groundY)
        player.x = screenW * 0.12f

        enemies.clear()
        chapter.enemies.forEach { spawn ->
            enemies.add(Enemy(context, spawn.type, screenW * spawn.xFraction, groundY))
        }
        items.clear()
        chapter.items.forEach { spawn ->
            items.add(Item(context, spawn.type, screenW * spawn.xFraction, groundY - 150f))
        }

        val btnSize = screenW * 0.075f
        val margin = 36f
        val gap = 20f
        btnLeft = RectF(margin, screenH - btnSize * 2 - margin, margin + btnSize * 2, screenH - margin)
        btnRight = RectF(
            margin + btnSize * 2 + gap, screenH - btnSize * 2 - margin,
            margin + btnSize * 4 + gap, screenH - margin
        )
        btnJump = RectF(
            screenW - btnSize * 4 - gap - margin, screenH - btnSize * 2 - margin,
            screenW - btnSize * 2 - gap - margin, screenH - margin
        )
        btnAttack = RectF(
            screenW - btnSize * 2 - margin, screenH - btnSize * 2 - margin,
            screenW - margin, screenH - margin
        )
        btnPause = RectF(screenW - 110f, 30f, screenW - 30f, 110f)

        running = true
        thread = thread { gameLoop() }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, w: Int, h: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        running = false
        thread?.join()
    }

    private fun gameLoop() {
        while (running) {
            val start = System.currentTimeMillis()
            if (!paused && !gameOver) update()
            val canvas = holder.lockCanvas() ?: continue
            try {
                render(canvas)
            } finally {
                holder.unlockCanvasAndPost(canvas)
            }
            val dt = System.currentTimeMillis() - start
            if (dt < 16) Thread.sleep(16 - dt)
        }
    }

    private fun update() {
        if (!victory) {
            if (movingLeft) player.moveLeft() else if (movingRight) player.moveRight() else player.stopMoving()
        }
        player.update()
        player.x = player.x.coerceIn(70f, screenW - 70f)

        if (!victory) {
            enemies.forEach { it.update(player.x) }
            items.forEach { it.update() }

            enemies.filter { it.alive }.forEach { e ->
                if (RectF.intersects(player.getBounds(), e.getBounds())) {
                    player.takeDamage()
                }
            }

            items.filter { !it.collected }.forEach { i ->
                if (RectF.intersects(player.getBounds(), i.getBounds())) {
                    i.collected = true
                    when (i.type) {
                        "coin" -> coinsCollected++
                        "heart" -> player.hearts = (player.hearts + 1).coerceAtMost(3)
                        "potion" -> player.powerTimer = 300
                        else -> {}
                    }
                }
            }

            if (player.hearts <= 0) gameOver = true

            val allDead = enemies.all { !it.alive }
            val keyCollected = items.filter { it.type == "key" }.all { it.collected }
            if (allDead && keyCollected) {
                victory = true
                victoryTimer = 0
                player.setVictory()
                unlockNextChapter()
            }
        } else {
            victoryTimer++
            if (victoryTimer > 100) onFinish(true)
        }
    }

    private fun unlockNextChapter() {
        val prefs = context.getSharedPreferences("trailstone", Context.MODE_PRIVATE)
        val unlocked = prefs.getInt("unlocked", 1)
        if (chapterId >= unlocked && chapterId < 5) {
            prefs.edit().putInt("unlocked", chapterId + 1).apply()
        }
    }

    private fun render(canvas: Canvas) {
        canvas.drawBitmap(bgBitmap, null, RectF(0f, 0f, screenW.toFloat(), screenH.toFloat()), paint)

        items.forEach { it.draw(canvas, paint) }
        enemies.filter { it.alive }.forEach { it.draw(canvas, paint) }
        player.draw(canvas, paint)

        drawHud(canvas)
        if (!victory && !gameOver) drawControls(canvas)

        if (gameOver) drawOverlay(canvas, "GAME OVER", "Tap to retry")
        if (victory) drawOverlay(canvas, "${chapter.name} Cleared!", "")
        if (paused && !gameOver && !victory) drawOverlay(canvas, "PAUSED", "Tap pause to resume")
    }

    private fun drawHud(canvas: Canvas) {
        paint.color = Color.WHITE
        paint.textSize = 46f
        paint.isFakeBoldText = true
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("\u2764 x${player.hearts}", 30f, 60f, paint)
        canvas.drawText("\u25CF x$coinsCollected", 30f, 115f, paint)
        paint.isFakeBoldText = false
    }

    private fun drawControls(canvas: Canvas) {
        paint.color = Color.argb(110, 255, 255, 255)
        listOf(btnLeft, btnRight, btnJump, btnAttack).forEach {
            canvas.drawOval(it, paint)
        }
        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = btnLeft.height() * 0.4f
        canvas.drawText("\u25C0", btnLeft.centerX(), btnLeft.centerY() + 16f, paint)
        canvas.drawText("\u25B6", btnRight.centerX(), btnRight.centerY() + 16f, paint)
        canvas.drawText("\u2191", btnJump.centerX(), btnJump.centerY() + 16f, paint)
        paint.textSize = btnAttack.height() * 0.32f
        canvas.drawText("ATTACK", btnAttack.centerX(), btnAttack.centerY() + 12f, paint)
        paint.textAlign = Paint.Align.LEFT

        canvas.drawBitmap(pauseBmp, null, btnPause, paint)
    }

    private fun drawOverlay(canvas: Canvas, title: String, subtitle: String) {
        paint.color = Color.argb(170, 0, 0, 0)
        canvas.drawRect(0f, 0f, screenW.toFloat(), screenH.toFloat(), paint)
        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 64f
        paint.isFakeBoldText = true
        canvas.drawText(title, screenW / 2f, screenH / 2f, paint)
        if (subtitle.isNotEmpty()) {
            paint.textSize = 34f
            paint.isFakeBoldText = false
            canvas.drawText(subtitle, screenW / 2f, screenH / 2f + 60f, paint)
        }
        paint.textAlign = Paint.Align.LEFT
        paint.isFakeBoldText = false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked

        if (gameOver && action == MotionEvent.ACTION_DOWN) {
            onFinish(false)
            return true
        }

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
            val idx = event.actionIndex
            val px = event.getX(idx)
            val py = event.getY(idx)

            if (btnPause.contains(px, py)) {
                paused = !paused
            } else if (!paused && !victory) {
                if (btnJump.contains(px, py)) player.jump()
                if (btnAttack.contains(px, py)) {
                    val hitbox = player.attack()
                    if (hitbox != null) {
                        enemies.filter { it.alive }.forEach { e ->
                            if (RectF.intersects(hitbox, e.getBounds())) e.takeDamage()
                        }
                    }
                }
            }
        }

        movingLeft = false
        movingRight = false
        for (i in 0 until event.pointerCount) {
            val px = event.getX(i)
            val py = event.getY(i)
            if (btnLeft.contains(px, py)) movingLeft = true
            if (btnRight.contains(px, py)) movingRight = true
        }
        return true
    }
}
