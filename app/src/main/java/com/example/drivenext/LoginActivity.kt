package com.example.drivenext

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.widget.*

class LoginActivity : BaseActivity() {
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var togglePasswordButton: ImageView
    private lateinit var loginButton: LinearLayout
    private lateinit var googleLoginButton: LinearLayout
    private lateinit var forgotPasswordText: TextView
    private lateinit var registerText: TextView

    private var isPasswordVisible = false

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
                startActivity(Intent(this, Congratulations::class.java)) // Переход на главную страницу
                finish()
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
}
