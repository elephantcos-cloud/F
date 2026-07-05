package com.trailstone.adventure

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF

enum class PlayerState { IDLE, WALK, JUMP, ATTACK, HURT, VICTORY }

class Player(context: Context, private val groundY: Float) {

    var x = 150f
    var y = groundY
    var facingRight = true
    var hearts = 3
    var invulnerableTimer = 0
    var powerTimer = 0

    var state = PlayerState.IDLE
        private set

    val width = 90f
    val height = 170f

    private var velocityY = 0f
    private var onGround = true
    private var frameTimer = 0
    private var walkFrame = 0
    private var attackFrame = 0
    private var stateTimer = 0

    private val res = context.resources
    private val bmpIdle = BitmapFactory.decodeResource(res, R.drawable.player_idle)
    private val bmpWalk1 = BitmapFactory.decodeResource(res, R.drawable.player_walk1)
    private val bmpWalk2 = BitmapFactory.decodeResource(res, R.drawable.player_walk2)
    private val bmpCrouch = BitmapFactory.decodeResource(res, R.drawable.player_crouch)
    private val bmpJump = BitmapFactory.decodeResource(res, R.drawable.player_jump)
    private val bmpAttack = arrayOf(
        BitmapFactory.decodeResource(res, R.drawable.player_attack1),
        BitmapFactory.decodeResource(res, R.drawable.player_attack2),
        BitmapFactory.decodeResource(res, R.drawable.player_attack3),
        BitmapFactory.decodeResource(res, R.drawable.player_attack4)
    )
    private val bmpHurt = BitmapFactory.decodeResource(res, R.drawable.player_hurt)
    private val bmpVictory = BitmapFactory.decodeResource(res, R.drawable.player_victory)

    fun moveLeft() {
        if (state == PlayerState.ATTACK || state == PlayerState.HURT || state == PlayerState.VICTORY) return
        x -= 8f
        facingRight = false
        if (onGround && state != PlayerState.JUMP) state = PlayerState.WALK
    }

    fun moveRight() {
        if (state == PlayerState.ATTACK || state == PlayerState.HURT || state == PlayerState.VICTORY) return
        x += 8f
        facingRight = true
        if (onGround && state != PlayerState.JUMP) state = PlayerState.WALK
    }

    fun stopMoving() {
        if (state == PlayerState.WALK) state = PlayerState.IDLE
    }

    fun jump() {
        if (onGround && state != PlayerState.ATTACK && state != PlayerState.HURT) {
            onGround = false
            velocityY = -30f
            state = PlayerState.JUMP
        }
    }

    /** Triggers the attack animation and returns the hitbox for that swing, or null if attack can't start. */
    fun attack(): RectF? {
        if (state == PlayerState.ATTACK || state == PlayerState.HURT || !onGround) return null
        state = PlayerState.ATTACK
        attackFrame = 0
        stateTimer = 0
        return getAttackHitbox()
    }

    private fun getAttackHitbox(): RectF {
        val range = 120f
        return if (facingRight) {
            RectF(x + width / 2, y - height * 0.75f, x + width / 2 + range, y - height * 0.15f)
        } else {
            RectF(x - width / 2 - range, y - height * 0.75f, x - width / 2, y - height * 0.15f)
        }
    }

    fun takeDamage() {
        if (invulnerableTimer > 0 || state == PlayerState.HURT || state == PlayerState.VICTORY) return
        hearts--
        state = PlayerState.HURT
        stateTimer = 0
        invulnerableTimer = 90
    }

    fun setVictory() {
        state = PlayerState.VICTORY
    }

    fun getBounds(): RectF = RectF(x - width / 2.2f, y - height, x + width / 2.2f, y)

    fun update() {
        if (invulnerableTimer > 0) invulnerableTimer--
        if (powerTimer > 0) powerTimer--

        if (!onGround) {
            velocityY += 1.6f
            y += velocityY
            if (y >= groundY) {
                y = groundY
                velocityY = 0f
                onGround = true
                if (state == PlayerState.JUMP) state = PlayerState.IDLE
            }
        }

        frameTimer++
        stateTimer++

        when (state) {
            PlayerState.WALK -> if (frameTimer % 8 == 0) walkFrame = 1 - walkFrame
            PlayerState.ATTACK -> {
                if (stateTimer % 4 == 0) attackFrame++
                if (attackFrame >= 4) {
                    state = PlayerState.IDLE
                    attackFrame = 0
                }
            }
            PlayerState.HURT -> if (stateTimer > 30) state = PlayerState.IDLE
            else -> {}
        }
    }

    fun draw(canvas: Canvas, paint: Paint) {
        val bmp = when (state) {
            PlayerState.IDLE -> bmpIdle
            PlayerState.WALK -> if (walkFrame == 0) bmpWalk1 else bmpWalk2
            PlayerState.JUMP -> if (onGround) bmpCrouch else bmpJump
            PlayerState.ATTACK -> bmpAttack[attackFrame.coerceIn(0, 3)]
            PlayerState.HURT -> bmpHurt
            PlayerState.VICTORY -> bmpVictory
        }
        val scale = height / bmp.height
        val drawW = bmp.width * scale
        val matrix = Matrix()
        if (facingRight) {
            matrix.postScale(scale, scale)
            matrix.postTranslate(x - drawW / 2, y - height)
        } else {
            matrix.postScale(-scale, scale)
            matrix.postTranslate(x + drawW / 2, y - height)
        }
        val oldAlpha = paint.alpha
        if (invulnerableTimer > 0 && invulnerableTimer % 10 < 5) paint.alpha = 120
        canvas.drawBitmap(bmp, matrix, paint)
        paint.alpha = oldAlpha
    }
}
