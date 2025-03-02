package com.example.drivenext

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.content.SharedPreferences

class LoginAuthActivity : BaseActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        // запоминаем что прошли онбординг
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("onboarding", true)
        editor.apply()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_and_auth)
        val buttonLogin = findViewById<LinearLayout>(R.id.button_layout)
        buttonLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        val buttonAuth = findViewById<LinearLayout>(R.id.button_layout1)
        buttonAuth.setOnClickListener {
            val intent = Intent(this, RegistationFirst::class.java)
            startActivity(intent)
        }
    }
}