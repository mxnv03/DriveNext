package com.example.drivenext

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Проверка на подключение к интернету
        if (!isNetworkAvailable()) {
            // Показать экран с ошибкой (показываем поверх текущей активности)
            showNoInternetScreen()
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    private fun showNoInternetScreen() {
        val intent = Intent(this, NoInternetActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK // Для того чтобы запустить активность как новый task
        startActivity(intent)
    }
}

