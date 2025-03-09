package com.example.drivenext

import android.content.Intent
import android.os.Bundle
import android.content.SharedPreferences
import android.text.InputType
import androidx.lifecycle.Observer
import android.widget.*
import androidx.lifecycle.ViewModelProvider

import com.example.drivenext.viewmodel.UserViewModel
import com.example.drivenext.data.User

class NewPassword : BaseActivity() {
    private lateinit var passwordField_1: EditText
    private lateinit var passwordField_2: EditText
    private lateinit var togglePasswordButton_1: ImageView
    private lateinit var togglePasswordButton_2: ImageView
    private lateinit var doneButton: LinearLayout
    private lateinit var backButton: ImageView
    private lateinit var userViewModel: UserViewModel

    private var isPasswordVisible_1 = false
    private var isPasswordVisible_2 = false
    private var userId: Long = -1

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_password)

        // Инициализация элементов
        backButton = findViewById(R.id.backButton)
        passwordField_1 = findViewById(R.id.editPassword1)
        passwordField_2 = findViewById(R.id.editPassword2)
        togglePasswordButton_1 = findViewById(R.id.imageView6)
        togglePasswordButton_2 = findViewById(R.id.imageView7)
        doneButton = findViewById(R.id.button_layout)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        userId = sharedPreferences.getLong("user_id", -1)

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
                passwordField_2.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                togglePasswordButton_2.setImageResource(R.drawable.hide_pass) // глаз закрыт
            }
            passwordField_2.setSelection(passwordField_2.text.length) // Ставим курсор в конец
        }

        doneButton.setOnClickListener {
            val password_1 = passwordField_1.text.toString()
            val password_2 = passwordField_2.text.toString()


            if (validateInput(password_1, password_2)) {
                savePasswordToDatabase(password_1)
                startActivity(Intent(this, AccountActivity::class.java)) // Переход на главную страницу
                finish()
            }
        }
        backButton.setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
        }
    }

    private fun savePasswordToDatabase(pass: String) {
        if (userId != -1L) {
            userViewModel.getUserById(userId).observe(this, Observer { user ->
                user?.let {
                    val updatedUser = User(
                        id = user.id,
                        firstName = user.firstName,
                        lastName = user.lastName,
                        patronymic = user.patronymic,
                        dateOfBirth = user.dateOfBirth,
                        email = user.email,
                        password = pass,
                        driverLicense = user.driverLicense,
                        sex = user.sex,
                        registration_date = user.registration_date,
                        photo_url = user.photo_url
                    )
                    userViewModel.updateUser(updatedUser)
                }
            })
        }
    }

    private fun validateInput(password1: String, password2: String): Boolean {
        if (password1.isEmpty() || password1.isEmpty()) {
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
        return true
    }
}
