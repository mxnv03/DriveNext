package com.example.drivenext

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.app.DatePickerDialog
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import java.util.Calendar

import com.example.drivenext.viewmodel.UserViewModel
import com.example.drivenext.data.User

class RegistationSecond : BaseActivity() {
    private lateinit var nextButton: LinearLayout
    private lateinit var backButton: ImageView
    private lateinit var surnameInput: EditText
    private lateinit var nameInput: EditText
    private lateinit var patronicInput: EditText
    private lateinit var dateInput: EditText

    private lateinit var errorTextSurname: TextView
    private lateinit var errorTextName: TextView
    private lateinit var errorTextPatronic: TextView
    private lateinit var errorTextDate: TextView

    private lateinit var calendarIcon: ImageView
    private lateinit var userViewModel: UserViewModel
    private var userId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_second_step)

        // Получаем userId из предыдущего экрана
        userId = intent.getLongExtra("USER_ID", -1)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        // Инициализация элементов
        nextButton = findViewById(R.id.button_layout)
        backButton = findViewById(R.id.backButton)
        errorTextSurname = findViewById(R.id.errorTextSurname)
        errorTextName = findViewById(R.id.errorTextName)
        errorTextPatronic = findViewById(R.id.errorTextPatronic)
        errorTextDate = findViewById(R.id.errorTextDate)

        surnameInput = findViewById(R.id.surnameInput)
        nameInput = findViewById(R.id.nameInput)
        patronicInput = findViewById(R.id.patronicInput)
        dateInput = findViewById(R.id.dateInput)

        calendarIcon = findViewById(R.id.calendarIcon)

        // Загружаем пользователя, если ID корректный
        if (userId != -1L) {
            userViewModel.getUserById(userId).observe(this) { user: User? ->
                user?.let {
                    nextButton.setOnClickListener {
                        val surname = surnameInput.text.toString()
                        val name = nameInput.text.toString()
                        val patronic = patronicInput.text.toString()
                        val birthDate = dateInput.text.toString()

                        if (validateInput(surname, name, patronic, birthDate)) {
                            val updatedUser = User(
                                id = user.id,  // ID не меняем
                                firstName = name,
                                lastName = surname,
                                patronymic = patronic,
                                dateOfBirth = birthDate,
                                email = user.email,
                                password = user.password,
                                driverLicense = "",
                                sex =  "male",
                                registration_date = ""
                            )

                            userViewModel.updateUser(updatedUser)
                            val intent =Intent(this, RegistationThird::class.java)
                            intent.putExtra("USER_ID", userId) // Передаём userId
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        }

        backButton.setOnClickListener {
            startActivity(Intent(this, RegistationFirst::class.java))
        }

        calendarIcon.setOnClickListener {
            showDatePicker()
        }
    }

    @SuppressLint("DefaultLocale")
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
            dateInput.setText(formattedDate)
        }, year, month, day)
        datePickerDialog.show()
    }

    private fun validateInput(surname: String, name: String,
                              patronic: String, birth_date: String): Boolean {
        if (surname.isEmpty()) {
            surnameInput.error = "Error"
            errorTextSurname.visibility = View.VISIBLE
            surnameInput.setBackgroundResource(R.drawable.rounded_button_red)
            return false
        } else {
            surnameInput.error = null
            errorTextSurname.visibility = View.GONE
            surnameInput.setBackgroundResource(R.drawable.rounded_button_white_login)
        }

        if (name.isEmpty()) {
            nameInput.error = "Error"
            errorTextName.visibility = View.VISIBLE
            nameInput.setBackgroundResource(R.drawable.rounded_button_red)
            return false
        } else {
            nameInput.error = null
            errorTextName.visibility = View.GONE
            nameInput.setBackgroundResource(R.drawable.rounded_button_white_login)
        }

        if (patronic.isEmpty()) {
            patronicInput.error = "Error"
            errorTextPatronic.visibility = View.VISIBLE
            patronicInput.setBackgroundResource(R.drawable.rounded_button_red)
            return false
        } else {
            patronicInput.error = null
            errorTextPatronic.visibility = View.GONE
            patronicInput.setBackgroundResource(R.drawable.rounded_button_white_login)
        }

        if (birth_date.isEmpty()) {
            dateInput.error = "Error"
            errorTextDate.visibility = View.VISIBLE
            dateInput.setBackgroundResource(R.drawable.rounded_button_red)
            return false
        } else {
            dateInput.error = null
            errorTextDate.visibility = View.GONE
            dateInput.setBackgroundResource(R.drawable.rounded_button_white_login)
        }

        return true
    }
}
