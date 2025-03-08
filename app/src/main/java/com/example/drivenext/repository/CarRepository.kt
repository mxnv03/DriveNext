package com.example.drivenext.repository

import com.example.drivenext.data.Car
import com.example.drivenext.data.CarDao

class CarRepository(private val carDao: CarDao) {
    suspend fun insertCar(car: Car) = carDao.insertCar(car)
    suspend fun getAllCars(): List<Car> = carDao.getAllCars()
    suspend fun getCarById(id: Int): Car? = carDao.getCarById(id)
}
