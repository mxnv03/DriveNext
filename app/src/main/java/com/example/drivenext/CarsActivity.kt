package com.example.drivenext

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout

class CarsActivity : BaseActivity() {
    private lateinit var nav_profile: ImageView
    private lateinit var search: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cars_activity)

        nav_profile = findViewById(R.id.nav_profile)
        search = findViewById(R.id.search)

        val carsListLayout = findViewById<LinearLayout>(R.id.cars_list)
        val carListManager = CarListManager(this, carsListLayout)
        carListManager.loadCars()

        nav_profile.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        search.setOnClickListener {
            val query = findViewById<EditText>(R.id.searchText).text.toString()
            val intent = Intent(this, LoadingActivity::class.java) // Переход в экран загрузки
            intent.putExtra("query", query)
            startActivity(intent)
        }
    }
}

