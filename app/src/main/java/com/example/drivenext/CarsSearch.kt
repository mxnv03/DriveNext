package com.example.drivenext

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView

class CarsSearch: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_activity)

        val searchQuery = intent.getStringExtra("query") ?: ""
        val carListManager = CarListManager(this, findViewById(R.id.cars_list))
        carListManager.loadCars(searchQuery)

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            startActivity(Intent(this, CarsActivity::class.java))
        }

        val nav_home = findViewById<ImageView>(R.id.nav_home)
        nav_home.setOnClickListener {
            startActivity(Intent(this, CarsActivity::class.java))
        }

        val nav_profile = findViewById<ImageView>(R.id.nav_profile)
        nav_profile.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}