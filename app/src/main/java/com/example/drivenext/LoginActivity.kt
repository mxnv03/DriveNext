package com.example.drivenext

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.widget.*
import androidx.activity.viewModels
import com.example.drivenext.data.AppDatabase
import com.example.drivenext.repository.UserRepository
import com.example.drivenext.viewmodel.UserViewModel
import com.example.drivenext.data.User
import androidx.lifecycle.Observer

class LoginActivity : BaseActivity() {
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var togglePasswordButton: ImageView
    private lateinit var loginButton: LinearLayout
    private lateinit var googleLoginButton: LinearLayout
    private lateinit var forgotPasswordText: TextView
    private lateinit var registerText: TextView

    private var isPasswordVisible = false
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Инициализация элементов
        emailField = findViewById(R.id.editTextText)
        passwordField = findViewById(R.id.editTextText1)
        togglePasswordButton = findViewById(R.id.imageView6)
        loginButton = findViewById(R.id.button_layout)
        googleLoginButton = findViewById(R.id.button_layout2)
        forgotPasswordText = findViewById(R.id.textView20)
        registerText = findViewById(R.id.textView21)

        passwordField.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        // Кнопка скрытия/показа пароля
        togglePasswordButton.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                passwordField.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                togglePasswordButton.setImageResource(R.drawable.hide_pass) // глаз открыт
            } else {
                passwordField.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                togglePasswordButton.setImageResource(R.drawable.hide_pass) // глаз закрыт
            }
            passwordField.setSelection(passwordField.text.length) // Ставим курсор в конец
        }

        // Обработка нажатия на кнопку "Войти"
        loginButton.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            if (validateInput(email, password)) {
                // Проверка пользователя по email
                userViewModel.getUserByEmail(email).observe(this, Observer { user ->
                    if (user != null) {
                        // Если пользователь найден, проверяем пароль
                        if (user.password == password) {
                            // Пароли совпадают, авторизуем пользователя
                            startActivity(Intent(this, Congratulations::class.java)) // Переход на главный экран
                            finish()
                        } else {
                            // Пароль неверный
                            Toast.makeText(this, "Неверный пароль", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Пользователь не найден
                        Toast.makeText(this, "Пользователь не найден", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        // Обработка входа через Google
        googleLoginButton.setOnClickListener {
            Toast.makeText(this, "Вход через Google", Toast.LENGTH_SHORT).show()
        }

        // Обработка нажатия "Забыли пароль?"
        forgotPasswordText.setOnClickListener {
            Toast.makeText(this, "Функция восстановления пароля", Toast.LENGTH_SHORT).show()
        }

        // Обработка нажатия "Зарегистрироваться"
        registerText.setOnClickListener {
            startActivity(Intent(this, RegistationFirst::class.java))
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        // Проверка на пустые поля
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            return false
        }

        // Валидация формата email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Введите корректный email", Toast.LENGTH_SHORT).show()
            return false
        }

        // Валидация пароля (например, минимум 6 символов)
        if (password.length < 6) {
            Toast.makeText(this, "Пароль должен содержать минимум 6 символов", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun checkLoginCredentials(email: String, password: String) {
        userViewModel.getUserByEmail(email).observe(this, Observer { user ->
            if (user != null) {
                // Сравниваем пароли
                if (user.password == password) {
                    startActivity(Intent(this, Congratulations::class.java)) // Переход на главную страницу
                    finish()
                } else {
                    Toast.makeText(this, "Неверный пароль", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Пользователь с таким email не найден", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
