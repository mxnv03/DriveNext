package com.example.drivenext

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout

class LoginAuthActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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