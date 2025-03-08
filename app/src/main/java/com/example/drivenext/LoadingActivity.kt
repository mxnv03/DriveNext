package com.example.drivenext

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class LoadingActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_waiting)

        val query = intent.getStringExtra("query")

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, CarsSearch::class.java)
            intent.putExtra("query", query)
            startActivity(intent)
            finish()
        }, 3000)
    }
}
