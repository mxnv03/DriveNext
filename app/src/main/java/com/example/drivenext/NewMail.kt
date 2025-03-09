package com.example.drivenext

import android.content.Intent
import android.os.Bundle
import android.content.SharedPreferences
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import com.example.drivenext.viewmodel.UserViewModel
import com.example.drivenext.data.User

class NewMail : BaseActivity() {
    private lateinit var emailField: EditText
    private lateinit var doneButton: LinearLayout
    private lateinit var backButton: ImageView
    private lateinit var errorText: TextView
    private lateinit var userViewModel: UserViewModel

    private var userId: Long = -1
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_mail)

        // Инициализация элементов
        emailField = findViewById(R.id.editTextText)
        backButton = findViewById(R.id.backButton)
        doneButton = findViewById(R.id.button_layout)
        errorText = findViewById(R.id.errorText)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        userId = sharedPreferences.getLong("user_id", -1)

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

        doneButton.setOnClickListener {
            val email = emailField.text.toString()
            validateEmail()
            if (validateInput(email)) {
                saveEmailToDatabase(email)
                startActivity(Intent(this, AccountActivity::class.java))
                finish()
            }
        }
        backButton.setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
        }
    }

    private fun saveEmailToDatabase(email: String) {
        if (userId != -1L) {
            userViewModel.getUserById(userId).observe(this, Observer { user ->
                user?.let {
                    val updatedUser = User(
                        id = user.id,
                        firstName = user.firstName,
                        lastName = user.lastName,
                        patronymic = user.patronymic,
                        dateOfBirth = user.dateOfBirth,
                        email = email,
                        password = user.password ,
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

    private fun validateInput(email: String): Boolean {
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
        return true
    }

}
