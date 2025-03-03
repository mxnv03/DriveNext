package com.example.drivenext

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.drivenext.viewmodel.UserViewModel

class SettingsActivity : BaseActivity() {
    private lateinit var linearAccount: LinearLayout
    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var avatar: ImageView

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

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        // Получаем ID пользователя из SharedPreferences
        val userId = sharedPreferences.getLong("user_id", -1)

        if (userId != -1L) {
            userViewModel.getUserById(userId).observe(this) { user ->
                user?.let {
                    userName.text = "${it.firstName} ${it.lastName}"
                    userEmail.text = it.email

                    // Загружаем фото профиля, если оно есть
                    if (!it.photo_url.isNullOrEmpty()) {
                        val uri = Uri.parse(it.photo_url)
                        // Проверяем, если это URI типа content://, используем ContentResolver
                        if (uri.scheme == "content") {
                            val permission = android.Manifest.permission.READ_EXTERNAL_STORAGE
                            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(this, arrayOf(permission), 1)
                            } else {
                                loadImageFromUri(uri)
                            }

                        } else {
                            avatar.setImageURI(uri)  // Для file:// или других типов URI
                        }
                    } else {
                        avatar.setImageResource(R.drawable.settings_avatar) // Заглушка, если фото нет
                    }
                }
            }
        }

        // Переход в настройки аккаунта
        linearAccount.setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
        }
    }

    // Метод для загрузки изображения через ContentResolver
    private fun loadImageFromUri(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            avatar.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show()
        }
    }
}
