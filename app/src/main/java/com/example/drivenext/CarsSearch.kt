package com.example.drivenext

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView

class CarsSearch: BaseActivity() {
    private lateinit var backButton: ImageView
    private lateinit var nav_home: ImageView
    private lateinit var nav_profile: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_activity)

        val searchQuery = intent.getStringExtra("query") ?: ""
        val carListManager = CarListManager(this, findViewById(R.id.cars_list))
        carListManager.loadCars(searchQuery)

        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            startActivity(Intent(this, CarsActivity::class.java))
        }
        nav_home = findViewById(R.id.nav_home)
        nav_home.setOnClickListener {
            startActivity(Intent(this, CarsActivity::class.java))
        }
        nav_profile = findViewById(R.id.nav_profile)
        nav_profile.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}