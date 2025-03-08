package com.example.drivenext.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cars")
data class Car(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val model: String,
    val brand: String,
    val pricePerDay: Int,
    val gearbox: String,
    val fuelType: String,
    val imageUrl: String
)
