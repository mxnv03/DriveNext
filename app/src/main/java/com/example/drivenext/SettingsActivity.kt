package com.example.drivenext

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.content.SharedPreferences
import android.text.InputType
import android.util.Patterns
import android.view.View
import android.widget.*

class SettingsActivity : BaseActivity() {
    private lateinit var linearAccount: LinearLayout

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        // Инициализация элементов
        linearAccount = findViewById(R.id.linearAccount)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        linearAccount.setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
        }
    }
}
