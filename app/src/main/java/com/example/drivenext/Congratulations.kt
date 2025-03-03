package com.example.drivenext

import android.os.Bundle
import android.content.Intent
import android.widget.LinearLayout

class Congratulations : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_congrulations)
        val buttonNext = findViewById<LinearLayout>(R.id.button_layout)
        buttonNext.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}