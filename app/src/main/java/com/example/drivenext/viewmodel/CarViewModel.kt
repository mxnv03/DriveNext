package com.example.drivenext.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivenext.data.Car
import com.example.drivenext.repository.CarRepository
import kotlinx.coroutines.launch

class CarViewModel(private val repository: CarRepository) : ViewModel() {

    fun insertCar(car: Car) {
        viewModelScope.launch {
            repository.insertCar(car)
        }
    }

    fun getAllCars(callback: (List<Car>) -> Unit) {
        viewModelScope.launch {
            val cars = repository.getAllCars()
            callback(cars)
        }
    }
}
