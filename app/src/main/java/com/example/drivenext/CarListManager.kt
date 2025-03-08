package com.example.drivenext

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.drivenext.data.AppDatabase
import com.example.drivenext.data.Car

class CarListManager(
    private val context: Context,
    private val carsListLayout: LinearLayout
) {
    private var allCars: List<Car> = emptyList()
    private val carDao = AppDatabase.getDatabase(context).carDao()

    fun loadCars(searchQuery: String? = null) {
        Thread {
            allCars = carDao.getAllCars()  // Загружаем машины из БД
            val filteredCars = if (!searchQuery.isNullOrEmpty()) {
                allCars.filter { car ->
                    car.model.contains(searchQuery, ignoreCase = true) ||
                            car.brand.contains(searchQuery, ignoreCase = true)
                }
            } else {
                allCars  // Если запроса нет, показываем все машины
            }
            (context as Activity).runOnUiThread {
                updateCarList(filteredCars)
            }
        }.start()
    }

    private fun updateCarList(cars: List<Car>) {
        carsListLayout.removeAllViews()
        for (car in cars) {
            val carView = LayoutInflater.from(context).inflate(R.layout.car_item, carsListLayout, false)
            bindCarView(carView, car)
            carsListLayout.addView(carView)
        }
    }

    private fun bindCarView(view: View, car: Car) {
        view.findViewById<TextView>(R.id.carName).text = car.model
        view.findViewById<TextView>(R.id.carBrand).text = car.brand
        view.findViewById<TextView>(R.id.carPrice).text = "${car.pricePerDay}₽/сутки"
        view.findViewById<TextView>(R.id.gearboxText).text = car.gearbox
        view.findViewById<TextView>(R.id.fuelText).text = car.fuelType

        val imageView = view.findViewById<ImageView>(R.id.carImage)
        Glide.with(context).load(car.imageUrl).into(imageView)
    }
}

