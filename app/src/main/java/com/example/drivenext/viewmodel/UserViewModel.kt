package com.example.drivenext.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.drivenext.data.AppDatabase
import com.example.drivenext.repository.UserRepository
import kotlinx.coroutines.launch
import com.example.drivenext.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = AppDatabase.getDatabase(application).userDao()
    private val userRepository = UserRepository(userDao)

    fun insertUser(user: User, callback: (Long) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = userRepository.insertUser(user) // Получаем ID
            withContext(Dispatchers.Main) {
                callback(userId) // Передаём ID в callback
            }
        }
    }

    fun getUserById(userId: Long): LiveData<User> {
        return userRepository.getUserById(userId)
    }

    fun getPhotoByEmail(email: String): LiveData<String> {
        return userRepository.getPhotoByEmail(email)
    }

    fun updateUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.updateUser(user)
        }
    }

    fun getUserByEmail(email: String): LiveData<User> {
        return userRepository.getUserByEmail(email)
    }
}
