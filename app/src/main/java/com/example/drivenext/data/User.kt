package com.example.drivenext.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val patronymic: String?,
    val driverLicense: String,
    val email: String,
    val dateOfBirth: String,
    val password: String,
    val sex: String,
    val registration_date: String,
    val photo_url: String,
)
