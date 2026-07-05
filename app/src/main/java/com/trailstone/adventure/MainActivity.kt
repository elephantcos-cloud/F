package com.trailstone.adventure

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#1a1a2e"))
        }

        val logo = ImageView(this).apply {
            setImageResource(R.drawable.ui_logo)
            adjustViewBounds = true
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = 100
                marginEnd = 100
            }
        }
        root.addView(logo)

        val playBtn = ImageView(this).apply {
            setImageResource(R.drawable.ui_play)
            layoutParams = LinearLayout.LayoutParams(200, 200).apply { topMargin = 70 }
            setOnClickListener {
                startActivity(Intent(this@MainActivity, ChapterSelectActivity::class.java))
            }
        }
        root.addView(playBtn)

        setContentView(root)
    }
}
