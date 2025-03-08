package com.example.drivenext

import android.content.Intent
import android.os.Bundle
import android.content.SharedPreferences
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.drivenext.viewmodel.UserViewModel

class SettingsActivity : BaseActivity() {
    private lateinit var linearAccount: LinearLayout
    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var avatar: ImageView
    private lateinit var navHome: ImageView

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        // Инициализация элементов
        linearAccount = findViewById(R.id.linearAccount)
        userName = findViewById(R.id.userName)
        userEmail = findViewById(R.id.userEmail)
        avatar = findViewById(R.id.avatar)
        navHome = findViewById(R.id.nav_home)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        // Получаем ID пользователя из SharedPreferences
        val userId = sharedPreferences.getLong("user_id", -1)

        if (userId != -1L) {
            userViewModel.getUserById(userId).observe(this) { user ->
                user?.let {
                    userName.text = "${it.firstName} ${it.lastName}"
                    userEmail.text = it.email

                    // Загружаем фото профиля через Glide
                    if (!it.photo_url.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(it.photo_url)
                            .circleCrop()
                            .placeholder(R.drawable.settings_avatar) // Заглушка при загрузке
                            .error(R.drawable.settings_avatar) // Заглушка при ошибке
                            .into(avatar)
                    } else {
                        avatar.setImageResource(R.drawable.settings_avatar)
                    }
                }
            }
        }

        // Переход в настройки аккаунта
        linearAccount.setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
        }

        // Переход на страницу поиска
        navHome.setOnClickListener {
            startActivity(Intent(this, CarsActivity::class.java))
        }
    }
}
