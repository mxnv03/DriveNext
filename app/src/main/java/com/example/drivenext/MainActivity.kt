package com.example.drivenext

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.content.SharedPreferences

class MainActivity : BaseActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        val savedOnboarding = sharedPreferences.getBoolean("onboarding", false)
        val savedLogin = sharedPreferences.getString("email", null)
        setContentView(R.layout.activity_main)
        Handler(Looper.getMainLooper()).postDelayed({
            if (savedLogin != null) {
                if (savedLogin.isEmpty()) {
                    startActivity(Intent(this, LoginAuthActivity::class.java))
                } else {
                    startActivity(Intent(this, SettingsActivity::class.java))
                }
            }
            if (savedOnboarding) {
                startActivity(Intent(this, LoginAuthActivity::class.java))
            } else {
                startActivity(Intent(this, Onboarding1::class.java))
            }
            finish() // Закрываем текущую активность, чтобы нельзя было вернуться назад
        }, 2000)
    }
}