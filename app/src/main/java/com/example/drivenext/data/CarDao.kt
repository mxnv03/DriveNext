package com.example.drivenext.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CarDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCar(car: Car)

    @Query("SELECT * FROM cars")
    fun getAllCars(): List<Car>

    @Query("SELECT * FROM cars WHERE id = :carId")
    fun getCarById(carId: Int): Car?

    @Query("DELETE FROM cars")
    fun deleteAllCars()
}
