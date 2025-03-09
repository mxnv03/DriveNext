package com.example.drivenext

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView


class HelpActivity: BaseActivity() {
    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.help)

        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}