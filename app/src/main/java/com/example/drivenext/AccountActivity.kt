package com.example.drivenext

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.content.SharedPreferences
import android.os.Build
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.drivenext.viewmodel.UserViewModel

class AccountActivity : BaseActivity() {

    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView
    private lateinit var userGenderTextView: TextView
    private lateinit var userAvatarImageView: ImageView
    private lateinit var userViewModel: UserViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dateJoin: TextView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account)

        // Инициализация элементов разметки
        userNameTextView = findViewById(R.id.textView22) // Имя пользователя
        userEmailTextView = findViewById(R.id.mail_text) // Почта
        userGenderTextView = findViewById(R.id.male_text) // Пол
        userAvatarImageView = findViewById(R.id.addAvatar) // Аватар
        dateJoin = findViewById(R.id.dateJoin) // Аватар

        // Инициализация ViewModel
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Получаем ID пользователя из SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = sharedPreferences.getLong("user_id", -1)

        if (userId != -1L) {
            // Наблюдаем за данными пользователя
            userViewModel.getUserById(userId).observe(this, Observer { user ->
                user?.let {
                    // Заполняем поля данными пользователя
                    userNameTextView.text = "${it.firstName} ${it.lastName}"
                    userEmailTextView.text = it.email
                    userGenderTextView.text = it.sex
                    dateJoin.text = "Присоединился ${it.registration_date}"

                    // Загружаем фото профиля, если оно есть
                    if (!it.photo_url.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(it.photo_url)
                            .circleCrop() // Обрезаем в круг
                            .into(userAvatarImageView)
                    } else {
                        userAvatarImageView.setImageResource(R.drawable.avatar)
                    }
                }
            })
        }

        val backButton: ImageView = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        val logOutButton: LinearLayout = findViewById(R.id.login_out)
        logOutButton.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.remove("user_id")
            startActivity(Intent(this, LoginAuthActivity::class.java))
        }
    }
}
