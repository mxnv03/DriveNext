package com.example.drivenext.repository

import androidx.lifecycle.LiveData
import com.example.drivenext.data.User
import com.example.drivenext.data.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao) {
    suspend fun insertUser(user: User): Long {
        return withContext(Dispatchers.IO) {
            userDao.insert(user)
        }
    }

    fun getUserByEmail(email: String): LiveData<User> {
        return userDao.getUserByEmail(email)
    }

    fun getPhotoByEmail(email: String): LiveData<String> {
        return userDao.getPhotoByEmail(email)
    }

    fun getUserById(userId: Long): LiveData<User> {
        return userDao.getUserById(userId)
    }

    suspend fun updateUser(user: User) {
        withContext(Dispatchers.IO) {
            userDao.update(user)
        }
    }

}
