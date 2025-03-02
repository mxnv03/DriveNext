package com.example.drivenext

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast

class NoInternetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.no_connection)

        val retryButton  = findViewById<LinearLayout>(R.id.button_layout)
        retryButton.setOnClickListener {
            if (isNetworkAvailable()) {
                // Закрываем текущую активность, если подключение восстановлено
                finish()
            } else {
                Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }
}

