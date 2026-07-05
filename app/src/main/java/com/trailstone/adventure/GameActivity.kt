package com.trailstone.adventure

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val chapterId = intent.getIntExtra("chapter", 1)
        val gameView = GameView(this, chapterId) {
            // Called when the chapter ends (cleared or player backed out after game over).
            runOnUiThread { finish() }
        }
        setContentView(gameView)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
