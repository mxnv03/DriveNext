package com.example.drivenext

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.app.DatePickerDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import java.util.Calendar

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_second_step)

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

        // Обработка нажатия на кнопку "Далее"
        nextButton.setOnClickListener {
            val surname = surnameInput.text.toString()
            val name = nameInput.text.toString()
            val patronic = patronicInput.text.toString()
            val birth_date = dateInput.text.toString()

            if (validateInput(surname, name, patronic, birth_date)) {
                startActivity(Intent(this, RegistationThird::class.java))
                finish()
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
