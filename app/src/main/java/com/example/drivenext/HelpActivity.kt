package com.example.drivenext

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView


class HelpActivity: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.help)

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}