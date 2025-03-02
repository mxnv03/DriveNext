package com.example.drivenext

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, Onboarding1::class.java)
            startActivity(intent)
            finish() // Закрываем текущую активность, чтобы нельзя было вернуться назад
        }, 3000)
    }
}