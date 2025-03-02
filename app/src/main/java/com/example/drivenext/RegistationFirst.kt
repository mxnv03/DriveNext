package com.example.drivenext

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.content.SharedPreferences
import android.text.InputType
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProvider

import com.example.drivenext.viewmodel.UserViewModel
import com.example.drivenext.data.User

class RegistationFirst : BaseActivity() {
    private lateinit var emailField: EditText
    private lateinit var passwordField_1: EditText
    private lateinit var passwordField_2: EditText
    private lateinit var togglePasswordButton_1: ImageView
    private lateinit var togglePasswordButton_2: ImageView
    private lateinit var nextButton: LinearLayout
    private lateinit var backButton: ImageView
    private lateinit var errorText: TextView
    private lateinit var checkBox: CheckBox
    private lateinit var userViewModel: UserViewModel

    private var isPasswordVisible_1 = false
    private var isPasswordVisible_2 = false

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_first_step)

        // Инициализация элементов
        emailField = findViewById(R.id.editTextText)
        backButton = findViewById(R.id.backButton)
        passwordField_1 = findViewById(R.id.editPassword1)
        passwordField_2 = findViewById(R.id.editPassword2)
        togglePasswordButton_1 = findViewById(R.id.imageView6)
        togglePasswordButton_2 = findViewById(R.id.imageView7)
        nextButton = findViewById(R.id.button_layout)
        errorText = findViewById(R.id.errorText)
        checkBox = findViewById(R.id.checkBox2)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        fun validateEmail() {
            val email = emailField.text.toString().trim()
            if (email.isEmpty()) {
                emailField.error = "Error"
                errorText.visibility = View.VISIBLE
                emailField.setBackgroundResource(R.drawable.rounded_button_red)
            } else {
                emailField.error = null
                errorText.visibility = View.GONE
                emailField.setBackgroundResource(R.drawable.rounded_button_white_login)
            }
        }

        passwordField_1.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        passwordField_2.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        togglePasswordButton_1.setOnClickListener {
            isPasswordVisible_1 = !isPasswordVisible_1
            if (isPasswordVisible_1) {
                passwordField_1.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                togglePasswordButton_1.setImageResource(R.drawable.hide_pass) // глаз открыт
            } else {
                passwordField_1.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                togglePasswordButton_1.setImageResource(R.drawable.hide_pass) // глаз закрыт
            }
            passwordField_1.setSelection(passwordField_1.text.length) // Ставим курсор в конец
        }

        togglePasswordButton_2.setOnClickListener {
            isPasswordVisible_2 = !isPasswordVisible_2
            if (isPasswordVisible_2) {
                passwordField_2.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                togglePasswordButton_2.setImageResource(R.drawable.hide_pass) // глаз открыт
            } else {
                passwordField_1.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                togglePasswordButton_2.setImageResource(R.drawable.hide_pass) // глаз закрыт
            }
            passwordField_2.setSelection(passwordField_2.text.length) // Ставим курсор в конец
        }

        // Обработка нажатия на кнопку "Войти"
        nextButton.setOnClickListener {
            val email = emailField.text.toString()
            val password_1 = passwordField_1.text.toString()
            val password_2 = passwordField_2.text.toString()

            validateEmail()

            if (validateInput(email, password_1, password_2)) {
                saveUserData(email)
                // Создание пользователя
                val user = User(
                    firstName = "",
                    lastName = "",
                    patronymic = "",
                    driverLicense = "",
                    email = email,
                    dateOfBirth = "",
                    password = password_1,
                    sex = "",
                    registration_date = ""
                )
                userViewModel.insertUser(user) { userId ->  // <-- Передаём callback
                    val intent = Intent(this, RegistationSecond::class.java)
                    intent.putExtra("USER_ID", userId) // Передаём userId
                    startActivity(intent)
                    finish()
                }
                startActivity(Intent(this, RegistationSecond::class.java)) // Переход на главную страницу
                finish()
            }
        }
        backButton.setOnClickListener {
            startActivity(Intent(this, LoginAuthActivity::class.java))
        }
    }

    private fun validateInput(email: String, password1: String, password2: String): Boolean {
        if (email.isEmpty()) {
            emailField.error = "Error"
            errorText.text = "Это поле является обязательным"
            errorText.visibility = View.VISIBLE
            emailField.setBackgroundResource(R.drawable.rounded_button_red)
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.error = "Error"
            errorText.text = "Введите корректный email"
            errorText.visibility = View.VISIBLE
            emailField.setBackgroundResource(R.drawable.rounded_button_red)
            return false
        } else {
            emailField.error = null
            errorText.visibility = View.GONE
            emailField.setBackgroundResource(R.drawable.rounded_button_white_login)
        }

        if (email.isEmpty() || password1.isEmpty() || password1.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password1.length < 6) {
            passwordField_1.setBackgroundResource(R.drawable.rounded_button_red)
            Toast.makeText(this, "Пароль должен содержать минимум 6 символов", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password1 != password2) {
            passwordField_1.setBackgroundResource(R.drawable.rounded_button_red)
            passwordField_2.setBackgroundResource(R.drawable.rounded_button_red)
            passwordField_1.text.clear()
            passwordField_2.text.clear()
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!checkBox.isChecked) {
            checkBox.setTextColor(Color.RED) // Подсветка ошибки
            return false
        } else {
            checkBox.setTextColor(Color.parseColor("#1A1A1A"))
        }

        return true
    }

    private fun saveUserData(email: String) {
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.apply()
    }

}
